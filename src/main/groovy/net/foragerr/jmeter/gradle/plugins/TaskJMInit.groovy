package net.foragerr.jmeter.gradle.plugins

import groovy.util.logging.Log4j
import org.apache.commons.io.IOUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

/**
 * Created by foragerr@gmail.com on 7/17/2015.
 */
class TaskJMInit extends DefaultTask{

    protected final Logger log = Logging.getLogger(getClass());

    @TaskAction
    jmInit(){

        project.jmeter.maxHeapSize = "512M"
        project.jmeter.reportPostfix = ""

        //Init plugin settings
        File buildDir = project.getBuildDir()
        File workDir = new File (buildDir , "jmeter")
        project.jmeter.workDir = workDir

        File reportDir = new File(buildDir, project.jmeter.reportDir ?: "jmeter-report")
        project.jmeter.reportDir = reportDir

        File jmLog =  new File(reportDir, project.jmeter.jmLog ?: "jmeter.log")
        project.jmeter.jmLog =  jmLog

        File testFileDir =  new File(project.getProjectDir(), "src/test/jmeter");
        project.jmeter.testFileDir =  testFileDir
		
		project.jmeter.enableReports = project.jmeter.enableReports==null ? false : project.jmeter.enableReports
        project.jmeter.enableExtendedReports = project.jmeter.enableExtendedReports==null ? true : project.jmeter.enableExtendedReports
        project.jmeter.jmVersion = loadJMeterVersion();

        //Create required folders
        def jmeterJUnitFolder = new File(workDir, "lib/junit")
        jmeterJUnitFolder.mkdirs()

        def jmeterExtFolder = new File(workDir, "lib/ext")
        jmeterExtFolder.mkdirs()
        reportDir.mkdirs()

        initTempProperties()
        resolveJmeterSearchPath()

    }

    protected void initTempProperties() throws IOException {
        List<File> tempProperties = new ArrayList<File>();

        File saveServiceProperties = new File(project.jmeter.workDir, "saveservice.properties");
        System.setProperty("saveservice_properties", "/" + saveServiceProperties.getName());
        tempProperties.add(saveServiceProperties);
        log.debug("saveservice_properties location is " + System.getProperty("saveservice_properties"))

        File upgradeProperties = new File(project.jmeter.workDir, "upgrade.properties");
        System.setProperty("upgrade_properties", "/" + upgradeProperties.getName());
        tempProperties.add(upgradeProperties);

        File defaultJmeterProperties = new File(project.jmeter.workDir, "jmeter.properties");
        System.setProperty("default_jm_properties", "/" + defaultJmeterProperties.getName());
        tempProperties.add(defaultJmeterProperties);
		
		File jmPluginProperties = new File(project.jmeter.workDir, "jmeter-plugin.properties");
		System.setProperty("default_jm_properties", "/" + jmPluginProperties.getName());
		tempProperties.add(jmPluginProperties);

		//Copy files from jar to workDir
        for (File f : tempProperties) {
            try {
                FileWriter writer = new FileWriter(f);
                IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(f.getName()), writer);
                writer.flush();
                writer.close();
            } catch (IOException ioe) {
                throw new GradleException("Couldn't create temporary property file " + f.getName() + " in directory " + workDir.getPath(), ioe);
            }

        }
    }

    protected void resolveJmeterSearchPath() {
        StringBuilder cp = new StringBuilder()
        URL[] classPath = ((URLClassLoader)this.getClass().getClassLoader()).getURLs()
        String jmeterVersionPattern = project.jmeter.jmVersion.replaceAll("[.]", "[.]")
        String pathSeparator = File.pathSeparator;
        for (URL dep : classPath) {
            if (dep.getPath().matches("^.*org[./]apache[./]jmeter[/]ApacheJMeter.*" +
                    jmeterVersionPattern + ".jar\$")) {
                cp.append(dep.getPath())
                cp.append(pathSeparator)
            } else if (dep.getPath().matches("^.*bsh.*[.]jar\$")) {
                cp.append(dep.getPath())
                cp.append(pathSeparator)
            } else if (project.jmeter.jmPluginJars != null){
                for (String plugin: project.jmeter.jmPluginJars) {
                    if(dep.getPath().matches("^.*" + plugin + "\$")) {
                        cp.append(dep.getPath())
                        cp.append(pathSeparator)
                    }
                }
            }
        }
        cp.append(new File(project.jmeter.workDir, "lib" + File.separator + "ext").getCanonicalPath())
        System.setProperty("search_paths", cp.toString());
        log.debug("Search path is set to " + System.getProperty("search_paths"))
    }

    private String loadJMeterVersion() {
        String jmeterVersion
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("jmeter-plugin.properties")
            if (is==null) {
                log.error ("Error fetching jmeter version")
                throw new GradleException("Error fetching jmeter version")
            }
            Properties pluginProps = new Properties()
            pluginProps.load(is)
            jmeterVersion = pluginProps.getProperty("jmeter.version", null)
            if (jmeterVersion == null) {
                throw new GradleException("You should set correct jmeter.version at jmeter-plugin.properies file")
            }
        } catch (Exception e) {
            log.error("Can't load JMeter version, build will stop", e)
            throw new GradleException("Can't load JMeter version, build will stop", e)
        }

        return jmeterVersion;
    }

}
