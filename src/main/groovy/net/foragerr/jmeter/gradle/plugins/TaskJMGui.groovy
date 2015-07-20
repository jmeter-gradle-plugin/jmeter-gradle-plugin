package net.foragerr.jmeter.gradle.plugins

import net.foragerr.jmeter.gradle.plugins.utils.JMUtils;
import net.foragerr.jmeter.gradle.plugins.worker.JMeterRunner

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

class TaskJMGui extends DefaultTask{

    protected final Logger log = Logging.getLogger(getClass());
    private List<File> jmeterUserPropertiesFiles = project.jmeter.jmUserPropertiesFiles;

    @TaskAction
    jmGui() throws IOException{
        try {
            List<String> args = new ArrayList<String>();
            args.addAll(Arrays.asList(
                    "-p", JMUtils.getJmeterPropsFile(project).getCanonicalPath()));

            if(jmeterUserPropertiesFiles!=null)
            {
                for(File userPropertyFile: jmeterUserPropertiesFiles)
                {
                    if(userPropertyFile.exists() && userPropertyFile.isFile())
                    {
                        args.addAll(Arrays.asList("-S", userPropertyFile.getCanonicalPath()));
                    }
                }
            }

            initUserProperties(args);

            log.debug("JMeter is called with the following command line arguments: " + args.toString());

            JMSpecs specs = new JMSpecs();
            specs.getSystemProperties().put("search_paths", System.getProperty("search_paths"));
            specs.getSystemProperties().put("jmeter.home", project.jmeter.workDir.getAbsolutePath());
            specs.getSystemProperties().put("saveservice_properties", System.getProperty("saveservice_properties"));
            specs.getSystemProperties().put("upgrade_properties", System.getProperty("upgrade_properties"));
            specs.getSystemProperties().put("log_file", project.jmeter.jmLog);
            specs.getJmeterProperties().addAll(args);
            specs.setMaxHeapSize(project.jmeter.maxHeapSize.toString());
            new JMeterRunner().executeJmeterCommand(specs, project.jmeter.workDir.getAbsolutePath());


        } catch (IOException e) {
            throw new GradleException("Error Executing Test", e);
        }
    }


    //TODO should probably also be in JMUtils
    private void initUserProperties(List<String> jmeterArgs) {
        if (project.jmeter.jmUserProperties != null) {
            project.jmeter.jmUserProperties.each { property -> jmeterArgs.add("-J" + property) }
        }
    }
}
