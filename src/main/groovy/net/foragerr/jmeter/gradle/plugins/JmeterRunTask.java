package net.foragerr.jmeter.gradle.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.transform.TransformerException;

import net.foragerr.jmeter.gradle.plugins.worker.JMeterRunner;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.gradle.api.GradleException;

public class JmeterRunTask extends JmeterAbstractTask {

    /**
     * Path to a Jmeter test XML file.
     * Relative to srcDir.
     * May be declared instead of the parameter includes.
     */
    private List<File> jmeterTestFiles;

    /**
     * Sets the list of include patterns to use in directory scan for JMeter Test XML files.
     * Relative to srcDir.
     * May be declared instead of a single jmeterTestFile.
     * Ignored if parameter jmeterTestFile is given.
     */
    private List<String> includes;

    /**
     * Sets the list of exclude patterns to use in directory scan for Test files.
     * Relative to srcDir.
     * Ignored if parameter jmeterTestFile file is given.
     */
    private List<String> excludes;

    /**
     * Directory in which the reports are stored.
     * <p/>
     * By default build/jmeter-report"
     */
    private File reportDir;

    /**
     * Whether or not to generate reports after measurement.
     * <p/>
     * By default true
     */
    private Boolean enableReports = null;

    /**
     * Whether or not to generate extended reports after measurement.
     * <p/>
     * By default true
     */
    private Boolean enableExtendedReports = null;


    /**
     * Use remote JMeter installation to run tests
     * <p/>
     * By default false
     */
    private Boolean remote = null;

    /**
     * Sets whether ErrorScanner should ignore failures in JMeter result file.
     * <p/>
     * By default false
     */
    private Boolean jmeterIgnoreFailure = null;

    /**
     * Sets whether ErrorScanner should ignore errors in JMeter result file.
     * <p/>
     * By default false
     */
    private Boolean jmeterIgnoreError = null;

    /**
     * Postfix to add to report file.
     * <p/>
     * By default "-report.html"
     */
    private String reportPostfix;

     /**
     * Custom Xslt which is used to create the report.
     */
    private File reportXslt;

    private String maxHeapSize;

    private List<File> jmeterUserPropertiesFiles;

    @Override
    protected void runTaskAction() throws IOException {
        List<String> testFiles = new ArrayList<String>();
        if (jmeterTestFiles != null) {
            for (File f : jmeterTestFiles) {
                if (f.exists() && f.isFile()) {
                    testFiles.add(f.getCanonicalPath());
                } else {
                    throw new GradleException("Test file " + f.getCanonicalPath() + " does not exists");
                }
            }
        } else {
            testFiles.addAll(scanSourceFolder());
        }

        List<String> results = new ArrayList<String>();
        for (String file : testFiles) {
            results.add(executeJmeterTest(file));
        }

        if (this.enableReports) {
            makeHTMLReport(results);
        }
        if (this.enableExtendedReports) {
            makeExtendedReport(results);
        }
        checkForErrors(results);

    }


    @Override
    protected void loadPropertiesFromConvention() {
        super.loadPropertiesFromConvention();
        jmeterIgnoreError = getJmeterIgnoreError();
        jmeterIgnoreFailure = getJmeterIgnoreFailure();
        jmeterTestFiles = getJmeterTestFiles();
        reportDir = getReportDir();
        remote = getRemote();
        enableReports = getEnableReports();
        enableExtendedReports = getEnableExtendedReports();
        reportPostfix = getReportPostfix();
        reportXslt = getReportXslt();
        includes = getIncludes();
        excludes = getExcludes();
        maxHeapSize = getMaxHeapSize();
    }

     private void checkForErrors(List<String> results) {
        ErrorScanner scanner = new ErrorScanner(this.jmeterIgnoreError, this.jmeterIgnoreFailure);
        try {
            for (String file : results) {
                if (scanner.scanForProblems(new File(file))) {
                    log.warn("There were test errors.  See the jmeter logs for details");
                }
            }
        } catch (IOException e) {
            throw new GradleException("Can't read log file", e);
        }
    }

    private void makeExtendedReport(List<String> results) throws IOException {
        for (String resultFile : results) {
            try {
            	log.info("Creating Extended Reports");
                CreateExtendedReport.createExtendedReport(resultFile,getJmeterPropsFile(),getWorkDir());
            } catch (Throwable e) {
                log.error("Failed to create extended report for " + resultFile, e);
            }
        }
    }


