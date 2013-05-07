package pt.ist.fenixWebFramework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.struts.action.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.presentationTier.servlets.filters.ExceptionHandlerFilter;

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
     * Struts exception handler class name. Defaults to <code>ExceptionHandler.class.getName()</code>
     */
    public static String getExceptionHandlerClassname() {
        return getProperty("exceptionHandlerClassname", ExceptionHandler.class.getName());
    }

    /**
     * This parameter specifies if a checksum is generated and verified for each URL to avoid url tampering attacks. Defaults to
     * <code>true</code>
     */
    public static Boolean getFilterRequestWithDigest() {
        return getBooleanProperty("filterRequestWithDigest", true);
    }

    /**
     * Redirect link to use when a URL tampering is detected. Default is <code>""</code>
     * 
     * @see {@link #getFilterRequestWithDigest()}
     */
    public static String getTamperingRedirect() {
        return getProperty("tamperingRedirect", "");
    }

    /**
     * Renderer validators to generate JQuery javascript to also validate user input at client side. Defaults to
     * <strong>false</strong>
     */
    public static boolean isJavascriptValidationEnabled() {
        return getBooleanProperty("javascriptValidationEnabled", false);
    }

    /**
     * Determines when the StandardInputRenderer shows the required (*) mark. Defaults to <strong>false</strong>
     */
    public static boolean getRequiredMarkShown() {
        return getBooleanProperty("requiredMarkShown", false);
    }

    /**
     * Error page URL. Handles exceptions treated by {@link ExceptionHandlerFilter}.
     */
    public static String getErrorPage() {
        return getProperty("errorPage", "");
    }

    private static String getProperty(final String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    private static boolean getBooleanProperty(final String key, boolean defaultValue) {
        return properties.containsKey(key) ? Boolean.parseBoolean(properties.getProperty(key)) : defaultValue;
    }
}