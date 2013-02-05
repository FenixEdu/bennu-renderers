package pt.ist.fenixWebFramework.renderers.components.converters;

import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public interface Convertible {
    public boolean hasConverter();

    public Converter getConverter();

    public Object getConvertedValue(MetaSlot slot);
}
