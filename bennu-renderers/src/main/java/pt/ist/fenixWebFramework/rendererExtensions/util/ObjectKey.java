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
package pt.ist.fenixWebFramework.rendererExtensions.util;

import java.util.Objects;

public class ObjectKey {

    private final String externalId;
    private final Class type;

    public ObjectKey(String externalId, Class type) {
        super();

        this.externalId = externalId;
        this.type = type;
    }

    public String getExternalId() {
        return externalId;
    }

    public Class getType() {
        return type;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ObjectKey) {
            if (!Objects.equals(type, ((ObjectKey) other).type)) {
                return false;
            }
            return Objects.equals(externalId, ((ObjectKey) other).externalId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.externalId) + Objects.hashCode(this.type);
    }
}
