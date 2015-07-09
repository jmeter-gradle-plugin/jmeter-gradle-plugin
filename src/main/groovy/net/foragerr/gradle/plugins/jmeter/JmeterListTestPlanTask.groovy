package net.foragerr.gradle.plugins.jmeter;

import org.apache.tools.ant.DirectoryScanner;

public class JmeterListTestPlanTask extends JmeterAbstractTask {

    /**
     * Sets the list of include patterns to use in directory scan for JMeter Test XML files.
     * Relative to srcDir.
     */
    private List<String> includes;

    /**
     * Sets the list of exclude patterns to use in directory scan for Test files.
     * Relative to srcDir.
     */
    private List<String> excludes;

    @Override
    protected void runTaskAction() throws IOException {
        System.out.println("");
        System.out.println("Jmeter Test Plan");
        System.out.println("----------------");
        List<String> allTestFiles = new ArrayList<String>();
        allTestFiles.addAll(scanSourceFolder());
        if (allTestFiles.size()>0){
            for (String file : allTestFiles) {
                System.out.println(file);
            }
        } else {
            System.out.println("There is no test files in source directory.");
        }
    }


    @Override
    protected void loadPropertiesFromConvention() {
        super.loadPropertiesFromConvention();
        includes = getIncludes();
        excludes = getExcludes();
    }


    private List<String> scanSourceFolder() {
        def defaultIncludes = ["**/*.jmx"];
        List<String> result = new ArrayList<String>();
        DirectoryScanner scaner = new DirectoryScanner();
        scaner.setBasedir(getSrcDir());
        scaner.setExcludes(DirectoryScanner.defaultExcludes);
        scaner.setCaseSensitive(false);
        scaner.setIncludes(includes == null ? defaultIncludes.toArray(new String[defaultIncludes.size()]) : includes.toArray(new String[includes.size()]));
        if (excludes != null) {
            scaner.addExcludes(excludes.toArray(new String[excludes.size()]));
        }
        scaner.scan();
        for (String localPath : scaner.getIncludedFiles()) {
            result.add(localPath);
        }

        return result;
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
}
