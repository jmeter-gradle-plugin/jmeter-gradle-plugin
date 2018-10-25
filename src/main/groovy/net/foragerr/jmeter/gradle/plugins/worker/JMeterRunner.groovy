package net.foragerr.jmeter.gradle.plugins.worker

import groovy.io.FileType
import net.foragerr.jmeter.gradle.plugins.JMSpecs
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.internal.os.OperatingSystem

import java.util.jar.Attributes
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

class JMeterRunner {

    private final static Logger LOGGER = Logging.getLogger(JMeterRunner.class)

    private void launchProcess(ProcessBuilder processBuilder, String workingDirectory) {
        processBuilder.redirectErrorStream(true)
        processBuilder.directory(new File(workingDirectory))
        Process p = processBuilder.start()
        p.inputStream.eachLine {println it}
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
     * To get a list of the contents of the pather.jar:
     * `unzip -p build/jmeter/pather.jar META-INF/MANIFEST.MF | sed -e 's/^ //' -e 's/  $//' | tr -d '\r\n' | tr ' ' '\n'`
     *
     * @param workDir working directory of executed build
    */
    private File generatePatherJar(String workDir){
        File patherJar = new File(new File(workDir), "pather.jar")
        if (patherJar.exists()) patherJar.delete()
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        StringBuilder cpBuilder = new StringBuilder();

        //add from jmeter/lib
        new File(workDir, "lib").eachFileRecurse(FileType.FILES){ file ->
            cpBuilder.append(file.toURI())
            cpBuilder.append(" ")
        }
        //add from jmeter/lib/ext
        new File(workDir, "lib/ext").eachFileRecurse(FileType.FILES){ file ->
            cpBuilder.append(file.toURI())
            cpBuilder.append(" ")
        }

        List<URL> classPath = ((URLClassLoader)this.getClass().getClassLoader()).getURLs() as List

        // openjfx for non-Oracle JDK
        def openjfxPattern = ~/\/javafx-.*\.jar/
        def openjfxOSPattern = ~/\/javafx-.*-${operatingSystemClassifier()}\.jar/
        classPath.removeIf { URL url ->
            String file = url.getFile()
            file.find(openjfxPattern) && !file.find(openjfxOSPattern)
        }

        classPath.each {u ->
            cpBuilder.append(u.getPath())
            cpBuilder.append(" ")
        }
        manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, cpBuilder.substring(0, cpBuilder.size() - 1) )
        JarOutputStream target = new JarOutputStream(new FileOutputStream(patherJar.getCanonicalPath()), manifest);
        target.close();
        return patherJar
    }

    private String operatingSystemClassifier() {
        String platform = 'unsupported'
        int javaMajorVersion = System.properties['java.runtime.version'].split('[^0-9]+')[0] as int
        if (javaMajorVersion < 11) {
            return platform
        }
        OperatingSystem currentOS = org.gradle.internal.os.OperatingSystem.current()
        if (currentOS.isWindows()) {
            platform = 'win'
        } else if (currentOS.isLinux()) {
            platform = 'linux'
        } else if (currentOS.isMacOsX()) {
            platform = 'mac'
        }
        platform
    }
}
