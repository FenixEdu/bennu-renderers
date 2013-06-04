package pt.ist.fenixWebFramework.rendererExtensions.factories;

import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectCollection;
import pt.ist.fenixframework.Atomic;

public class DomainMetaObjectCollection extends MetaObjectCollection {

    private static final long serialVersionUID = -7897380281635901184L;

    @Atomic
    @Override
    public void commit() {
        for (MetaObject object : getAllMetaObjects()) {
            object.commit();
        }
    }

}
