package pt.ist.fenixWebFramework.rendererExtensions.converters;

import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

public class DomainObjectKeyConverter extends Converter {

    @Override
    public Object convert(Class type, Object value) {

        if (value == null || value.equals("")) {
            return null;
        }
        int index = ((String) value).indexOf(":");

        final String key = index > 0 ? ((String) value).substring(index + 1) : (String) value;
        try {
            return FenixFramework.getDomainObject(key);
        } catch (NumberFormatException e) {
            throw new ConversionException("invalid oid in key: " + key, e);
        }
    }

    public static String code(final DomainObject object) {
        return object.getExternalId();
    }

}
