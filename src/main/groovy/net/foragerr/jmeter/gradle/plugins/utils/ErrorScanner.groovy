package net.foragerr.jmeter.gradle.plugins.utils

import org.gradle.api.GradleException
import java.io.BufferedReader
import java.io.File


class ErrorScanner {
    private static final String PAT_ERROR = "<error>true</error>"
    private static final String PAT_FAILURE_REQUEST = "s=\"false\""
    private static final String PAT_FAILURE = "<failure>true</failure>"
    private static final String PAT_FALSE = ",false,"
    
    def ignoreErrors
    def ignoreFailures
    def failBuildOnError

    ErrorScanner(def ignoreErrors, def ignoreFailures, def failBuildOnError) {
        this.ignoreErrors = ignoreErrors
        this.ignoreFailures = ignoreFailures
        this.failBuildOnError = failBuildOnError
    }

    public boolean scanForProblems(File file) throws IOException {
        def result = false;
        file.eachLine {line ->
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
