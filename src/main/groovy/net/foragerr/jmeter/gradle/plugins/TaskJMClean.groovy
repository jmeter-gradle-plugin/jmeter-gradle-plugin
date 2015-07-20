package net.foragerr.jmeter.gradle.plugins

import net.foragerr.jmeter.gradle.plugins.helper.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction;

public class TaskJMClean extends DefaultTask {

    /**
     * Directory in which the reports are stored.
     * <p/>
     * By default build/jmeter-report"
     */
    private File reportDir = project.jmeter.reportDir;

    @TaskAction
    jmClean() throws IOException{
        if (!cleanReportDir()){
            throw new IOException("Unable to clean reports");
        }
    }

    private boolean cleanReportDir() {
        return FileUtils.delete(reportDir,  new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        });
    }
}
