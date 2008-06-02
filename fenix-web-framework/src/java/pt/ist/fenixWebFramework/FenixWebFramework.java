package pt.ist.fenixWebFramework;

import java.util.Locale;

import pt.ist.fenixframework.FenixFramework;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import dml.DomainModel;

public class FenixWebFramework extends FenixFramework {

    private static final Object INIT_LOCK = new Object();

    public static void initialize(Config config) {
        synchronized (INIT_LOCK) {
            FenixFramework.initialize(config);

            final Locale locale = new Locale(config.getDefaultLanguage(), config.getDefaultLocation(), config.getDefaultVariant());
            Language.setDefaultLocale(locale);
        }
    }

    public static Config getConfig() {
        return (Config) FenixFramework.getConfig();
    }

    public static DomainModel getDomainModel() {
	return FenixFramework.getDomainModel();
    }

}
