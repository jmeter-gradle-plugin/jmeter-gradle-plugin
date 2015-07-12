package net.foragerr.jmeter.gradle.plugins

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.Convention

import java.util.concurrent.Callable

public class JmeterPlugin implements Plugin<Project> {

    public static final String JMETER_RUN = "jmeterRun";

    public static final String JMETER_RUN_GUI = "jmeterEditor";

    public static final String JMETER_LIST = "jmeterListTestPlan";

    public static final String JMETER_CLEAN = "jmeterCleanReport";

    public static final String JMETER_PLUGIN_NAME = "jmeter";

    public static final String PERFORMANCE_GROUP = "performance";

    public void apply(final Project project) {
        final JmeterPluginConvention jmeterConvention = new JmeterPluginConvention(project);

        Convention convention = project.getConvention();
        convention.getPlugins().put(JMETER_PLUGIN_NAME, jmeterConvention);

        JmeterRunGuiTask runGuiTask = project.getTasks().create(JMETER_RUN_GUI, JmeterRunGuiTask.class, new Action<JmeterRunGuiTask>() {
            @Override
            public void execute(JmeterRunGuiTask jmeterRunGuiTask) {
                configureJmeterAbstractTask(project, jmeterConvention, jmeterRunGuiTask);
                configureJmeterRunGuiTask(project, jmeterConvention, jmeterRunGuiTask);
            }
        });
        runGuiTask.setDescription("Start JMeter GUI");
        runGuiTask.setGroup(PERFORMANCE_GROUP);

        JmeterRunTask runTask = project.getTasks().create(JMETER_RUN, JmeterRunTask.class, new Action<JmeterRunTask>() {

            @Override
            public void execute(JmeterRunTask jmeterRunTask) {
                configureJmeterTask(project, jmeterConvention, jmeterRunTask);
            }
        });
        runTask.setDescription("Execute JMeter tests");
        runTask.setGroup(PERFORMANCE_GROUP);

        JmeterListTestPlanTask listTestPlanTask = project.getTasks().create(JMETER_LIST, JmeterListTestPlanTask.class, new Action<JmeterListTestPlanTask>() {

            @Override
            public void execute(JmeterListTestPlanTask jmeterListTestPlanTask) {
                configureJmeterListTestPlanTask(project, jmeterConvention, jmeterListTestPlanTask);
            }
        });
        listTestPlanTask.setDescription("List JMeter test plan");
        listTestPlanTask.setGroup(PERFORMANCE_GROUP);

        JmeterCleanReportTask cleanReportTask = project.getTasks().create(JMETER_CLEAN, JmeterCleanReportTask.class, new Action<JmeterCleanReportTask>() {

            @Override
            public void execute(JmeterCleanReportTask jmeterCleanReportTask) {
                configureJmeterCleanReportTask(project, jmeterConvention, jmeterCleanReportTask);
            }
        });
        cleanReportTask.setDescription("Clean Jmeter Report");
        cleanReportTask.setGroup(PERFORMANCE_GROUP);
    }

    private void configureJmeterTask(Project project, final JmeterPluginConvention jmeterConvention, JmeterRunTask jmeterRunTask) {
        configureJmeterAbstractTask(project, jmeterConvention, jmeterRunTask);
        jmeterRunTask.getConventionMapping().map("jmeterTestFiles", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterTestFiles();
            }
        });

        jmeterRunTask.getConventionMapping().map("includes", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getIncludes();
            }
        });

        jmeterRunTask.getConventionMapping().map("excludes", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getExcludes();
            }
        });

        jmeterRunTask.getConventionMapping().map("reportDir", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getReportDir();
            }
        });

        jmeterRunTask.getConventionMapping().map("enableReports", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getEnableReports();
            }
        });

        jmeterRunTask.getConventionMapping().map("enableExtendedReports", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getEnableExtendedReports();
            }
        });

        jmeterRunTask.getConventionMapping().map("remote", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getRemote();
            }
        });

        jmeterRunTask.getConventionMapping().map("jmeterIgnoreFailure", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterIgnoreFailure();
            }
        });

        jmeterRunTask.getConventionMapping().map("jmeterIgnoreError", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterIgnoreError();
            }
        });

        jmeterRunTask.getConventionMapping().map("reportPostfix", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getReportPostfix();
            }
        });

        jmeterRunTask.getConventionMapping().map("reportXslt", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getReportXslt();
            }
        });

        jmeterRunTask.getConventionMapping().map("maxHeapSize", new Callable<Object>() {
            @Override
            Object call() throws Exception {
                return jmeterConvention.getMaxHeapSize();
            }
        })
    }

    private void configureJmeterListTestPlanTask(Project project, final JmeterPluginConvention jmeterConvention, JmeterListTestPlanTask jmeterListTestPlanTask) {
        configureJmeterAbstractTask(project, jmeterConvention, jmeterListTestPlanTask);
        jmeterListTestPlanTask.getConventionMapping().map("includes", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getIncludes();
            }
        });

        jmeterListTestPlanTask.getConventionMapping().map("excludes", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getExcludes();
            }
        });
    }

    private void configureJmeterCleanReportTask(Project project, final JmeterPluginConvention jmeterConvention, JmeterCleanReportTask jmeterCleanReportTask) {
        configureJmeterAbstractTask(project, jmeterConvention, jmeterCleanReportTask);
        jmeterCleanReportTask.getConventionMapping().map("reportDir", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getReportDir();
            }
        });
    }

    private void configureJmeterAbstractTask(Project project, final JmeterPluginConvention jmeterConvention, JmeterAbstractTask jmeterRunGuiTask) {

        jmeterRunGuiTask.getConventionMapping().map("srcDir", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getSrcDir();
            }
        });

        jmeterRunGuiTask.getConventionMapping().map("jmeterUserProperties", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterUserProperties();
            }
        });

        jmeterRunGuiTask.getConventionMapping().map("jmeterPluginJars", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterPluginJars();
            }
        });

        jmeterRunGuiTask.getConventionMapping().map("jmeterPropertyFile", new Callable<Object>() {
            public Object call() throws Exception {
                return jmeterConvention.getJmeterPropertyFile();
            }
        });

        jmeterRunGuiTask.getConventionMapping().map("maxHeapSize", new Callable<Object>() {
            @Override
            Object call() throws Exception {
                return jmeterConvention.getMaxHeapSize();
            }
        })

        jmeterRunGuiTask.getConventionMapping().map("jmeterUserPropertiesFiles", new Callable<Object>() {
            @Override
            Object call() throws Exception {
                return jmeterConvention.getJmeterUserPropertiesFiles();
            }
        })
    }

    private void configureJmeterRunGuiTask(Project project, final JmeterPluginConvention jmeterConvention, JmeterRunGuiTask jmeterRunGuiTask) {

        jmeterRunGuiTask.getConventionMapping().map("jmeterEditFile", new Callable<Object>() {
            @Override
            Object call() throws Exception {
                return jmeterConvention.getJmeterEditFile();
            }
        });
    }
}