package net.foragerr.jmeter.gradle.plugins.utils

import org.gradle.api.GradleException
import java.io.BufferedReader
import java.io.File


class ErrorScanner {
    private static final String PAT_ERROR = "<error>true</error>"
    private static final String PAT_FAILURE_REQUEST = "s=\"false\""
    private static final String PAT_FAILURE = "<failure>true</failure>"

    def ignoreErrors

    def ignoreFailures

    /**
     *
     * @param ignoreErrors
     *            if an error is found with this scanner it will throw an
     *            exception instead of returning true;
     * @param ignoreFailures
     *            if a failure is found with this scanner it will throw an
     *            exception instead of returning true;
     */

    ErrorScanner(def ignoreErrors, def ignoreFailures) {
        this.ignoreErrors = ignoreErrors
        this.ignoreFailures = ignoreFailures
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

    /**
     * protected for testing
     * @param line
     * @return
     */
    protected boolean lineContainsForErrors(String line) {
        if (line.contains(PAT_ERROR)) {
            if (this.ignoreErrors) {
                return true
            } else {
                throw new GradleException("There were test errors.  See the jmeter logs for details.")
            }
        }
        if (line.contains(PAT_FAILURE) || line.contains(PAT_FAILURE_REQUEST)) {
            if (this.ignoreFailures) {
                return true
            } else {
                throw new GradleException("There were test failures.  See the jmeter logs for details.")
            }
        }
        return false
    }

}
