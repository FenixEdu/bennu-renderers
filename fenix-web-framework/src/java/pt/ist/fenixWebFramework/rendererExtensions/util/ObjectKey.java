package pt.ist.fenixWebFramework.rendererExtensions.util;

public class ObjectKey {

    private final long oid;

    public ObjectKey(long oid) {
        super();
    
        this.oid = oid;
    }

    public long getOid() {
        return oid;
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof ObjectKey)) {
            return false;
        }
        ObjectKey otherKey = (ObjectKey) other;
        return this.oid == otherKey.oid;
    }

    @Override
    public int hashCode() {
        return (int) this.oid;
    }
}
