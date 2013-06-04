package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.converters.FloatNumberConverter;

/**
 * {@inheritDoc}
 * 
 * This renderer converts the value to a float with {@link Float#parseFloat(java.lang.String)}.
 * 
 * @author cfgi
 */
public class FloatInputRenderer extends NumberInputRenderer {

    @Override
    protected Converter getConverter() {
        return new FloatNumberConverter();
    }

}
