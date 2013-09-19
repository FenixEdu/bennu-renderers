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
        if (!(other instanceof ObjectKey)) {
            return false;
        }

        ObjectKey otherKey = (ObjectKey) other;

        if (this.type == null && otherKey.type != null) {
            return false;
        }

        if (this.type != null && !this.type.equals(otherKey.type)) {
            return false;
        }

        return this.externalId == null ? otherKey.externalId == null : this.externalId.equals(otherKey.externalId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.externalId) + Objects.hashCode(this.type);
    }
}
