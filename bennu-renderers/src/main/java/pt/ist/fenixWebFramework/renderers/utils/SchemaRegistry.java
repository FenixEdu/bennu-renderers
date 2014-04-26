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
