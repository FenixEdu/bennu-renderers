package pt.ist.fenixWebFramework.rendererExtensions.factories;

import pt.ist.fenixWebFramework.renderers.model.DefaultValues;
import pt.ist.fenixframework.DomainObject;

public class FenixDefaultValues extends pt.ist.fenixWebFramework.renderers.model.DefaultValues {

    public static DefaultValues getInstance() {
        if (DefaultValues.instance == null) {
            DefaultValues.instance = new FenixDefaultValues();
        }

        return DefaultValues.instance;
    }

    public DomainObject createValue(DomainObject o, Class type, String defaultValue) {
        return null;
    }
}
