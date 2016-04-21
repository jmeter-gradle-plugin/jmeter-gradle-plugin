##Gradle plugin to execute JMeter tests.  [![Build Status](https://travis-ci.org/jmeter-gradle-plugin/jmeter-gradle-plugin.svg?branch=master)](https://travis-ci.org/jmeter-gradle-plugin/jmeter-gradle-plugin)

For usage see: http://jmeter.foragerr.net/  
or [wiki](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/wiki/Getting-Started)

##News
**4/21/2016**
Version 1.0.5 released
* added support for minHeapSize [#56](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/56)
* added additional jmeter-plugin and webdriver jars to classpath [#57](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/57)
* Fixed [#55](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/55) issue with jmSystemPropertiesFiles
* Reformatted a few code files

**4/2/2016**
* Version 1.0.4 released
* [#47](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/47) and [#49](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/49) Fixed. These are related issues that cause a test failure when using xpath extractor
* Gradle wrapper upgraded to 2.11
* [#41](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/41) Fixed
* [#42](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/42) Fixed

**12/12/2015**
* Version 1.0.3 released
* [#39](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/39) support for commandline option `-q` added
* [#40](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/40) Fixed, jmeter GUI now launches with testfile loaded when available

**11/15/2015**
* Stable version 1.0.2 released.
* Added out of box support for jp@gc plugins for both test execution and gui mode

**11/11/2015**
* version 1.0.1 released.
* Added support for passing -D and -XX type arguments to the JMeter jvm. [See here](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/37).

**11/6/2015**
* Stable version 1.0 released. 
* This plugin is now also available from the [Gradle plugins page](https://plugins.gradle.org/)
* With gradle (ver>2.1) the plugin can be used with this shorthand:  

		plugins {
		  id "net.foragerr.jmeter" version "1.0-2.13"
		}

##Attribution
This project started as a hard fork of [kulya/jmeter-gradle-plugin](https://github.com/kulya/jmeter-gradle-plugin). Besides defect fixes and feature enhancements, most of the original codebase has been re-written since. 

If you are a user of the older plugin see [here]() for easy migration to this version of the plugin. If you're a developer familiar with the older plugin, see [here]() for notes about major changes.
