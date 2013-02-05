package pt.ist.fenixWebFramework.renderers.exceptions;

public class NoSuchSchemaException extends RuntimeException {
    public NoSuchSchemaException(String schema) {
        super("Could not find schema named '" + schema + "'");
    }
}
