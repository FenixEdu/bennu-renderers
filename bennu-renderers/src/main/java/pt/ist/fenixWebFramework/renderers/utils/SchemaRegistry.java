package pt.ist.fenixWebFramework.renderers.utils;

import java.util.Hashtable;
import java.util.Map;

import pt.ist.fenixWebFramework.renderers.exceptions.NoSuchSchemaException;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;

public class SchemaRegistry {

    private Map<String, Schema> schemasTable = null;

    public SchemaRegistry() {
        super();

        schemasTable = new Hashtable<String, Schema>();
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
}
