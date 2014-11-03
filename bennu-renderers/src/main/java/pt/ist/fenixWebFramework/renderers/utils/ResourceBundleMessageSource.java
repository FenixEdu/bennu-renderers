package pt.ist.fenixWebFramework.renderers.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ResourceBundleMessageSource implements RenderersMessageSource {

    private final String bundleName;

    public ResourceBundleMessageSource(String bundleName) {
        this.bundleName = bundleName;
    }

    @Override
    public Optional<String> getMessage(Locale locale, String key) {
        try {
            if (key == null) {
                return Optional.empty();
            }
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
            return bundle.containsKey(key) ? Optional.of(bundle.getString(key)) : Optional.empty();
        } catch (MissingResourceException e) {
            return Optional.empty();
        }
    }

}
