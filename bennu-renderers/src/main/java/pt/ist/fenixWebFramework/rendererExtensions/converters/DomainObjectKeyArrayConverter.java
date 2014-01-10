package pt.ist.fenixWebFramework.rendererExtensions.converters;

import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixframework.DomainObject;

public class DomainObjectKeyArrayConverter extends Converter {

    @Override
    public Object convert(Class type, Object value) {
        DomainObjectKeyConverter converter = new DomainObjectKeyConverter();
        List<DomainObject> result = new ArrayList<DomainObject>();

        String[] values = (String[]) value;
        for (String key : values) {
            result.add((DomainObject) converter.convert(type, key));
        }

        return result;
    }

}
