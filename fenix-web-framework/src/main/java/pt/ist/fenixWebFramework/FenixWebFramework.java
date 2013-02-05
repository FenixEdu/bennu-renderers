package pt.ist.fenixWebFramework;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.LevelRangeFilter;

import pt.ist.fenixframework.FenixFramework;
import pt.utl.ist.fenix.tools.file.DSpaceFileManagerFactory;
import pt.utl.ist.fenix.tools.file.FileManagerFactory;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import dml.DomainModel;

public class FenixWebFramework extends FenixFramework {

    private static final Object INIT_LOCK = new Object();

    public static void bootStrap(final Config config) {
        synchronized (INIT_LOCK) {
            FenixFramework.bootStrap(config);
        }
    }

    public static void initialize(final Config config) {
        synchronized (INIT_LOCK) {
            FenixFramework.initialize();

            final Locale locale =
                    new Locale(config.getDefaultLanguage(), config.getDefaultLocation(), config.getDefaultVariant());
            Language.setDefaultLocale(locale);

            initializeLoggingSystem(config);

            initializeFileManager(config);
        }
    }

    private static void initializeLoggingSystem(final Config config) {
        if (config.logProfileFilename != null) {
            final Logger logger = Logger.getLogger("pt.ist.fenixWebFramework.servlets.filters.ProfilingFilter");
            logger.setAdditivity(false);
            logger.setLevel(Level.DEBUG);

            final Layout layout = new PatternLayout("%d{HH:mm:ss.SSS} %m%n");
            final String filename =
                    config.logProfileDir == null ? config.logProfileFilename : config.logProfileDir + File.separatorChar
                            + config.logProfileFilename;
            try {
                final FileAppender fileAppender = new FileAppender(layout, filename, true);
                fileAppender.setName("pt.ist.fenixWebFramework.servlets.filters");
                fileAppender.setThreshold(Priority.DEBUG);

                final LevelRangeFilter levelRangeFilter = new LevelRangeFilter();
                levelRangeFilter.setLevelMin(Level.DEBUG);
                levelRangeFilter.setLevelMax(Level.WARN);
                levelRangeFilter.setAcceptOnMatch(true);
                fileAppender.addFilter(levelRangeFilter);
                final DenyAllFilter denyAllFilter = new DenyAllFilter();
                fileAppender.addFilter(denyAllFilter);

                logger.addAppender(fileAppender);
            } catch (IOException ex) {
                //throw new Error(ex);
                final Logger l = Logger.getLogger(FenixWebFramework.class);
                logger.warn("Profile logger not initialized correctly.");
            }
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
        return (Config) FenixFramework.getConfig();
    }

    public static DomainModel getDomainModel() {
        return FenixFramework.getDomainModel();
    }

}
