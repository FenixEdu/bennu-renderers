package pt.ist.fenixWebFramework.renderers.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DefaultUserIdentityFactory extends UserIdentityFactory {

    @Override
    public UserIdentity createUserIdentity(final HttpServletRequest request) {
        final HttpSession httpSession = request.getSession(false);
        final String sessionId = httpSession == null ? null : httpSession.getId();
        return new SimpleUserIdentity(sessionId);
    }

}