    private void makeHTMLReport(List<String> results) {
        try {
            ReportTransformer transformer;
            transformer = new ReportTransformer(getXslt());
            log.info("Building HTML Report.");
            for (String resultFile : results) {
                final String outputFile = toOutputFileName(resultFile);
                log.info("transforming: " + resultFile + " to " + outputFile);
                transformer.transform(resultFile, outputFile);
            }
        } catch (FileNotFoundException e) {
            log.error("Can't transfrorm result", e);
            throw new GradleException("Error writing report file jmeter file.", e);
        } catch (TransformerException e) {
            log.error("Can't transfrorm result", e);
            throw new GradleException("Error transforming jmeter results", e);
        } catch (IOException e) {
            log.error("Can't transfrorm result", e);
            throw new GradleException("Error copying resources to jmeter results", e);
        }  catch (Exception e) {
            log.error("Can't transfrorm result", e);
        }
    }

    private InputStream getXslt() throws IOException {
        if (this.reportXslt == null) {
            //if we are using the default report, also copy the images out.
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/collapse.jpg"), new FileOutputStream(this.reportDir.getPath() + File.separator + "collapse.jpg"));
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/expand.jpg"), new FileOutputStream(this.reportDir.getPath() + File.separator + "expand.jpg"));
            log.debug("Using reports/jmeter-results-detail-report_21.xsl for building report");
            return Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/jmeter-results-detail-report_21.xsl");
        } else {
            log.debug("Using " + this.reportXslt + " for building report");
            return new FileInputStream(this.reportXslt);
        }
    }

     private String toOutputFileName(String fileName) {
        if (fileName.endsWith(".xml")) {
            return fileName.replace(".xml", this.reportPostfix);
        } else {
            return fileName + this.reportPostfix;
        }
    }

    private String executeJmeterTest(String fileLocation) {
        try {
            File testFile = new File(fileLocation);
            File resultFile = getResultFile(testFile);
            resultFile.delete();
            List<String> args = new ArrayList<String>();
             args.addAll(Arrays.asList("-n",
                     "-t", testFile.getCanonicalPath(),
                     "-l", resultFile.getCanonicalPath()
                     ));
            args.add("-p");
            args.add(getJmeterPropsFile().getCanonicalPath());
            
            if(jmeterUserPropertiesFiles!=null)
            {
                for(File userPropertyFile: jmeterUserPropertiesFiles)
                {
                    if(userPropertyFile.exists() && userPropertyFile.isFile())
                    {
                        args.addAll(Arrays.asList("-S", userPropertyFile.getCanonicalPath()));
                    }
                }
            }
            initUserProperties(args);

            if (remote) {
                args.add("-r");
            }
            
            log.info("JMeter is called with the following command line arguments: " + args.toString());
            

            JmeterSpecs specs = new JmeterSpecs();
            specs.getSystemProperties().put("search_paths", System.getProperty("search_paths"));
            specs.getSystemProperties().put("jmeter.home", getWorkDir().getAbsolutePath());
            specs.getSystemProperties().put("saveservice_properties", System.getProperty("saveservice_properties"));
            specs.getSystemProperties().put("upgrade_properties", System.getProperty("upgrade_properties"));
            specs.getSystemProperties().put("log_file", System.getProperty("log_file"));
            specs.getSystemProperties().put("jmeter.save.saveservice.output_format","xml");
            specs.getJmeterProperties().addAll(args);
            specs.setMaxHeapSize(maxHeapSize);
            new JMeterRunner().executeJmeterCommand(specs, getWorkDir().getAbsolutePath());
            return resultFile.getCanonicalPath();
        } catch (IOException e) {
            throw new GradleException("Can't execute test", e);
        }
    }

private File getJmeterPropsFile() {
	File propsInSrcDir = new File(srcDir,"jmeter.properties");
	
    //1. Is jmeterPropertyFile defined?
    if (getJmeterPropertyFile() != null) 
    	return getJmeterPropertyFile();
    
    //2. Does jmeter.properties exist in $srcDir/test/jmeter
    else if (propsInSrcDir.exists()) 
    	return propsInSrcDir;
    
    //3. If neither, use the default jmeter.properties
    else{
    	File defPropsFile = new File(workDir + System.getProperty("default_jm_properties"));
    	return defPropsFile;
    }
}


private File getResultFile(File testFile) {
    	
        DateFormat fmt = new SimpleDateFormat("yyyyMMdd-HHmm");
        if (resultFilenameTimestamp==null)
        	return new File(reportDir, testFile.getName() + "-" + fmt.format(new Date()) + ".xml");
        
    	//if resultFilenameTimestamp is "none" do not use a timestamp in filename
    	if (resultFilenameTimestamp.equals("none"))
    		return new File(reportDir, testFile.getName() + ".xml");
    	
    	//else if resultFilenameTimestamp is "useSaveServiceFormat" use saveservice.format
    	if (resultFilenameTimestamp.equals("useSaveServiceFormat")){
    		String saveServiceFormat =  System.getProperty("jmeter.save.saveservice.timestamp_format");
    		if (saveServiceFormat.equals("none")) return new File(reportDir, testFile.getName() + ".xml");
    		try
    		{
    		    fmt = new SimpleDateFormat(saveServiceFormat);
    		    return new File(reportDir, testFile.getName() + "-" + fmt.format(new Date()) + ".xml");
    		}
    		catch (Exception e)
    		{
    		    // jmeter.save.saveservice.timestamp_format does not contain a valid format
    			log.warn("jmeter.save.saveservice.timestamp_format Not defined, using default timestamp format");
    		}
    	}
    	
    	//for all other unhandled conditions fallback to default:
    	fmt = new SimpleDateFormat("yyyyMMdd-HHmm");
    	return new File(reportDir, testFile.getName() + "-" + fmt.format(new Date()) + ".xml");
	}


