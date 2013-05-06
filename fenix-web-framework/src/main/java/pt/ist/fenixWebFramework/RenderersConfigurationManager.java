package pt.ist.fenixWebFramework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.struts.action.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderersConfigurationManager {
    private static Logger logger = LoggerFactory.getLogger(RenderersConfigurationManager.class);

    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = RenderersConfigurationManager.class.getResourceAsStream("/renderers.properties")) {
            if (inputStream == null) {
                logger.warn("renderers.properties not found, using defaults");
            } else {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            logger.warn("renderers.properties could not be read, using defaults");
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This <strong>optional</strong> parameter specifies the context path of
     * requests used by the application The default value for this parameter is <code>null</code>.
     */
    protected static String appContext = null;

    /**
     * This <strong>optional</strong> parameter specifies if the
     * fenix-web-framework should generate checksum hashing for each url in
     * order to avoid url tampering
     * 
     */
    protected static Boolean filterRequestWithDigest = false;

    /**
     * This is <strong>required when defining filterRequestWithDigest</strong>
     * parameter and specifies the link to where a URL tampering user shall be
     * sent
     */
    protected static String tamperingRedirect = "";

    // TODO : document this
    protected static String exceptionHandlerClassname = ExceptionHandler.class.getName();

    /**
     * This is a <strong>optional</strong> that allows renderer validators to
     * generate JQuery javascript to also validate user input at client side.
     * Defaults to <strong>false</strong>
     */
    protected static boolean javascriptValidationEnabled = false;

    /**
     * This is a <strong>optional</strong> that determines when the
     * StandardInputRenderer shows the required (*) mark. Defaults to
     * <strong>false</strong>
     */
    protected static boolean requiredMarkShown = false;

    public static String getExceptionHandlerClassname() {
        return getProperty("exceptionHandlerClassname", exceptionHandlerClassname);
    }

    public static String getAppContext() {
        return getProperty("appContext", appContext);
    }

    public static Boolean getFilterRequestWithDigest() {
        return getBooleanProperty("filterRequestWithDigest", filterRequestWithDigest);
    }

    public static String getTamperingRedirect() {
        return getProperty("tamperingRedirect", tamperingRedirect);
    }

    public static boolean isJavascriptValidationEnabled() {
        return getBooleanProperty("javascriptValidationEnabled", javascriptValidationEnabled);
    }

    public static boolean getRequiredMarkShown() {
        return getBooleanProperty("requiredMarkShown", requiredMarkShown);
    }

    private static String getProperty(final String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    private static boolean getBooleanProperty(final String key, boolean defaultValue) {
        return properties.containsKey(key) ? Boolean.parseBoolean(properties.getProperty(key)) : defaultValue;
    }
}