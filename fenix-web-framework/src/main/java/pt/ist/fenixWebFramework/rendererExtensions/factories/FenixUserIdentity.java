package pt.ist.fenixWebFramework.rendererExtensions.factories;

import java.util.Objects;

import pt.ist.bennu.core.domain.User;
import pt.ist.fenixWebFramework.renderers.model.UserIdentity;

public class FenixUserIdentity implements UserIdentity {

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
        if (obj instanceof FenixUserIdentity) {
            return Objects.equals(user, ((FenixUserIdentity) obj).user);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.user.hashCode();
    }

}