	private List<String> scanSourceFolder() {
        List<String> result = new ArrayList<String>();
        DirectoryScanner scaner = new DirectoryScanner();
        scaner.setBasedir(getSrcDir());
        scaner.setIncludes(includes == null ? new String[]{"**/*.jmx"} : includes.toArray(new String[]{}));
        if (excludes != null) {
            scaner.setExcludes(excludes.toArray(new String[]{}));
        }
        scaner.scan();
        for (String localPath : scaner.getIncludedFiles()) {
            result.add(scaner.getBasedir() + File.separator + localPath);
        }
        return result;
    }

    public List<File> getJmeterTestFiles() {
        return jmeterTestFiles;
    }

    public void setJmeterTestFiles(List<File> jmeterTestFiles) {
        this.jmeterTestFiles = jmeterTestFiles;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public File getReportDir() {
        return reportDir;
    }

    public void setReportDir(File reportDir) {
        this.reportDir = reportDir;
    }

    public Boolean getEnableReports() {
        return enableReports;
    }

    public void setEnableReports(Boolean enableReports) {
        this.enableReports = enableReports;
    }

    public boolean getEnableExtendedReports() {
        return enableExtendedReports;
    }

    public void setEnableExtendedReports(Boolean enableExtenededReports) {
        this.enableExtendedReports = enableExtenededReports;
    }

    public Boolean getRemote() {
        return remote;
    }

    public void setRemote(Boolean remote) {
        this.remote = remote;
    }

    public Boolean getJmeterIgnoreFailure() {
        return jmeterIgnoreFailure;
    }

    public void setJmeterIgnoreFailure(Boolean jmeterIgnoreFailure) {
        this.jmeterIgnoreFailure = jmeterIgnoreFailure;
    }

    public Boolean getJmeterIgnoreError() {
        return jmeterIgnoreError;
    }

    public void setJmeterIgnoreError(Boolean jmeterIgnoreError) {
        this.jmeterIgnoreError = jmeterIgnoreError;
    }

    public String getReportPostfix() {
        return reportPostfix;
    }

    public void setReportPostfix(String reportPostfix) {
        this.reportPostfix = reportPostfix;
    }

    public File getReportXslt() {
        return reportXslt;
    }

    public void setReportXslt(File reportXslt) {
        this.reportXslt = reportXslt;
    }

    public String getMaxHeapSize() {
        return maxHeapSize;
    }

    public void setMaxHeapSize(String maxHeapSize) {
        this.maxHeapSize = maxHeapSize;
    }

    public List<File> getJmeterUserPropertiesFiles() {
        return jmeterUserPropertiesFiles;
    }

    public void setJmeterUserPropertiesFiles(List<File> jmeterUserPropertiesFiles) {
        this.jmeterUserPropertiesFiles = jmeterUserPropertiesFiles;
    }
}
