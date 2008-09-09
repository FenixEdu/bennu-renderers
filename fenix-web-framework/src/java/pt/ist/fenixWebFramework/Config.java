package pt.ist.fenixWebFramework;


public class Config extends pt.ist.fenixframework.Config {

    /**
     * This <strong>optional</strong> parameter that indicates if the database 
     * structure should be automatically updated with missing structure entries
     * when the framework is initialized. Defaults to false;
     */
    protected boolean updateDataRepositoryStructure = false;


    /**
     * This <strong>required</strong> parameter specifies the default
     * language that will be used to construct the default <code>java.util.Locale<code>
     * used by the frameworks localization tools.
     */
    protected String defaultLanguage = null;

    /**
     * This <strong>required</strong> parameter specifies the default
     * language that will be used to construct the default <code>java.util.Locale<code>
     * used by the frameworks localization tools.
     */
    protected String defaultLocation = null;

    /**
     * This <strong>optional</strong> parameter specifies the default
     * variant that will be used to construct the default <code>java.util.Locale<code>
     * used by the frameworks localization tools.
     */
    protected String defaultVariant = null;


    /**
     * This <strong>optional</strong> parameter specifies the directory where the profile
     * log will be stored. If it is not specified, and a filename is provided for the log,
     * the current working directory will be used.
     */
    protected String logProfileDir = null;

    /**
     * This <strong>optional</strong> parameter specifies the filename where the profile
     * log will be stored. If it is not specified, the profiling logger will not be
     * initialized, and it will fall back on whatever is specified in to log4j.xml file.
     */
    protected String logProfileFilename = null;

    /**
     * This <strong>optional</strong> parameter specifies the
     * context path of   requests used by the application 
     * The default value for this parameter is <code>null</code>.
     */
    protected String appContext = null;
    
    
    /**
     * This <strong>optional</strong> parameter specifies if
     * the fenix-web-framework should generate checksum
     * hashing for each url in order to avoid url tampering
     * 
     */
    protected Boolean filterRequestWithDigest = false; 
    
    /**
     * This is <strong>required when defining filterRequestWithDigest</strong> 
     * parameter and specifies the link to where a URL tampering user shall be
     * sent
     */
    protected String tamperingRedirect = "";
    
    
    // TODO: document this Dspace stuff
    protected String dspaceClientTransportClass = null;
    protected String fileManagerFactoryImplementationClass = null;
    protected String dspaceServerUrl = null;
    protected String dspaceDownloadUriFormat = null;
    protected String dspaceUsername = null;
    protected String dspacePassword = null;
    protected String dspaceRmiServerName = null;
    protected String jndiPropertiesFile = null;
    protected String rmiRegistryPort = null;
    protected String rmiPort = null;
    protected String rmiSsl = null;
    protected String rmiSslTruststore = null;
    protected String rmiSslTruststorePassword = null;
    protected String rmiStreamBytesMin = null;
    protected String rmiStreamBytesMax = null;
    protected String rmiStreamBytesBlock = null;

    // TODO : document cas stuff
    protected boolean casEnabled = false;
    protected String casLoginUrl = null;
    protected String casLogoutUrl = null;
    protected String casValidateUrl = null;

    // TODO : document this
    protected String exceptionHandlerClassname = null;

    // TODO : make method in superclass visible in the hierarchy so we don't need to replicate the code here.
    private static void checkRequired(Object obj, String fieldName) {
        if (obj == null) {
            throw new Error("The required field '" + fieldName + "' was not specified in the FenixFramework config.");
        }
    }

    public void checkConfig() {
	super.checkConfig();
        checkRequired(defaultLanguage, "defaultLanguage");
        checkRequired(defaultLocation, "defaultLocation");
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getDefaultLocation() {
        return defaultLocation;
    }

    public String getDefaultVariant() {
        return defaultVariant;
    }

    public String getLogProfileDir() {
        return logProfileDir;
    }

    public String getLogProfileFilename() {
        return logProfileFilename;
    }

    public String getDspaceClientTransportClass() {
        return dspaceClientTransportClass;
    }

    public String getFileManagerFactoryImplementationClass() {
        return fileManagerFactoryImplementationClass;
    }

    public String getDspaceServerUrl() {
        return dspaceServerUrl;
    }

    public String getDspaceDownloadUriFormat() {
        return dspaceDownloadUriFormat;
    }

    public String getDspaceUsername() {
        return dspaceUsername;
    }

    public String getDspacePassword() {
        return dspacePassword;
    }

    public String getDspaceRmiServerName() {
        return dspaceRmiServerName;
    }

    public String getJndiPropertiesFile() {
        return jndiPropertiesFile;
    }

    public String getRmiRegistryPort() {
        return rmiRegistryPort;
    }

    public String getRmiPort() {
        return rmiPort;
    }

    public String getRmiSsl() {
        return rmiSsl;
    }

    public String getRmiSslTruststore() {
        return rmiSslTruststore;
    }

    public String getRmiSslTruststorePassword() {
        return rmiSslTruststorePassword;
    }

    public String getRmiStreamBytesMin() {
        return rmiStreamBytesMin;
    }

    public String getRmiStreamBytesMax() {
        return rmiStreamBytesMax;
    }

    public String getRmiStreamBytesBlock() {
        return rmiStreamBytesBlock;
    }

    public boolean isCasEnabled() {
        return casEnabled;
    }

    public String getCasLoginUrl() {
        return casLoginUrl;
    }

    public String getExceptionHandlerClassname() {
        return exceptionHandlerClassname;
    }
    
    public String getAppContext() {
        return appContext;
    }

    public Boolean getFilterRequestWithDigest() {
        return filterRequestWithDigest;
    }

    public String getTamperingRedirect() {
	return tamperingRedirect;
    }

    public String getCasLogoutUrl() {
        return casLogoutUrl;
    }

    public String getCasValidateUrl() {
        return casValidateUrl;
    }

}
