package net.foragerr.jmeter.gradle.plugins

import kg.apc.jmeter.PluginsCMDWorker
import net.foragerr.jmeter.gradle.plugins.utils.JMUtils;
import net.foragerr.jmeter.gradle.plugins.utils.ReportTransformer;

import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.jmeter.util.JMeterUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.Logging
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import javax.xml.transform.TransformerException

public class TaskJMReports extends DefaultTask {

    protected final Logger log = Logging.getLogger(getClass())

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

//	TODO: createReports should only kick-in if there are new jtl files to process.
//	@InputDirectory
//	def File project.jmeter.reportDir

    @TaskAction
    jmCreateReport(){
		//Get List of resultFiles
		List<File> jmResultFiles = new ArrayList<File>()
		jmResultFiles.addAll(JMUtils.scanDir(project, "**/*.xml", project.jmeter.reportDir));
		
		if (jmResultFiles.size()==0) log.warn("There are no results file to create reports from")
		
        if (project.jmeter.enableReports == true)makeHTMLReport(jmResultFiles)
        if (project.jmeter.enableExtendedReports == true) makeExtendedReports(jmResultFiles)
		
    }
	
	private void makeExtendedReports(List<File> results) throws IOException {
		for (File resultFile : results) {
			try {
				log.info("Creating Extended Reports " + resultFile.getName());
				createExtendedReport(resultFile);
			} catch (Throwable e) {
				log.error("Failed to create extended report for " + resultFile, e);
			}
		}
	}

    private  void createExtendedReport(File resultFile){
        String name = FilenameUtils.removeExtension(resultFile.getAbsolutePath());
        initializeJMeter(name, JMUtils.getJmeterPropsFile(project), project.jmeter.workDir);

        PluginsCMDWorker worker = new PluginsCMDWorker();
        for (String plugin : pluginTypes) {
            try {
                worker.setPluginType(plugin);
                worker.addExportMode(PluginsCMDWorker.EXPORT_PNG);
                worker.setOutputPNGFile(name + "-" + plugin + ".png");
                worker.addExportMode(PluginsCMDWorker.EXPORT_CSV);
                worker.setOutputCSVFile(name + "-" + plugin + ".csv");
                worker.setInputFile(resultFile.getAbsolutePath());
                worker.doJob();
            } catch (Exception e) {
                log.error("Failed to create report: " + plugin + " for " + name + " due to: ", e);
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

    private void makeHTMLReport(List<File> results) {
        try {
            ReportTransformer transformer;
            transformer = new ReportTransformer(getXslt());
            log.info("Building HTML Report.");
			for (File resultFile : results) {
				String reportTitle = project.jmeter.reportTitle ?: "Generated from: " + resultFile.getName();
                final File outputFile = new File(toOutputFileName(resultFile.getAbsolutePath()));
                log.info("transforming: " + resultFile + " to " + outputFile);
                transformer.transform(resultFile, outputFile, reportTitle);
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
            return fileName.replace(".xml", project.jmeter.reportPostfix + ".html");
        } else {
            return fileName + project.jmeter.reportPostfix;
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