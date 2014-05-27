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
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstanceCreator implements Serializable {

    private Class type;

    private List<MetaSlot> slots;
    private List<Class> argumentTypes;

    public InstanceCreator(Class type) {
        super();

        this.type = type;
        this.slots = new ArrayList<MetaSlot>();
        this.argumentTypes = new ArrayList<Class>();
    }

    public void addArgument(MetaSlot slot, Class type) {
        this.slots.add(slot);
        this.argumentTypes.add(type);
        slot.setSetterIgnored(true);
    }

    public Object createInstance() {
        try {
            Constructor constructor = getConstructor();
            Object[] values = getArgumentValues();

            return constructor.newInstance(values);
        } catch (Exception e) {
            throw new RuntimeException("failed to create instance of " + this.type.getName() + " with arguments "
                    + Arrays.asList(getArgumentTypes()), e);
        }
    }

    public Class[] getArgumentTypes() {
        return this.argumentTypes.toArray(new Class[0]);
    }

    public Constructor getConstructor() throws SecurityException, NoSuchMethodException {
        return this.type.getConstructor(getArgumentTypes());
    }

    public Object[] getArgumentValues() {
        Object[] values = new Object[this.slots.size()];

        for (int i = 0; i < values.length; i++) {
            values[i] = this.slots.get(i).getObject();
        }

        return values;
    }
}
