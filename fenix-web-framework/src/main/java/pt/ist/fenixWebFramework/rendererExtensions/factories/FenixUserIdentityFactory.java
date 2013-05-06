package pt.ist.fenixWebFramework.rendererExtensions.factories;

import javax.servlet.http.HttpServletRequest;

import pt.ist.bennu.core.security.Authenticate;
import pt.ist.fenixWebFramework.renderers.model.UserIdentity;
import pt.ist.fenixWebFramework.renderers.model.UserIdentityFactory;

public class FenixUserIdentityFactory extends UserIdentityFactory {
    @Override
    public UserIdentity createUserIdentity(HttpServletRequest request) {
        return new FenixUserIdentity(Authenticate.getUser());
    }
}
