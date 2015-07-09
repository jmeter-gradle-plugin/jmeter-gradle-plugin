package net.foragerr.gradle.plugins.jmeter;

import java.util.Arrays;
import java.util.List;

import kg.apc.jmeter.PluginsCMDWorker;

import org.apache.commons.io.FilenameUtils;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;


/**
 * A Simple wrapper around the reporter tool that will generate the graphs for all the plugin types.
 * It will configure the reporter tool so that it generates a csv and png.
 * 
 * The {@link JmeterRunTask} will invoke this as a separate process with the proper path configured.
 */
public class CreateReport {
    private static final Logger log = LoggingManager.getLoggerForClass();
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

    public static void main(String args[]) {
        String resultFile = args[0];
        String name = FilenameUtils.removeExtension(resultFile);
        initializeJMeter(name);

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

    private static void initializeJMeter(String name) {
        // Initialize the JMeter settings..
        JMeterUtils.setJMeterHome(System.getProperty("jmeter.home"));
        JMeterUtils.loadJMeterProperties(System.getProperty("jmeter.properties"));
        JMeterUtils.setProperty("log_file", name + ".log");
        JMeterUtils.initLogging();
        JMeterUtils.initLocale();
    }
}