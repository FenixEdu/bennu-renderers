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

/**
 * The MetaSlotKey represents an indentifier that allows to identify in a single
 * meta slot of a meta object. The string representation of this key can be
 * obtained by invoking {@link #toString()} and used in the interface to generate
 * identifiers that are unique to a certain slot.
 * 
 * @see pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent#setTargetSlot(MetaSlotKey)
 * 
 * @author cfgi
 */
public class MetaSlotKey extends MetaObjectKey {

    private String name;

    public MetaSlotKey(MetaObject metaObject, String name) {
        super(metaObject.getKey());

        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MetaSlotKey)) {
            return false;
        }

        MetaSlotKey otherSlotKey = (MetaSlotKey) other;
        return super.equals(other) && this.name.equals(otherSlotKey.name);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.name.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + ":" + this.name;
    }
}
