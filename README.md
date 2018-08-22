## Gradle plugin to execute JMeter tests.  
[![Build Status](https://travis-ci.org/jmeter-gradle-plugin/jmeter-gradle-plugin.svg?branch=master)](https://travis-ci.org/jmeter-gradle-plugin/jmeter-gradle-plugin) [ ![Download](https://api.bintray.com/packages/jmeter-gradle-plugin/jmeter/jmeter-gradle-plugin/images/download.svg) ](https://bintray.com/jmeter-gradle-plugin/jmeter/jmeter-gradle-plugin/_latestVersion)

For usage see: http://jmeter.foragerr.net/  
or [wiki](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/wiki/Getting-Started)

## News
**8/22/2018**
* Version 1.1.0 released with support for Jmeter 4.0
* I have not extensively tested this release, caveat emptor

**11/16/2017**
* Version 1.0.10 release with support for Jmeter 3.3
* Note that 1.0.9 and 1.0.8 are broken

**8/21/2016**
* [#79](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/79) fixed

**6/26/2016**
* Version 1.0.6 released
* [#56](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/56) and [#77](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/issues/77) fixed

**4/21/2016**
* Version 1.0.5 released
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

[See older here..](https://github.com/jmeter-gradle-plugin/jmeter-gradle-plugin/wiki/Release-Notes)

##Attribution
This project started as a hard fork of [kulya/jmeter-gradle-plugin](https://github.com/kulya/jmeter-gradle-plugin). Besides defect fixes and feature enhancements, most of the original codebase has been re-written since. 

If you are a user of the older plugin see [here]() for easy migration to this version of the plugin. If you're a developer familiar with the older plugin, see [here]() for notes about major changes.
