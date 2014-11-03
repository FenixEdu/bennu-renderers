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

public class CreationMetaObject extends SimpleMetaObject {

    private Class type;

    public CreationMetaObject(Class type) {
        super(null);

        this.type = type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    @Override
    public Class getType() {
        return this.type;
    }

    @Override
    protected void commit() {
        if (getInstanceCreator() != null) {
            setObject(getInstanceCreator().createInstance());
        } else {
            try {
                setObject(getType().newInstance());
            } catch (Exception e) {
                throw new RuntimeException("could not create instance of type " + getType().getName()
                        + " using the default constructor");
            }
        }

        super.commit();
    }

    @Override
    protected void setProperty(MetaSlot slot, Object value) {
        RendererPropertyUtils.setProperty(getObject(), slot.getName(), value, true);
    }

}
