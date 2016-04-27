package net.foragerr.jmeter.gradle.plugins

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction;

public class TaskJMClean extends DefaultTask {
	
	protected final Logger log = Logging.getLogger(getClass());

    //TODO should probably do a better job of deleting specific file types
    // instead of deleting the entire directory. This behavior is dangerous when
    // reportDir is set to a pre existing directory  #65

	@TaskAction
    jmClean() throws IOException{
		File reportDir = project.jmeter.reportDir ?: new File(project.getBuildDir(), "jmeter-report")
		log.info("Cleaning out folder: " + reportDir)
		reportDir.deleteDir()
		reportDir.mkdirs()

		//if jmeter log is in custom location, delete that as well
        project.jmeter.jmLog.delete()
    }
}
