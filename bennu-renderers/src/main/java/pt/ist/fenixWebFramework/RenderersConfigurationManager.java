package pt.ist.fenixWebFramework;

import org.fenixedu.bennu.core.annotation.ConfigurationManager;
import org.fenixedu.bennu.core.annotation.ConfigurationProperty;
import org.fenixedu.bennu.core.util.ConfigurationInvocationHandler;

public class RenderersConfigurationManager {
    @ConfigurationManager(description = "Bennu Renderers Configuration")
    public interface ConfigurationProperties {
        @ConfigurationProperty(key = "filterRequestWithDigest",
                description = "Specifies if a checksum is generated and verified for each URL to avoid url tampering attacks.",
                defaultValue = "true")
        public Boolean filterRequestWithDigest();

        @ConfigurationProperty(
                key = "tamperingRedirect",
                description = "Redirect link to use when a URL tampering is detected. Required when 'filterRequestWithDigest' is true",
                defaultValue = "/")
        public String tamperingRedirect();

        @ConfigurationProperty(key = "javascriptValidationEnabled",
                description = "Renderer validators to generate JQuery javascript to also validate user input at client side.",
                defaultValue = "false")
        public Boolean javascriptValidationEnabled();

        @ConfigurationProperty(key = "requiredMarkShown",
                description = "Determines when the StandardInputRenderer shows the required (*) mark.", defaultValue = "false")
        public Boolean requiredMarkShown();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }
}