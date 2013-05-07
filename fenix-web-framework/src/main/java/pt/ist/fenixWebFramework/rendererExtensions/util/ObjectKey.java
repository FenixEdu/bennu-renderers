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
