package net.foragerr.jmeter.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

/**
 * Created by @author foragerr@gmail.com on 7/17/2015.
 */
class JMPlugin implements Plugin<Project>{

    static final String TASK_GROUP_NAME = 'JMeter'
    protected final Logger log = Logging.getLogger(getClass());

    void apply(Project project){
        project.extensions.create("jmeter", JMPluginExtension)

        project.task('jmInit', type:TaskJMInit){
            group null //hide this task
            description 'Init task - pointless to run by itself'
        }

        project.task('jmRun', type:TaskJMRun, dependsOn: 'jmInit'){
            group TASK_GROUP_NAME
            description 'Execute JMeter Tests'
        }

        project.task('jmGui', type:TaskJMGui, dependsOn: 'jmInit'){
            group TASK_GROUP_NAME
            description 'Launch JMeter GUI to edit tests'
        }

        project.task('jmReport', type:TaskJMReports, dependsOn: 'jmInit'){
            group TASK_GROUP_NAME
            description 'Create JMeter test Reports'
        }

        project.task('jmClean', type:TaskJMClean){
            group TASK_GROUP_NAME
            description 'Clean JMeter test Reports'
        }
    }
}
