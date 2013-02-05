package pt.ist.fenixWebFramework.renderers.model;

import java.io.Serializable;

/**
 * The MetaObjectKey represents a meta object key, that is, an identifier that
 * allows to identify a meta object through out several requests. The identifier
 * can be used in the interface to refer to the meta object and to create
 * identifier unique to that meta object.
 * 
 * @author cfgi
 */
public class MetaObjectKey implements Serializable {

    private Class type;
    private long code;

    public MetaObjectKey(Class type, long code) {
        if (type == null) {
            throw new NullPointerException("type cannot be null");
        }

        this.type = type;
        this.code = code;
    }

    protected MetaObjectKey(MetaObjectKey key) {
        this.type = key.type;
        this.code = key.code;
    }

    protected Class getType() {
        return this.type;
    }

    protected long getCode() {
        return this.code;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MetaObjectKey)) {
            return false;
        }

        MetaObjectKey otherKey = (MetaObjectKey) other;
        return this.code == otherKey.code && this.type.equals(otherKey.type);
    }

    @Override
    public int hashCode() {
        return (int) (this.code + this.type.hashCode());
    }

    @Override
    public String toString() {
        return this.type.getName() + ":" + this.code;
    }
}
