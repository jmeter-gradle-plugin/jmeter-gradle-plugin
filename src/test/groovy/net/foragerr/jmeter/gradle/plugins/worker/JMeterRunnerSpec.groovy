package net.foragerr.jmeter.gradle.plugins.worker

import net.foragerr.jmeter.gradle.plugins.JMSpecs

import spock.lang.Specification

class JMeterRunnerSpec extends Specification {


    JMeterRunner runner

    void setup() {
        runner = new JMeterRunner()
    }

    void 'null JMSpecs do not propogate'() {
        given:
        JMSpecs specs = Mock()
        final String workDir = new File(new File('temp').absolutePath).parent
        final String launchClass = 'FooClass'

        when:
        String[] args = runner.createArgumentList(specs, workDir, launchClass)

        then:
        args[0] == 'java'
        args[1] == '-Xmx64'
        args[2] == '-cp'
        args[4] == 'FooClass'
        1 * specs.getMinHeapSize() >> null
        2 * specs.getMaxHeapSize() >> 64
        1 * specs.getUserSystemProperties() >> []
        1 * specs.getSystemProperties() >> [:]
        1 * specs.getJmeterProperties() >> []
        0 * _
    }
}
