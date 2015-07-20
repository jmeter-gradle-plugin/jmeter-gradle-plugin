package net.foragerr.jmeter.gradle.plugins.utils

import org.apache.tools.ant.DirectoryScanner
import org.gradle.api.Project

/**
 * Created by foragerr@gmail.com on 7/19/2015.
 */
class JMUtils {

    static File getJmeterPropsFile(Project project) {
        File propsInSrcDir = new File(project.jmeter.testFileDir,"jmeter.properties");

        //1. Is jmeterPropertyFile defined?
        if (project.jmeter.jmPropertyFile != null)
            return project.jmeter.jmPropertyFile;

        //2. Does jmeter.properties exist in $srcDir/test/jmeter
        else if (propsInSrcDir.exists())
            return propsInSrcDir;

        //3. If neither, use the default jmeter.properties
        else{
            File defPropsFile = new File(project.jmeter.workDir + System.getProperty("default_jm_properties"));
            return defPropsFile;
        }
    }


    static  List<File> scanDir(Project project, String pattern, File baseDir) {
        List<File> scanResults = new ArrayList<File>()
        DirectoryScanner scanner = new DirectoryScanner()
        scanner.setBasedir(baseDir)
        scanner.setIncludes(pattern)
        scanner.scan()
        for (String result : scanner.getIncludedFiles()) {
            scanResults.add(new File(scanner.getBasedir(), result))
        }
        return scanResults;
    }
}
