package pt.ist.fenixWebFramework.util;

import java.io.Serializable;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.pstm.Transaction;

/**
 * A <code>DomainReference</code> allows a serializable object to refer to a domain object. 
 * The <code>DomainReference</code> introduces an indirection point between the holder object 
 * and the domain object that avoids any data from the domain object to be stored in the 
 * serialization point.
 * 
 * @author cfgi
 */
public class DomainReference<T extends DomainObject> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long oid;

    transient T object;

    public DomainReference(final T object) {
        if (object == null) {
            this.object = null;
            this.oid = null;
        } else {
            this.object = object;
            this.oid = Long.valueOf(object.getOID());
        }
    }
    
    public DomainReference(long oid) {
        this.oid = Long.valueOf(oid);	
    }

    /**
     * Allows you to check if a certain {@link DomainReference} is a refenrece
     * to <code>null</code>. Note that, when a domain reference is a null
     * renference, you can still get a <code>null</code> value from
     * {@link #getObject()} when the referenced object no longer exists.
     * 
     * @return <code>true</code> if the domain reference is an explicit
     *         reference to <code>null</code>
     */
    public boolean isNullReference() {
        return oid == null;
    }
    
    public Long getOid() {
        return oid;
    }

    public Class getType() {
	final T object = getObject();
	return object == null ? null : object.getClass();
    }

    public T getObject() {
	if (object == null && oid != null) {
	    object = (T) Transaction.getObjectForOID(oid.longValue());
	}
	return object;
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof DomainReference)) {
            return false;
        }

        DomainReference otherReference = (DomainReference) other;

        if (this.getOid() == null && otherReference.getOid() != null) {
            return false;
        }
        
        if (this.getOid() != null && !this.getOid().equals(otherReference.getOid())) {
            return false;
        }
        
        if (this.getType() == null && otherReference.getType() != null) {
            return false;
        }
        
        if (this.getType() != null && !this.getType().equals(otherReference.getType())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int oidHash;
        int typeHash;
        
        oidHash  = getOid() == null ? 0 : getOid().hashCode();
        typeHash = getType() == null ? 0 : getType().hashCode();
        
        return oidHash + typeHash;
    }

    @Override
    public String toString() {
	return oid == null ? "<null>" : Long.toString(oid);
    }
    
}
