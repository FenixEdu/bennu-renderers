package pt.ist.fenixWebFramework.rendererExtensions.factories;

import pt.ist.fenixWebFramework.renderers.model.UserIdentity;
import pt.ist.fenixWebFramework.security.User;

public class FenixUserIdentity implements UserIdentity {
    
    /**
     * Serial version id. 
     */
    private static final long serialVersionUID = 1L;
    
    private User user;

    public FenixUserIdentity(final User user) {
        super();
        this.user = user;
    }

    public User getUserView() {
        return user;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof FenixUserIdentity)) {
            return false;
        }
        
        FenixUserIdentity other = (FenixUserIdentity) obj;
        
        if (this.user == null) {
            return other.user == null;
        }
        else {
            return this.user.equals(other.user);
        }
    }
    
    @Override
    public int hashCode() {
        return this.user.hashCode();
    }
    
}
