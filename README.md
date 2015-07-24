This is a gradle plugin that enables running JMeter tests. This repo is based on [this now-dormant repo](https://github.com/kulya/jmeter-gradle-plugin), significant redevelopment has occured since version 0.5.0 Development is still active, and stability is beta. The repo is integrated with travis and bintray for auto-builds.

Getting Started 
===============

## Edit your build.gradle

Create a build.gradle file and include the jmeter plugin as follows:

	apply plugin: 'net.foragerr.jmeter'

	buildscript {
	    repositories {
			 maven {
			    url 'https://dl.bintray.com/jmeter-gradle-plugin/jmeter'
			 }
	    }
	    dependencies {
	        classpath "net.foragerr.jmeter:jmeter-gradle-plugin:latest.release"
	        
	        //for specific version use:
	        //classpath "net.foragerr.jmeter:jmeter-gradle-plugin:0.5.0-2.13"
	    }
	}

## Configure JMeter
### Simple configuration

include the following in your build.gradle (Optional - if this section is not declared, default configuration is used):

	jmeter {
	    jmTestFiles = [file("src/test/jmeter/test2.jmx")] //if jmx file is not in the default location
	    jmUserPropertiesFiles = [file("src/test/jmeter/user.properties")] //to add additional user properties
		 enableExtendedReports = true //produce Graphical and CSV reports
	}

### Advanced configuration

This section is under construction

**Changing default path**

In order to use a custom directory for jmx files (default is `src/test/jmeter`) you can set the property *testFileDir*.
* E.g. testFileDir = file("src/main/resources/jmeter")

**Include and exclude files**

When working with the default or custom path to scan for jmx files, you can include or exclude specific files with the *ïncludes* and *excludes* properties.
* E.g. excludes = ["excludeThisTest.jmx"]

(Note that you should provide a list of patterns, not just a String) 

### Edit JMeter files

By default the plugin will search for *.jmx files in `src/test/jmeter`. You can launch the UI end edit your files by running:

`gradle jmGui`

### Run the tests

You can run the tests by executing 

`gradle jmRun`

### Create Reports

You can run the tests by executing 

`gradle jmReport`

By default, extended reports are turned on and HTML reports are turned off

The results of the tests will can be found in(default location, can be overridden) `build/jmeter-report`