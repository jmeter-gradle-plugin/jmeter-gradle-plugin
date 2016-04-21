package net.foragerr.jmeter.gradle.plugins.worker

import net.foragerr.jmeter.gradle.plugins.JMSpecs
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import java.util.jar.Attributes
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

class JMeterRunner {

    private final static Logger LOGGER = Logging.getLogger(JMeterRunner.class)

    private void launchProcess(ProcessBuilder processBuilder, String workingDirectory) {
        processBuilder.redirectErrorStream(true)
        processBuilder.directory(new File(workingDirectory))
        Process p = processBuilder.start()
        int processResult = p.waitFor()
        if (processResult != 0) {
            throw new GradleException("Something went wrong during jmeter test execution, Please see jmeter logs for more information")
        }
    }

    void executeJmeterCommand(JMSpecs specs, String workingDirectory) {
        ProcessBuilder processBuilder = new ProcessBuilder(createArgumentList(specs, workingDirectory, "org.apache.jmeter.NewDriver")).inheritIO()
        launchProcess(processBuilder, workingDirectory);
    }

    private String[] createArgumentList(JMSpecs specs, String workDir, String launchClass) {
        String javaRuntime = "java"

        List<String> argumentsList = new ArrayList<>()
        argumentsList.add(javaRuntime)
        argumentsList.add("-Xms${specs.minHeapSize}".toString())
        argumentsList.add("-Xmx${specs.maxHeapSize}".toString())
        argumentsList.addAll(specs.getUserSystemProperties())
        specs.getSystemProperties().each {k,v ->
            argumentsList.add("-D$k=$v".toString())
        }
        argumentsList.add("-cp")
        argumentsList.add(workDir + File.separator + "lib" + System.getProperty("path.separator") +
            workDir + File.separator + "lib" + File.separator + "ext" + System.getProperty("path.separator") +
            generatePatherJar(workDir).getAbsolutePath())
        argumentsList.add(launchClass)
        argumentsList.addAll(specs.jmeterProperties)
        LOGGER.debug("Command to run is $argumentsList")
        argumentsList.toArray(new String[argumentsList.size()])
    }

    /**
     * As a workaround for the command argument length being too long for Windows, more than 8K chars, generate
     *   a tmp .jar as a path container for the long classpath.
     *
     * @param workDir working directory of executed build
    */
    private File generatePatherJar(String workDir){
        File patherJar = new File(new File(workDir), "pather.jar")
        if (patherJar.exists()) patherJar.delete()
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        StringBuilder cpBuilder = new StringBuilder();
        URL[] classPath = ((URLClassLoader)this.getClass().getClassLoader()).getURLs()
        classPath.each {u ->
            cpBuilder.append(u.getPath())
            cpBuilder.append(" ")
        }
        manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, cpBuilder.substring(0, cpBuilder.size() - 1) )
        JarOutputStream target = new JarOutputStream(new FileOutputStream(patherJar.getCanonicalPath()), manifest);
        target.close();
        return patherJar
    }
}
