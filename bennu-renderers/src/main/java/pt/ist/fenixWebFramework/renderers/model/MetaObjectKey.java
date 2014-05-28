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

/**
 * The MetaObjectKey represents a meta object key, that is, an identifier that
 * allows to identify a meta object through out several requests. The identifier
 * can be used in the interface to refer to the meta object and to create
 * identifier unique to that meta object.
 * 
 * @author cfgi
 */
public class MetaObjectKey implements Serializable {

    private final Class type;
    private final String code;

    public MetaObjectKey(Class type, String code) {
        if (type == null) {
            throw new NullPointerException("type cannot be null");
        }

        this.type = type;
        this.code = code;
    }

    protected MetaObjectKey(MetaObjectKey key) {
        this.type = key.type;
        this.code = key.code;
    }

    protected Class getType() {
        return this.type;
    }

    protected String getCode() {
        return this.code;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MetaObjectKey)) {
            return false;
        }

        MetaObjectKey otherKey = (MetaObjectKey) other;
        return (this.code == null ? otherKey.code == null : this.code.equals(otherKey.code)) && this.type.equals(otherKey.type);
    }

    @Override
    public int hashCode() {
        return this.code.hashCode() + this.type.hashCode();
    }

    @Override
    public String toString() {
        return this.type.getName() + ":" + this.code;
    }
}
