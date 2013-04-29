package pt.ist.fenixWebFramework;

import java.util.Locale;
import java.util.Properties;

import pt.utl.ist.fenix.tools.file.DSpaceFileManagerFactory;
import pt.utl.ist.fenix.tools.file.FileManagerFactory;
import pt.utl.ist.fenix.tools.util.i18n.Language;

public class FenixWebFramework {

    private static final Object INIT_LOCK = new Object();

    private static Config frameworkConfig;

    public static void initialize(final Config config) {
        synchronized (INIT_LOCK) {

            frameworkConfig = config;

            final Locale locale =
                    new Locale(config.getDefaultLanguage(), config.getDefaultLocation(), config.getDefaultVariant());

            Language.setDefaultLocale(locale);

            initializeFileManager(config);
        }
    }

    private static void initializeFileManager(Config config) {
        if (config.getDspaceClientTransportClass() != null) {
            final Properties properties = new Properties();
            properties.put("dspace.client.transport.class", config.getDspaceClientTransportClass());
            properties.put("file.manager.factory.implementation.class", config.getFileManagerFactoryImplementationClass());
            properties.put("dspace.serverUrl", config.getDspaceServerUrl());
            properties.put("dspace.downloadUriFormat", config.getDspaceDownloadUriFormat());
            properties.put("dspace.username", config.getDspaceUsername());
            properties.put("dspace.password", config.getDspacePassword());
            properties.put("dspace.rmi.server.name", config.getDspaceRmiServerName());
            properties.put("jndi.properties.file", config.getJndiPropertiesFile());
            properties.put("rmi.registry.port", config.getRmiRegistryPort());
            properties.put("rmi.port", config.getRmiPort());
            properties.put("rmi.ssl", config.getRmiSsl());
            properties.put("rmi.ssl.truststore", config.getRmiSslTruststore());
            properties.put("rmi.ssl.truststore.password", config.getRmiSslTruststorePassword());
            properties.put("rmi.stream.bytes.min", config.getRmiStreamBytesMin());
            properties.put("rmi.stream.bytes.max", config.getRmiStreamBytesMax());
            properties.put("rmi.stream.bytes.block", config.getRmiStreamBytesBlock());

            FileManagerFactory.init(config.getFileManagerFactoryImplementationClass());

            DSpaceFileManagerFactory.init(properties);
        }
    }

    public static Config getConfig() {
        return frameworkConfig;
    }

}
