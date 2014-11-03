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

import pt.ist.fenixWebFramework.renderers.utils.RendererPropertyUtils;

public class TransientMetaObject extends MetaObject {

    private transient Object object;
    private Class<?> type;
    private int code;

    public TransientMetaObject(Object object) {
        super();
        setObject(object);
    }

    protected void setObject(Object object) {
        this.object = object;
        this.code = object == null ? 0 : object.hashCode();
        this.type = object.getClass();
    }

    @Override
    public Object getObject() {
        return this.object;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public MetaObjectKey getKey() {
        return new MetaObjectKey(getType(), String.valueOf(this.code));
    }

    @Override
    protected void commit() {
        for (MetaSlot slot : getAllSlots()) {
            if (slot.isSetterIgnored()) {
                continue;
            }

            if (slot.isCached()) {
                Object value = slot.getObject();

                try {
                    setProperty(slot, value);
                } catch (Exception e) {
                    throw new RuntimeException("could not write property '" + slot.getName() + "' in object " + getObject(), e);
                }
            }
        }
    }

    protected void setProperty(MetaSlot slot, Object value) {
        RendererPropertyUtils.setProperty(getObject(), slot.getName(), value, false);
    }
}
