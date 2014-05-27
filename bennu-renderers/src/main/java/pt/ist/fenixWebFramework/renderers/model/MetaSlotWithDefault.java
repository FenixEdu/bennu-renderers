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

public class MetaSlotWithDefault extends MetaSlot {

    private boolean createValue;

    public MetaSlotWithDefault(MetaObject metaObject, String name) {
        super(metaObject, name);

        this.createValue = true;
    }

    @Override
    public Object getObject() {
        if (this.createValue) {
            this.createValue = false;

            setObject(createDefault(getType(), getDefaultValue()));
        }

        return super.getObject();
    }

    @Override
    public void setObject(Object object) {
        super.setObject(object);
        this.createValue = false;
    }

    @Override
    public Class getType() {
        Class type = getMetaObject().getType();

        return RendererPropertyUtils.getPropertyType(type, getName());
    }

    protected Object createDefault(Class type, String defaultValue) {
        DefaultValues instance = DefaultValues.getInstance();
        return instance.createValue(type, defaultValue);
    }

}
