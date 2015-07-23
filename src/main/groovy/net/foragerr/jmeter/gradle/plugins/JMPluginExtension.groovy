package net.foragerr.jmeter.gradle.plugins

/**
 * Created by @author foragerr@gmail.com on 7/17/2015.
 */
class JMPluginExtension {

    File jmLog = null
    File testFileDir = null
    File jmPropertyFile = null
    File customReportXslt

    Boolean ignoreErrors = true
    Boolean ignoreFailures = true
    Boolean remote = false
    Boolean enableReports = false
    Boolean enableExtendedReports = true

	List<File> jmTestFiles = null
    List<File> jmUserPropertiesFiles = null
    List<String> jmPluginJars = null
    List<String> jmUserProperties = null

    String jmVersion
    String jmPluginVersion
    String resultFilenameTimestamp
    String reportPostfix
    String reportXslt
    String maxHeapSize
	String reportTitle = null
	
	//For internal use, Not user settable:
	File workDir = null 
	File reportDir = null
	List<File> jmResultFiles = null

}
