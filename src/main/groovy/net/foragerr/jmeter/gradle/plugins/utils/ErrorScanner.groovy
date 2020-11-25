package net.foragerr.jmeter.gradle.plugins.utils

import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

class ErrorScanner {
    private static final String CSV_PAT_HEADER = ",success,"
    private static final String PAT_ERROR = "<error>true</error>"
    private static final String PAT_FAILURE = "<failure>true</failure>"
    private static final String PAT_FALSE = ",false,"
    private static final String LINE_SEPARATOR = "-------------------------------------------------------";

    private final Logger log = Logging.getLogger(getClass())
    def ignoreErrors
    def ignoreFailures
    def failBuildOnError
    def csvLogFile
    def errorThreshold

    ErrorScanner(def ignoreErrors, def ignoreFailures, def failBuildOnError, def csvLogFile, def errorThreshold) {
        this.ignoreErrors = ignoreErrors
        this.ignoreFailures = ignoreFailures
        this.failBuildOnError = failBuildOnError
        this.csvLogFile = csvLogFile
        this.errorThreshold = errorThreshold
    }

    public boolean scanForProblems(File file) {
        if (this.errorThreshold == 0.0) {
            return checkForFirstError(file)
        } else {
            return checkThreshold(file)
        }
    }

    private boolean checkThreshold(File file) {
        int totalTestCount = 0
        int countTestFailures = 0
        if (this.csvLogFile == true) {
            file.eachLine { line ->
                if (!line.contains(CSV_PAT_HEADER)) { // Ignore 1st line in CSV
                    totalTestCount++
                }
                if (line.contains(PAT_FALSE)) {
                    countTestFailures++
                }
            }
        } else {
            def testResults = (new XmlParser()).parse(file).value()
            testResults.each {
                httpSample ->
                    boolean isThereAFailure = false
                    httpSample.assertionResult.each {
                        assertionResult ->
                            if (assertionResult.error[0].text() == 'true' ||
                                    assertionResult.failure[0].text() == 'true') {
                                isThereAFailure = true
                            }
                    }
                    totalTestCount++
                    if (isThereAFailure) {
                        countTestFailures++
                    }
            }
        }
        log.info(LINE_SEPARATOR)
        log.info("P E R F O R M A N C E    T E S T    R E S U L T S")
        log.info(LINE_SEPARATOR)
        log.info("Total requests:\t\t\t{}", totalTestCount)
        log.info("Failed requests:\t\t{}", countTestFailures)
        double failurePercentage = (countTestFailures * 100.0) / totalTestCount
        log.info("Failures:\t\t\t{}% ({}% accepted)", failurePercentage, errorThreshold)
        log.info(LINE_SEPARATOR)
        if (failurePercentage <= this.errorThreshold) {
            return false
        }
        throw new GradleException(
                String.format("Failing build because error percentage %s is above accepted threshold %s",
                        failurePercentage, errorThreshold))
    }


    private boolean checkForFirstError(File file) {
        def result = false
        file.eachLine { line ->
            def lineResult = lineContainsForErrors(line)
            if (!result) {
                result = lineResult
            }
        }
        return result
    }

    protected boolean lineContainsForErrors(String line) {
    
        if (line.contains(PAT_FALSE)) {
            if (failBuildOnError) {
                throw new GradleException("There were test errors.  See the jmeter logs for details.")
            } else {
                return true
            }
        }

        if (line.contains(PAT_ERROR)) {
            if (this.ignoreErrors) {
                return true
            } else {
                throw new GradleException("There were test errors.  See the jmeter logs for details.")
            }
        }
        if (line.contains(PAT_FAILURE)) {
            if (this.ignoreFailures) {
                return true
            } else {
                throw new GradleException("There were test failures.  See the jmeter logs for details.")
            }
        }
       
        return false
    }

}
