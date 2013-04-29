package pt.ist.fenixWebFramework.renderers.model;

public class PrimitiveMetaObjectKey extends MetaObjectKey {

    private final Object object;
    private final Class type;

    public PrimitiveMetaObjectKey(Object object, Class type) {
        super(type, null);

        this.object = object;
        this.type = type;
    }

    @Override
    public String toString() {
        return this.object == null ? "" : this.object.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PrimitiveMetaObjectKey)) {
            return false;
        }

        PrimitiveMetaObjectKey otherKey = (PrimitiveMetaObjectKey) obj;

        if (this.object == null) {
            if (otherKey.object != null) {
                return false;
            }
        } else {
            if (!this.object.equals(otherKey.object)) {
                return false;
            }
        }

        return this.type.equals(otherKey.type);
    }

    @Override
    public int hashCode() {
        return this.type.hashCode() + (this.object == null ? 0 : this.object.hashCode());
    }
}
