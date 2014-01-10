package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public interface DataProvider {
    public Object provide(Object source, Object currentValue);

    public Converter getConverter();
}
