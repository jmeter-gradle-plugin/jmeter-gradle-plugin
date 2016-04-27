package net.foragerr.jmeter.gradle.plugins

import org.apache.commons.io.IOUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

class TaskJMInit extends DefaultTask {

    protected final Logger log = Logging.getLogger(getClass());

    private String thisPluginVersion
    private String jmeterVersion
    private String jmeterPluginsVersion

    @TaskAction
    jmInit() {

        //Init plugin settings
        project.jmeter.maxHeapSize = project.jmeter.maxHeapSize ?: "512M"
        project.jmeter.minHeapSize = project.jmeter.minHeapSize ?: "512M"
        project.jmeter.reportPostfix = ""

        File buildDir = project.getBuildDir()
        File workDir = new File(buildDir, "jmeter")
        project.jmeter.workDir = workDir

        project.jmeter.reportDir = project.jmeter.reportDir ?: new File(buildDir, "jmeter-report")

        project.jmeter.jmLog = project.jmeter.jmLog ?:  new File(project.jmeter.reportDir, "jmeter.log")

        project.jmeter.testFileDir = project.jmeter.testFileDir == null ? new File(project.getProjectDir(), "src/test/jmeter") : project.jmeter.testFileDir;

        project.jmeter.ignoreErrors = project.jmeter.ignoreErrors == null ? true : project.jmeter.ignoreErrors
        project.jmeter.ignoreFailures = project.jmeter.ignoreFailures == null ? true : project.jmeter.ignoreFailures
        project.jmeter.enableReports = project.jmeter.enableReports == null ? false : project.jmeter.enableReports
        project.jmeter.enableExtendedReports = project.jmeter.enableExtendedReports == null ? true : project.jmeter.enableExtendedReports
        LoadPluginProperties()
        project.jmeter.jmVersion = this.jmeterVersion

        //Create required folders
        def jmeterJUnitFolder = new File(workDir, "lib/junit")
        jmeterJUnitFolder.mkdirs()

        def jmeterExtFolder = new File(workDir, "lib/ext")
        jmeterExtFolder.mkdirs()
        project.jmeter.reportDir.mkdirs()

        initTempProperties()
        resolveJmeterSearchPath()

        //print version info
        log.info("------------------------")
        log.info("Using")
        log.info("   jmeter-gradle-plugin version:" + this.thisPluginVersion);
        log.info("   jmeter version:" + this.jmeterVersion);
        log.info("   jmeter jp@gc plugins version:" + this.jmeterPluginsVersion);
        log.info("------------------------")
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
        URL[] classPath = ((URLClassLoader) this.getClass().getClassLoader()).getURLs()
        String jmeterVersionPattern = project.jmeter.jmVersion.replaceAll("[.]", "[.]")
        String pathSeparator = ';'; //intentionally not File.PathSeparator - JMeter parses for ; on all platforms
        for (URL dep : classPath) {
            if (dep.getPath().matches("^.*org[./]apache[./]jmeter[/]ApacheJMeter.*" +
                    jmeterVersionPattern + ".jar\$")) {
                cp.append(dep.getPath())
                cp.append(pathSeparator)
            } else if (dep.getPath().matches("^.*bsh.*[.]jar\$")) {
                cp.append(dep.getPath())
                cp.append(pathSeparator)
                //add jp@gc plugins to search_path
            } else if (dep.getPath().matches("^.*jmeter-plugins.*\$")) {
                cp.append(dep.getPath())
                cp.append(pathSeparator)
            }
        }
        cp.append(new File(project.jmeter.workDir, "lib" + File.separator + "ext").getCanonicalPath())
        System.setProperty("search_paths", cp.toString());
        log.debug("Search path is set to " + System.getProperty("search_paths"))
    }

    private void LoadPluginProperties() {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("jmeter-plugin.properties")
            if (is == null) {
                log.error("Error fetching jmeter version")
                throw new GradleException("Error fetching jmeter version")
            }
            Properties pluginProps = new Properties()
            pluginProps.load(is)

            this.thisPluginVersion = pluginProps.getProperty("thisPlugin.version")
            this.jmeterVersion = pluginProps.getProperty("jmeter.version")
            this.jmeterPluginsVersion = pluginProps.getProperty("plugin.version")
        } catch (Exception e) {
            log.error("Can't load JMeter version, build will stop", e)
            throw new GradleException("Can't load JMeter version, build will stop", e)
        }
    }
}
