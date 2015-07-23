package net.foragerr.jmeter.gradle.plugins

import net.foragerr.jmeter.gradle.plugins.utils.ErrorScanner;
import net.foragerr.jmeter.gradle.plugins.utils.JMUtils;
import net.foragerr.jmeter.gradle.plugins.worker.JMeterRunner

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

import java.text.DateFormat
import java.text.SimpleDateFormat

public class TaskJMRun extends DefaultTask {

    protected final Logger log = Logging.getLogger(getClass());

    @TaskAction
    jmRun(){

         //Get List of test files to run
        List<File> testFiles = new ArrayList<File>();
        if (project.jmeter.jmTestFiles != null) {
            project.jmeter.jmTestFiles.each { File file ->
                if (file.exists() && file.isFile()) {
                    testFiles.add(file);
                } else {
                    throw new GradleException("Test file " + file.getCanonicalPath() + " does not exists");
                }
            }
        } else {
            testFiles.addAll(JMUtils.scanDir(project, "**/*.jmx", project.jmeter.testFileDir));
            log.info(testFiles.size() + " test files found in folder scan")
        }

        //Run Tests
        List<File> resultList = new ArrayList<File>();
        for (File testFile : testFiles) resultList.add(executeJmeterTest(testFile))


        //Scan for errors
        checkForErrors(resultList);

        project.jmeter.jmResultFiles=resultList;

    }

    private void checkForErrors(List<File> results) {
        ErrorScanner scanner = new ErrorScanner(project.jmeter.ignoreErrors, project.jmeter.ignoreFailures);
        try {
            for (File file : results) {
                if (scanner.scanForProblems(file)) {
                    log.warn("There were test errors.  See the jmeter logs for details");
                }
            }
        } catch (IOException e) {
            throw new GradleException("Can't read log file", e);
        }
    }

    private File executeJmeterTest(File testFile) {
        try {
            log.info('Executing jMeter test : ' + testFile.getCanonicalPath())
            File resultFile = getResultFile(testFile);
            resultFile.delete();

            //Build Jmeter command args
            List<String> args = new ArrayList<String>();
            args.addAll(Arrays.asList("-n",
                    "-t", testFile.getCanonicalPath(),
                    "-l", resultFile.getCanonicalPath(),
                    "-p", JMUtils.getJmeterPropsFile(project).getCanonicalPath()
            ));

            if(project.jmeter.jmUserPropertiesFiles!=null)
            {
                for(File userPropertyFile: project.jmeter.jmUserPropertiesFiles)
                {
                    if(userPropertyFile.exists() && userPropertyFile.isFile())
                    {
                        args.addAll(Arrays.asList("-S", userPropertyFile.getCanonicalPath()));
                    }
                }
            }

            initUserProperties(args);

            if (project.jmeter.remote) {
                args.add("-r");
            }

            log.info("JMeter is called with the following command line arguments: " + args.toString());
            JMSpecs specs = new JMSpecs();
            specs.getSystemProperties().put("search_paths", System.getProperty("search_paths"));
            specs.getSystemProperties().put("jmeter.home", project.jmeter.workDir.getAbsolutePath());
            specs.getSystemProperties().put("saveservice_properties", System.getProperty("saveservice_properties"));
            specs.getSystemProperties().put("upgrade_properties", System.getProperty("upgrade_properties"));
            specs.getSystemProperties().put("log_file", project.jmeter.jmLog);
            specs.getSystemProperties().put("jmeter.save.saveservice.output_format","xml");
            specs.getJmeterProperties().addAll(args);
            specs.setMaxHeapSize(project.jmeter.maxHeapSize.toString());
            new JMeterRunner().executeJmeterCommand(specs, project.jmeter.workDir.getAbsolutePath());
            return resultFile;
        } catch (IOException e) {
            throw new GradleException("Can't execute test", e);
        }
    }

    private void initUserProperties(List<String> jmeterArgs) {
        if (project.jmeter.jmUserProperties != null) {
            project.jmeter.jmUserProperties.each { property -> jmeterArgs.add("-J" + property) }
        }
    }

    private File getResultFile(File testFile) {

        DateFormat fmt = new SimpleDateFormat("yyyyMMdd-HHmm");
        if (project.jmeter.resultFilenameTimestamp==null)
            return new File(project.jmeter.reportDir, testFile.getName() + "-" + fmt.format(new Date()) + ".xml");

        //if resultFilenameTimestamp is "none" do not use a timestamp in filename
        if (project.jmeter.resultFilenameTimestamp.equals("none"))
            return new File(project.jmeter.reportDir, testFile.getName() + ".xml");

        //else if resultFilenameTimestamp is "useSaveServiceFormat" use saveservice.format
        if (project.jmeter.resultFilenameTimestamp.equals("useSaveServiceFormat")){
            String saveServiceFormat =  System.getProperty("jmeter.save.saveservice.timestamp_format");
            if (saveServiceFormat.equals("none")) return new File(project.jmeter.reportDir, testFile.getName() + ".xml");
            try
            {
                fmt = new SimpleDateFormat(saveServiceFormat);
                return new File(project.jmeter.reportDir, testFile.getName() + "-" + fmt.format(new Date()) + ".xml");
            }
            catch (Exception e)
            {
                // jmeter.save.saveservice.timestamp_format does not contain a valid format
                log.warn("jmeter.save.saveservice.timestamp_format Not defined, using default timestamp format");
            }
        }

        //for all other unhandled conditions fallback to default:
        fmt = new SimpleDateFormat("yyyyMMdd-HHmm");
        return new File(reportDir, testFile.getName() + "-" + fmt.format(new Date()) + ".xml");
    }



}
