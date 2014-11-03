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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import pt.ist.fenixWebFramework.rendererExtensions.util.ObjectChange;
import pt.ist.fenixWebFramework.rendererExtensions.util.ObjectKey;
import pt.ist.fenixWebFramework.renderers.model.CompositeSlotSetter;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectKey;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.model.SimpleMetaObject;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.WriteOnReadError;

public class DomainMetaObject extends SimpleMetaObject {

    private String externalId;

    private transient DomainObject object;

    protected DomainMetaObject() {
        super(null);
    }

    public DomainMetaObject(final DomainObject object) {
        this();
        setObject(object);
    }

    @Override
    public Object getObject() {
        if (this.object == null) {
            this.object = getPersistentObject();
        }
        return this.object;
    }

    @Override
    protected void setObject(final Object object) {
        this.object = (DomainObject) object;
        if (this.object != null) {
            this.externalId = this.object.getExternalId();
        }
    }

    protected DomainObject getPersistentObject() {
        return FenixFramework.getDomainObject(externalId);
    }

    public String getExternalId() {
        return externalId;
    }

    protected void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public Class getType() {
        return getObject().getClass();
    }

    @Override
    public MetaObjectKey getKey() {
        return new MetaObjectKey(getType(), getExternalId());
    }

    @Override
    protected void commit() {
        List<ObjectChange> changes = new ArrayList<ObjectChange>();

        ObjectKey key = new ObjectKey(getExternalId(), getType());

        for (MetaSlot slot : getAllSlots()) {
            if (slot.isSetterIgnored()) {
                continue;
            }

            if (slot.isCached()) {
                Object change = slot.getObject();
                ObjectChange objectChange = new ObjectChange(key, slot.getName(), change);
                changes.add(objectChange);
            }
        }

        for (CompositeSlotSetter compositeSetter : getCompositeSetters()) {
            try {
                changes.add(new ObjectChange(key, compositeSetter.getSetter(getType()), compositeSetter.getArgumentValues()));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("could not find specialized setter", e);
            }
        }

        callService(changes);
    }

    public static class ServicePredicateWithResult {

        final List<ObjectChange> changes;

        Object result = null;

        public ServicePredicateWithResult(final List<ObjectChange> changes) {
            this.changes = changes;
        }

        public Object execute() {
            Hashtable<ObjectKey, Object> objects = new Hashtable<ObjectKey, Object>();

            for (ObjectChange change : changes) {
                try {
                    Object object = getObject(objects, change);

                    processChange(change, object);
                } catch (InvocationTargetException e) {
                    if (e.getCause() != null && e.getCause() instanceof WriteOnReadError) {
                        throw (WriteOnReadError) e.getCause();
                    }
                    if (e.getCause() != null && e.getCause() instanceof RuntimeException) {
                        throw (RuntimeException) e.getCause();
                    }
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                } catch (NoSuchMethodException e) {
                    throw new Error(e);
                } catch (InstantiationException e) {
                    throw new Error(e);
                }
            }

            return objects.values();
        }

        protected void processChange(ObjectChange change, Object object) throws IllegalAccessException,
                InvocationTargetException, NoSuchMethodException, InstantiationException {
            if (change.slot != null) {
                setProperty(object, change.slot, change.value);
            } else if (change.setter != null) {
                invokeSetter(object, change.setter, change.values);
            }
        }

        private void invokeSetter(Object object, Method setter, Object[] values) {
            try {
                setter.invoke(object, values);
            } catch (InvocationTargetException e) {
                if (e.getCause() != null && e.getCause() instanceof WriteOnReadError) {
                    throw (WriteOnReadError) e.getCause();
                }
                if (e.getCause() != null && e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                } else {
                    throw new RuntimeException("error while invoking specialized setter", e.getTargetException());
                }
            } catch (Exception e) {
                throw new RuntimeException("error while invoking specialized setter", e);
            }
        }

        protected void setProperty(Object object, String slot, Object value) throws IllegalAccessException,
                InvocationTargetException, NoSuchMethodException, InstantiationException {
            Class type = getSlotType(object, slot);

            if (type == null) {
                throw new RuntimeException("could not find type of property " + slot + " in object " + object);
            }

            if (Collection.class.isAssignableFrom(type)) {
                setCollectionProperty(object, slot, (Collection) value);
            } else {
                try {
                    setSlotProperty(object, slot, value);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("the value '" + value + "' given for slot '" + slot
                            + "' does not match slot's type '" + type + "'");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("could not access to the slot '" + slot + "' of object '" + object
                            + "', probably is not public");
                }
            }
        }

        protected Class getSlotType(Object object, String slot) throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            return PropertyUtils.getPropertyType(object, slot);
        }

        protected void setSlotProperty(Object object, String slot, Object value) throws IllegalAccessException,
                InvocationTargetException, NoSuchMethodException, InstantiationException {
            PropertyUtils.setProperty(object, slot, value);
        }

        protected void setCollectionProperty(Object object, String slot, Collection list) throws IllegalAccessException,
                InvocationTargetException, NoSuchMethodException, InstantiationException {
            Collection relation = (Collection) getSlotProperty(object, slot);

            if (relation == null || isWriteableSlot(object, slot)) {
                relation = new ArrayList();
                relation.addAll(list);

                // ASSUMPTION: if collection is null then there is a setter that allows the value to be changed
                setSlotProperty(object, slot, relation);
            } else {
                // ASSUMPTION: changing the list affects the relation
                // TODO: cfgi, I hope this is ok but must check
                relation.clear();
                relation.addAll(list);
            }
        }

        protected boolean isWriteableSlot(Object object, String slot) {
            return PropertyUtils.isWriteable(object, slot);
        }

        protected Object getSlotProperty(Object object, String slot) throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException, InstantiationException {
            return PropertyUtils.getProperty(object, slot);
        }

        private Object getObject(Hashtable<ObjectKey, Object> objects, ObjectChange change) {
            Object object = objects.get(change.key);

            if (object == null) {
                object = getNewObject(change);
                objects.put(change.key, object);
            }

            return object;
        }

        protected DomainObject getNewObject(ObjectChange change) {
            return FenixFramework.getDomainObject(change.key.getExternalId());
        }

    }

    protected ServicePredicateWithResult getServiceToCall(final List<ObjectChange> changes) {
        return new ServicePredicateWithResult(changes);
    }

    @Atomic
    protected Object callService(final List<ObjectChange> changes) {
        return getServiceToCall(changes).execute();
    }

}
