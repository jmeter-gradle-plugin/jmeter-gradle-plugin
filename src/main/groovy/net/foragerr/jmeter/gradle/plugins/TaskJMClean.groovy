package net.foragerr.jmeter.gradle.plugins

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
		File reportDir = project.jmeter.reportDir ?: new File(project.getBuildDir(), "jmeter-report")
		log.info("Cleaning out folder: " + reportDir)
		reportDir.deleteDir()
		reportDir.mkdirs()
    }
}
