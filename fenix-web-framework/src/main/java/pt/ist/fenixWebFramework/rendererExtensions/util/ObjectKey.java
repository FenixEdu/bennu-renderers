package pt.ist.fenixWebFramework.rendererExtensions.util;

public class ObjectKey {

    private final long oid;
    private final Class type;

    public ObjectKey(long oid, Class type) {
        super();

        this.oid = oid;
        this.type = type;
    }

    public long getOid() {
        return oid;
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

        return this.oid == otherKey.oid;
    }

    @Override
    public int hashCode() {
        return ((int) this.oid) + (this.type == null ? 0 : this.type.hashCode());
    }
}
