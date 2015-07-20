package net.foragerr.jmeter.gradle.plugins

import net.foragerr.jmeter.gradle.plugins.utils.FileUtils

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction;

public class TaskJMClean extends DefaultTask {
	
	protected final Logger log = Logging.getLogger(getClass());

    /**
     * Directory in which the reports are stored.
     * <p/>
     * By default build/jmeter-report"
     */

	@TaskAction
    jmClean() throws IOException{
        log.info("Cleaning out folder: " + project.jmeter.reportDir)
		project.jmeter.reportDir.deleteDir()
    }
}
