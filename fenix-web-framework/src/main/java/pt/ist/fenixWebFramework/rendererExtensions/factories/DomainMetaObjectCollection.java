package pt.ist.fenixWebFramework.rendererExtensions.factories;

import java.util.List;

import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectCollection;
import pt.ist.fenixWebFramework.services.ServiceManager;
import pt.ist.fenixWebFramework.services.ServicePredicate;

public class DomainMetaObjectCollection extends MetaObjectCollection {

    @Override
    public void commit() {
        final List<MetaObject> metaObjects = getAllMetaObjects();
        final ServicePredicate servicePredicate = new ServicePredicate() {
            @Override
            public void execute() {
                for (MetaObject object : metaObjects) {
                    object.commit();
                }
            }
        };
        ServiceManager.execute(servicePredicate);
    }

}
