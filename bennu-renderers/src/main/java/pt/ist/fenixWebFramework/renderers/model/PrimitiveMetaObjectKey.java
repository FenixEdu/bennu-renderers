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

public class PrimitiveMetaObjectKey extends MetaObjectKey {

    private final Object object;
    private final Class type;

    public PrimitiveMetaObjectKey(Object object, Class type) {
        super(type, null);

        this.object = object;
        this.type = type;
    }

    @Override
    public String toString() {
        return this.object == null ? "" : this.object.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PrimitiveMetaObjectKey)) {
            return false;
        }

        PrimitiveMetaObjectKey otherKey = (PrimitiveMetaObjectKey) obj;

        if (this.object == null) {
            if (otherKey.object != null) {
                return false;
            }
        } else {
            if (!this.object.equals(otherKey.object)) {
                return false;
            }
        }

        return this.type.equals(otherKey.type);
    }

    @Override
    public int hashCode() {
        return this.type.hashCode() + (this.object == null ? 0 : this.object.hashCode());
    }
}
