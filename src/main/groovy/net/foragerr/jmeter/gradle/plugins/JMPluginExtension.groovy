package net.foragerr.jmeter.gradle.plugins

class JMPluginExtension {

    // Test Files //
    File testFileDir = null
    List<File> jmTestFiles = null               //maps to -t, --testfile
    List<String> includes = null
    List<String> excludes = null

    // Jmeter Properties //
    File jmPropertyFile = null                  //maps to -p, --propfile
    File jmAddProp = null                       //maps to -q, --addprop
    List<File> jmSystemPropertiesFiles = null   //maps to -S, --systemPropertyFile
    List<String> jmSystemProperties = null      //maps to -D, --systemproperty
    List<String> jmUserProperties = null        //maps to -J, --jmeterproperty

    // Logs //
    File resultsLog = null                      //maps to -l, --logfile
    File jmLog = null                           //maps to -j, --jmeterlogfile

    // Plugin Options //
    Boolean ignoreErrors = null
    Boolean ignoreFailures = null
    Boolean remote = false
    Boolean csvLogFile = null
    Boolean showSummarizer = null

    // Java Properties //
    String maxHeapSize
    String minHeapSize

    // For internal use, Not user settable //
    String jmVersion
    String jmPluginVersion
    File workDir = null
    File reportDir = null
    List<File> jmResultFiles = null

}
