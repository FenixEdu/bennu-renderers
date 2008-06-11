package pt.ist.fenixWebFramework;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.LevelRangeFilter;

import pt.ist.fenixframework.FenixFramework;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import dml.DomainModel;

public class FenixWebFramework extends FenixFramework {

    private static final Object INIT_LOCK = new Object();

    public static void initialize(final Config config) {
	synchronized (INIT_LOCK) {
	    FenixFramework.initialize(config);

	    final Locale locale = new Locale(config.getDefaultLanguage(), config.getDefaultLocation(), config.getDefaultVariant());
	    Language.setDefaultLocale(locale);

	    initializeLoggingSystem(config);
	}
    }

    private static void initializeLoggingSystem(final Config config) {
	if (config.logProfileFilename != null) {
	    final Logger logger = Logger.getLogger("com.atlassian.util.profiling");
	    logger.setAdditivity(false);

	    final Layout layout = new PatternLayout("%d{HH:mm:ss.SSS} %m%n");
	    final String filename = config.logProfileDir == null ? config.logProfileFilename : config.logProfileDir
		    + File.separatorChar + config.logProfileFilename;
	    try {
		final FileAppender fileAppender = new FileAppender(layout, filename, true);
		fileAppender.setName("com.atlassian.util.profiling");
		fileAppender.setThreshold(Priority.INFO);

		final LevelRangeFilter levelRangeFilter = new LevelRangeFilter();
		levelRangeFilter.setLevelMin(Level.INFO);
		levelRangeFilter.setLevelMax(Level.WARN);
		levelRangeFilter.setAcceptOnMatch(true);
		fileAppender.addFilter(levelRangeFilter);
		final DenyAllFilter denyAllFilter = new DenyAllFilter();
		fileAppender.addFilter(denyAllFilter);

		logger.addAppender(fileAppender);
	    } catch (IOException ex) {
		throw new Error(ex);
	    }
	}
    }

    public static Config getConfig() {
	return (Config) FenixFramework.getConfig();
    }

    public static DomainModel getDomainModel() {
	return FenixFramework.getDomainModel();
    }

}
