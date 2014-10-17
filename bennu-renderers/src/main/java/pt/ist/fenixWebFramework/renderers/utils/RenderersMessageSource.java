package pt.ist.fenixWebFramework.renderers.utils;

import java.util.Locale;
import java.util.Optional;

public interface RenderersMessageSource {

    public Optional<String> getMessage(Locale locale, String message);

}
