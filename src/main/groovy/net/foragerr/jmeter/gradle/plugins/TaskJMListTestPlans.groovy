package net.foragerr.jmeter.gradle.plugins;

import org.apache.tools.ant.DirectoryScanner
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction;

public class TaskJMListTestPlans extends DefaultTask {

    @TaskAction
    protected void jmLostTestPlans() throws IOException {
        System.out.println("");
        System.out.println("Jmeter Test Plan");
        System.out.println("----------------");
        List<String> allTestFiles = new ArrayList<String>();
        allTestFiles.addAll(scanSourceFolder());
        if (allTestFiles.size()>0){
            for (String file : allTestFiles) {
                System.out.println(file);
            }
        } else {
            System.out.println("There is no test files in source directory.");
        }
    }

    //---------TODO: MOve to COMMON LIB ---------------------
    private List<String> scanSourceFolder() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(project.jmeter.testFileDir)
        scanner.setIncludes("**/*.jmx")
        scanner.scan();
        return scanner.getIncludedFiles();
    }

}
