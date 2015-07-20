package net.foragerr.jmeter.gradle.plugins


class JMSpecs implements Serializable{

    private String maxHeapSize

    private List<String> jmeterProperties = new ArrayList<>()

    private Map<String, String> systemProperties = new HashMap<>()

    private boolean ignoreFailedTests;

    String getMaxHeapSize() {
        return maxHeapSize
    }

    void setMaxHeapSize(String maxHeapSize) {
        this.maxHeapSize = maxHeapSize
    }

    List<String> getJmeterProperties() {
        return jmeterProperties
    }

    void setJmeterProperties(List<String> jmeterProperties) {
        this.jmeterProperties = jmeterProperties
    }

    boolean getIgnoreFailedTests() {
        return ignoreFailedTests
    }

    void setIgnoreFailedTests(boolean ignoreFailedTests) {
        this.ignoreFailedTests = ignoreFailedTests
    }

    Map<String, String> getSystemProperties() {
        return systemProperties
    }

    void setSystemProperties(Map<String, String> systemProperties) {
        this.systemProperties = systemProperties
    }
}
