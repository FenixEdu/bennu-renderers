package pt.ist.fenixWebFramework.rendererExtensions.factories;

import javax.servlet.http.HttpServletRequest;

import pt.ist.fenixWebFramework.renderers.model.UserIdentity;
import pt.ist.fenixWebFramework.renderers.model.UserIdentityFactory;
import pt.ist.fenixWebFramework.security.User;
import pt.ist.fenixWebFramework.security.UserView;

public class FenixUserIdentityFactory extends UserIdentityFactory {

    @Override
    public UserIdentity createUserIdentity(HttpServletRequest request) {
        final User user = UserView.getUser();
        return new FenixUserIdentity(user);
    }

}
