package net.foragerr.jmeter.gradle.plugins.utils;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.InputStream;
import java.util.Properties;

public class JMeterPluginProperties {

    private static final Logger log = Logging.getLogger(JMeterPluginProperties.class);
    private static Properties pluginProps;

    public static synchronized Properties getPluginProperties() {
        if (pluginProps == null) {
            try {
                InputStream is = JMeterPluginProperties.class.getClassLoader().getResourceAsStream("jmeter-plugin.properties");
                pluginProps = new Properties();
                pluginProps.load(is);
                return pluginProps;
            } catch (Exception e) {
                log.error("Can't load jmeter-plugin.properties, build will stop", e);
                throw new GradleException("Can't load jmeter-plugin.properties", e);
            }
        }

        return pluginProps;
    }

    public static String getProperty(String name) {
        String value = getPluginProperties().getProperty(name);
        if (value == null) {
            throw new GradleException("Property " + name + " is not set in jmeter-plugin.properties");
        }

        return value;
    }
}
