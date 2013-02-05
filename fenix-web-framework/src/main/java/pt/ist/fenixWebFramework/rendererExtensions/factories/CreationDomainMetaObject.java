package pt.ist.fenixWebFramework.rendererExtensions.factories;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import pt.ist.fenixWebFramework.rendererExtensions.util.ObjectChange;
import pt.ist.fenixWebFramework.rendererExtensions.util.ObjectKey;
import pt.ist.fenixWebFramework.renderers.model.InstanceCreator;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectKey;
import pt.ist.fenixframework.DomainObject;

public class CreationDomainMetaObject extends DomainMetaObject {
    private Class type;

    public CreationDomainMetaObject(Class type) {
        super();

        setType(type);
        setOid(0);
    }

    @Override
    public MetaObjectKey getKey() {
        return new CreationMetaObjectKey(getType());
    }

    @Override
    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    @Override
    protected Object callService(List<ObjectChange> changes) {
        Object result = super.callService(changes);
        setObject(((Collection) result).iterator().next());

        return result;
    }

    @Override
    protected DomainObject getPersistentObject() {
        return null;
    }

    public static class CreationServicePredicateWithResult extends ServicePredicateWithResult {

        DomainMetaObject domainMetaObject;

        public CreationServicePredicateWithResult(List<ObjectChange> changes, final DomainMetaObject domainMetaObject) {
            super(changes);
            this.domainMetaObject = domainMetaObject;
        }

        @Override
        public void execute() {
            beforeRun(changes);

            InstanceCreator instanceCreator = domainMetaObject.getInstanceCreator();
            if (instanceCreator != null) {
                long oid = domainMetaObject.getOid();
                ObjectKey key = new ObjectKey(oid, domainMetaObject.getType());

                try {
                    changes.add(0, new ObjectChange(key, instanceCreator.getConstructor(), instanceCreator.getArgumentValues()));
                } catch (Exception e) {
                    throw new RuntimeException("could not find constructor for '" + domainMetaObject.getType().getName()
                            + "' with arguments " + Arrays.asList(instanceCreator.getArgumentTypes()), e);
                }
            }

            super.execute();
        }

        @Override
        protected DomainObject getNewObject(ObjectChange change) {
            try {
                Class objectClass = change.key.getType();

                if (change.constructor != null) {
                    return (DomainObject) change.constructor.newInstance(change.values);
                } else {
                    return (DomainObject) objectClass.newInstance();
                }
            } catch (Exception e) {
                if (e.getCause() instanceof Error) {
                    throw (Error) e.getCause();
                } else if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }

                throw new Error(e);
            }
        }

    }

    @Override
    protected ServicePredicateWithResult getServiceToCall(List<ObjectChange> changes) {
        return new CreationServicePredicateWithResult(changes, this);
    }
}
