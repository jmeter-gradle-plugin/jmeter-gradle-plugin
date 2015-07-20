package net.foragerr.jmeter.gradle.plugins

import kg.apc.jmeter.PluginsCMDWorker
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.jmeter.util.JMeterUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

import javax.xml.transform.TransformerException

public class TaskJMCreateReports extends DefaultTask {

    protected final org.gradle.api.logging.Logger log = Logging.getLogger(getClass())

    public static final List<String> pluginTypes = Arrays.asList(
            "ResponseTimesOverTime",
            "HitsPerSecond",
            "BytesThroughputOverTime",
            "LatenciesOverTime",
            "ResponseCodesPerSecond",
            "TransactionsPerSecond",
            "ResponseTimesDistribution",
            "ResponseTimesPercentiles",
            "ThreadsStateOverTime",
            "TimesVsThreads",
            "ThroughputVsThreads"
    );

    @TaskAction
    jmCreateReport(){
        if (project.jmeter.enableReports == true)makeHTMLReport(project.jmeter.jmResultFiles)
        if (project.jmeter.enableExtendedReports == true)makeHTMLReport(project.jmeter.jmResultFiles)
    }

    public  void createExtendedReport(String resultFile, File jmProps, File jmHome){
        String name = FilenameUtils.removeExtension(resultFile);
        initializeJMeter(name,jmProps,jmHome);

        PluginsCMDWorker worker = new PluginsCMDWorker();
        for (String plugin : pluginTypes) {
            try {
                worker.setPluginType(plugin);
                worker.addExportMode(PluginsCMDWorker.EXPORT_PNG);
                worker.setOutputPNGFile(name + "-" + plugin + ".png");
                worker.addExportMode(PluginsCMDWorker.EXPORT_CSV);
                worker.setOutputCSVFile(name + "-" + plugin + ".csv");
                worker.setInputFile(resultFile);
                worker.doJob();
            } catch (Exception e) {
                log.fatalError("Failed to create report: " + plugin + " for " + name + " due to: ", e);
            }
        }
    }

    private static void initializeJMeter(String name, File jmProps, File jmHome) {
        // Initialize JMeter settings..
        JMeterUtils.setJMeterHome(jmHome.getAbsolutePath());
        JMeterUtils.loadJMeterProperties(jmProps.getAbsolutePath());
        JMeterUtils.setProperty("log_file", name + ".log");
        JMeterUtils.initLogging();
        JMeterUtils.initLocale();
    }

    private void makeHTMLReport(List<String> results) {
        try {
            ReportTransformer transformer;
            transformer = new ReportTransformer(getXslt());
            log.info("Building HTML Report.");
            for (String resultFile : results) {
                final String outputFile = toOutputFileName(resultFile);
                log.info("transforming: " + resultFile + " to " + outputFile);
                transformer.transform(resultFile, outputFile);
            }
        } catch (FileNotFoundException e) {
            log.error("Can't transform result", e);
            throw new GradleException("Error writing report file jmeter file.", e);
        } catch (TransformerException e) {
            log.error("Can't transform result", e);
            throw new GradleException("Error transforming jmeter results", e);
        } catch (IOException e) {
            log.error("Can't transform result", e);
            throw new GradleException("Error copying resources to jmeter results", e);
        }  catch (Exception e) {
            log.error("Can't transform result", e);
        }
    }

    private String toOutputFileName(String fileName) {
        if (fileName.endsWith(".xml")) {
            return fileName.replace(".xml", project.jmeter.reportPostfix);
        } else {
            return fileName + project.jmeter.reportPostfix;
        }
    }

    private void makeExtendedReport(List<String> results) throws IOException {
        for (String resultFile : results) {
            try {
                log.info("Creating Extended Reports");
                TaskJMCreateExReport.createExtendedReport(resultFile, getJmeterPropsFile(), project.jmeter.workDir);
            } catch (Throwable e) {
                log.error("Failed to create extended report for " + resultFile, e);
            }
        }
    }

    private InputStream getXslt() throws IOException {
        if (project.jmeter.reportXslt == null) {
            //if we are using the default report, also copy the images out.
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/collapse.jpg"), new FileOutputStream(project.jmeter.reportDir.getPath() + File.separator + "collapse.jpg"));
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/expand.jpg"), new FileOutputStream(project.jmeter.reportDir.getPath() + File.separator + "expand.jpg"));
            log.debug("Using reports/jmeter-results-detail-report_21.xsl for building report");
            return Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/jmeter-results-detail-report_21.xsl");
        } else {
            log.debug("Using " + project.jmeter.reportXslt + " for building report");
            return new FileInputStream(project.jmeter.reportXslt);
        }
    }
}