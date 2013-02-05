package pt.ist.fenixWebFramework.servlets.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonObject {

    private Map<String, String> properties;

    public static final String OID_KEY = "oid";
    public static final String DESCRIPTION_KEY = "description";

    public JsonObject() {
        properties = new HashMap<String, String>();
    }

    public JsonObject(String oid, String description) {
        this();
        addAttribute(OID_KEY, oid);
        addAttribute(DESCRIPTION_KEY, description);
    }

    public void addAttribute(String key, String value) {
        properties.put(key, escape(value));
    }

    private String escape(String value) {
        return value.replaceAll("\"", "\\\\\"");
    }

    public String getJsonString() {
        StringBuilder builder = new StringBuilder("{");
        Iterator<String> iterator = properties.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.append("\"").append(key).append("\"").append(": \"").append(properties.get(key)).append("\"");
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append("}");
        return builder.toString();
    }

    public static String getJsonArrayString(List<JsonObject> jsonObjects) {
        StringBuilder builder = new StringBuilder("[");
        Iterator<JsonObject> iterator = jsonObjects.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next().getJsonString());
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();

    }
}
