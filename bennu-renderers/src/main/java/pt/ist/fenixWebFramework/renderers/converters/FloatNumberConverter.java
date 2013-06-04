package pt.ist.fenixWebFramework.renderers.converters;

import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class FloatNumberConverter extends Converter {

    @Override
    public Object convert(Class type, Object value) {
        String numberText = ((String) value).trim();

        if (numberText.length() == 0) {
            return null;
        }

        try {
            return Float.parseFloat(numberText);
        } catch (NumberFormatException e) {
            throw new ConversionException("renderers.converter.float", e, true, value);
        }
    }

}
