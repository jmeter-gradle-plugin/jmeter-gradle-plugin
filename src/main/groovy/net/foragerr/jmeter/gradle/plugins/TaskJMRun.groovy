package net.foragerr.jmeter.gradle.plugins

import net.foragerr.jmeter.gradle.plugins.utils.ErrorScanner
import net.foragerr.jmeter.gradle.plugins.utils.JMUtils
import net.foragerr.jmeter.gradle.plugins.worker.JMeterRunner
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

public class TaskJMRun extends DefaultTask {

    protected final Logger log = Logging.getLogger(getClass());

    @TaskAction
    jmRun() {

        //Get List of test files to run
        List<File> testFiles = JMUtils.getListOfTestFiles(project)

        //Run Tests
        List<File> resultList = new ArrayList<File>();
        for (File testFile : testFiles) resultList.add(executeJmeterTest(testFile))

        //Scan for errors
        checkForErrors(resultList);
        project.jmeter.jmResultFiles = resultList;

    }

    private void checkForErrors(List<File> results) {
        ErrorScanner scanner = new ErrorScanner(
                project.jmeter.ignoreErrors,
                project.jmeter.ignoreFailures,
                project.jmeter.failBuildOnError,
                project.jmeter.csvLogFile,
                project.jmeter.errorThreshold)
        try {
            for (File file : results) {
                if (scanner.scanForProblems(file)) {
                    log.warn("There were test errors.  See the jmeter logs for details");

					if (project.jmeter.failBuildOnError) 
						throw new GradleException("Errors during JMeter test");             
                }
            }
        } catch (IOException e) {
            throw new GradleException("Can't read log file", e);
        }
    }

    private File executeJmeterTest(File testFile) {
        try {
            log.info('Executing jMeter test : ' + testFile.getCanonicalPath())
            File resultFile = JMUtils.getResultFile(testFile, project);
            resultFile.delete();

            //Build Jmeter command args
            List<String> args = new ArrayList<String>();
            args.addAll(Arrays.asList("-n",
                    "-t", testFile.getCanonicalPath(),
                    "-l", resultFile.getCanonicalPath(),
                    "-p", JMUtils.getJmeterPropsFile(project).getCanonicalPath()
            ));

            // additional properties from file
            if (project.jmeter.jmAddProp)
                args.addAll(Arrays.asList("-q", project.jmeter.jmAddProp.getCanonicalPath()))

            //User provided sysprops
            List<String> userSysProps = new ArrayList<String>()
            if (project.jmeter.jmSystemPropertiesFiles != null) {
                for (File systemPropertyFile : project.jmeter.jmSystemPropertiesFiles) {
                    if (systemPropertyFile.exists() && systemPropertyFile.isFile()) {
                        args.addAll(Arrays.asList("-S", systemPropertyFile.getCanonicalPath()));
                    }
                }
            }

            // jmSystemProperties
            if (project.jmeter.jmSystemProperties != null) {
                for (String systemProperty : project.jmeter.jmSystemProperties) {
                    userSysProps.addAll(Arrays.asList(systemProperty));
                    log.info(systemProperty);
                }
            }

            //jmUserProperties
            if (project.jmeter.jmUserProperties != null) {
                project.jmeter.jmUserProperties.each { property -> args.add("-J" + property) }
            }

            //jmGlobalProperties
            if (project.jmeter.jmGlobalProperties != null) {
                project.jmeter.jmGlobalProperties.each { property -> args.add("-G" + property) }
            }

            if (project.jmeter.remote) {
                args.add("-r");
            }

            log.info("JMeter is called with the following command line arguments: " + args.toString());
            JMSpecs specs = new JMSpecs();
            specs.getUserSystemProperties().addAll(userSysProps);
            specs.getSystemProperties().put("search_paths", System.getProperty("search_paths"));
            specs.getSystemProperties().put("jmeter.home", project.jmeter.workDir.getAbsolutePath());
            specs.getSystemProperties().put("saveservice_properties", System.getProperty("saveservice_properties"));
            specs.getSystemProperties().put("upgrade_properties", System.getProperty("upgrade_properties"));
            specs.getSystemProperties().put("log_file", project.jmeter.jmLog);

            if ( project.jmeter.csvLogFile == true) 
            	specs.getSystemProperties().put("jmeter.save.saveservice.output_format", "csv");
            else 
            	specs.getSystemProperties().put("jmeter.save.saveservice.output_format", "xml");

            //enable summarizer
            if (project.jmeter.showSummarizer == true){
                specs.getSystemProperties().put('summariser.name','summary')
                specs.getSystemProperties().put('summariser.interval','30')
                specs.getSystemProperties().put('summariser.log','true')
                specs.getSystemProperties().put('summariser.out','true')
            }

            specs.getJmeterProperties().addAll(args);
            specs.setMaxHeapSize(project.jmeter.maxHeapSize.toString());
            specs.setMinHeapSize(project.jmeter.minHeapSize.toString());
            new JMeterRunner().executeJmeterCommand(specs, project.jmeter.workDir.getAbsolutePath());
            return resultFile;
        } catch (IOException e) {
            throw new GradleException("Can't execute test", e);
        }
    }
}
