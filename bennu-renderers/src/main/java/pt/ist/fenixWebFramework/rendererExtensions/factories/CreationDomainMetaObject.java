/**
 * Copyright © 2008 Instituto Superior Técnico
 *
 * This file is part of Bennu Renderers Framework.
 *
 * Bennu Renderers Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Renderers Framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Renderers Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    private final Class type;

    public CreationDomainMetaObject(Class type) {
        super();

        this.type = type;
        setExternalId(null);
    }

    @Override
    public MetaObjectKey getKey() {
        return new CreationMetaObjectKey(getType());
    }

    @Override
    public Class getType() {
        return type;
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

    public class CreationServicePredicateWithResult extends ServicePredicateWithResult {

        public CreationServicePredicateWithResult(List<ObjectChange> changes) {
            super(changes);
        }

        @Override
        public Object execute() {
            InstanceCreator instanceCreator = CreationDomainMetaObject.this.getInstanceCreator();
            if (instanceCreator != null) {
                String oid = CreationDomainMetaObject.this.getExternalId();
                ObjectKey key = new ObjectKey(oid, CreationDomainMetaObject.this.getType());

                try {
                    changes.add(0, new ObjectChange(key, instanceCreator.getConstructor(), instanceCreator.getArgumentValues()));
                } catch (Exception e) {
                    throw new RuntimeException("could not find constructor for '"
                            + CreationDomainMetaObject.this.getType().getName() + "' with arguments "
                            + Arrays.asList(instanceCreator.getArgumentTypes()), e);
                }
            }

            return super.execute();
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
        return new CreationServicePredicateWithResult(changes);
    }
}
