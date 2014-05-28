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
package pt.ist.fenixWebFramework.renderers.model;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixframework.core.WriteOnReadError;

public class CompositeSlotSetter implements Serializable {
    private MetaObject metaObject;

    private final String setterName;
    private final List<MetaSlot> slots;
    private final List<Class> types;

    public CompositeSlotSetter(MetaObject metaObject, String setterName) {
        super();

        this.metaObject = metaObject;
        this.setterName = setterName;

        this.slots = new ArrayList<MetaSlot>();
        this.types = new ArrayList<Class>();
    }

    public MetaObject getMetaObject() {
        return this.metaObject;
    }

    public String getSetterName() {
        return this.setterName;
    }

    public void setMetaObject(MetaObject metaObject) {
        this.metaObject = metaObject;
    }

    public void addArgument(MetaSlot slot, Class type) {
        this.slots.add(slot);
        this.types.add(type);
        slot.setSetterIgnored(true);
    }

    public void executeSetter() {
        Object object = this.metaObject.getObject();

        try {
            Method method = getSetter(object.getClass());
            Object[] values = getArgumentValues();

            method.invoke(object, values);
        } catch (RuntimeException e) {
            throw e;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof WriteOnReadError) {
                throw (WriteOnReadError) e.getCause();
            }
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Method getSetter(Class type) throws NoSuchMethodException {
        return type.getMethod(getSetterName(), getArgumentTypes());
    }

    public Object[] getArgumentValues() {
        Object[] values = new Object[this.slots.size()];

        int i = 0;
        for (MetaSlot slot : this.slots) {
            values[i++] = slot.getObject();
        }

        return values;
    }

    public Class[] getArgumentTypes() {
        return this.types.toArray(new Class[0]);
    }
}
