package pt.ist.fenixWebFramework.renderers.converters;

import java.util.Locale;
import java.util.Locale.Builder;

import pt.ist.fenixWebFramework.renderers.components.converters.BiDirectionalConverter;

public class LocaleConverter extends BiDirectionalConverter {
    @Override
    public String deserialize(Object object) {
        return ((Locale) object).toLanguageTag();
    }

    @Override
    public Object convert(Class type, Object value) {
        return new Builder().setLanguageTag((String) value).build();
    }
}
