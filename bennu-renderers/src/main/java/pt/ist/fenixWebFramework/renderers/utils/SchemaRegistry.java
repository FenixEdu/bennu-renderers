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
package pt.ist.fenixWebFramework.renderers.utils;

import java.util.HashMap;
import java.util.Map;

import pt.ist.fenixWebFramework.renderers.exceptions.NoSuchSchemaException;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;

public class SchemaRegistry {

    private final Map<String, Schema> schemasTable = new HashMap<>();

    public SchemaRegistry() {
        super();
    }

    public void registerSchema(Schema schema) {
        schemasTable.put(schema.getName(), schema);
    }

    public Schema getSchema(String schemaName) {
        if (schemaName == null) {
            return null;
        }

        if (!schemasTable.containsKey(schemaName)) {
            throw new NoSuchSchemaException(schemaName);
        }

        return schemasTable.get(schemaName);
    }

    public boolean hasSchema(String schemaName) {
        return schemasTable.containsKey(schemaName);
    }
}
