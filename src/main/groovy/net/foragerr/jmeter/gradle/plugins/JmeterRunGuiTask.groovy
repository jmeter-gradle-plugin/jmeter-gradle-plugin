package net.foragerr.jmeter.gradle.plugins

import net.foragerr.jmeter.gradle.plugins.worker.JMeterRunner;

import org.gradle.api.GradleException

class JmeterRunGuiTask extends JmeterAbstractTask{
    /**
     * Path to a Jmeter XML file which will be edited. This is for jmeterEditor task
     * Relative to srcDir.
     * May be declared instead of the parameter includes.
     */
    private File jmeterEditFile;

    private List<File> jmeterUserPropertiesFiles;

    @Override
    protected void runTaskAction() throws IOException{
        def editFile;
        try {
            if (jmeterEditFile != null) {
                if (jmeterEditFile.exists() && jmeterEditFile.isFile()) {
                    editFile = jmeterEditFile.getCanonicalPath();
                } else {
                    throw new GradleException("Edit file " + jmeterEditFile.getCanonicalPath() + " does not exists");
                }
            }

            List<String> args = new ArrayList<String>();
            args.addAll(Arrays.asList(
                    "-p", getJmeterPropertyFile().getCanonicalPath()));

            if (editFile != null) {
                args.addAll(Arrays.asList(
                        "-t", editFile));
            }

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

            log.debug("JMeter is called with the following command line arguments: " + args.toString());

            JmeterSpecs specs = new JmeterSpecs();
            specs.getSystemProperties().put("search_paths", System.getProperty("search_paths"));
            specs.getSystemProperties().put("jmeter.home", getWorkDir().getAbsolutePath());
            specs.getSystemProperties().put("saveservice_properties", System.getProperty("saveservice_properties"));
            specs.getSystemProperties().put("upgrade_properties", System.getProperty("upgrade_properties"));
            specs.getSystemProperties().put("log_file", System.getProperty("log_file"));
            specs.getJmeterProperties().addAll(args);
            specs.setMaxHeapSize(getMaxHeapSize());
            new JMeterRunner().executeJmeterCommand(specs, getWorkDir().getAbsolutePath());


        } catch (IOException e) {
            throw new GradleException("Can't execute test", e);
        }
    }

    public File getJmeterEditFile() {
        return jmeterEditFile;
    }

    public void setJmeterEditFile(File jmeterEditFile) {
        this.jmeterEditFile = jmeterEditFile;
    }

    public List<File> getJmeterUserPropertiesFiles() {
        return jmeterUserPropertiesFiles;
    }

    public void setJmeterUserPropertiesFiles(List<File> jmeterUserPropertiesFiles) {
        this.jmeterUserPropertiesFiles = jmeterUserPropertiesFiles;
    }
}
