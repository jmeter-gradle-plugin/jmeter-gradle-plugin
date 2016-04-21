package net.foragerr.jmeter.gradle.plugins


class JMSpecs implements Serializable{

    private String maxHeapSize
    private String minHeapSize
    private List<String> jmeterProperties = new ArrayList<>()
    private Map<String, String> systemProperties = new HashMap<>()
    private List<String> userSystemProperties = new ArrayList<>()

    List<String> getUserSystemProperties() {
        return userSystemProperties
    }

    void setUserSystemProperties(List<String> userSystemProperties) {
        this.userSystemProperties = userSystemProperties
    }

    String getMaxHeapSize() {
        return maxHeapSize
    }

    void setMaxHeapSize(String maxHeapSize) {
        this.maxHeapSize = maxHeapSize
    }

    String getMinHeapSize() {
        return minHeapSize
    }

    void setMinHeapSize(String minHeapSize) {
        this.minHeapSize = minHeapSize
    }

    List<String> getJmeterProperties() {
        return jmeterProperties
    }

    void setJmeterProperties(List<String> jmeterProperties) {
        this.jmeterProperties = jmeterProperties
    }

    Map<String, String> getSystemProperties() {
        return systemProperties
    }

    void setSystemProperties(Map<String, String> systemProperties) {
        this.systemProperties = systemProperties
    }
}
