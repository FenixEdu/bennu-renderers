package pt.ist.fenixWebFramework.servlets.filters.contentRewrite;

import java.io.Serializable;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.UserAuthenticationListener;

public class RenderersSessionSecret {

    private static final String RENDERERS_SESSION_SECRET = "RENDERERS_SESSION_SECRET";

    static String computeSecretFromSession(HttpSession session) {
        if (session != null) {
            SessionSecretWrapper secret = (SessionSecretWrapper) session.getAttribute(RENDERERS_SESSION_SECRET);
            if (secret != null) {
                return secret.secret;
            } else {
                return null;
            }
        }
        return null;
    }

    private static final class SessionSecretWrapper implements Serializable {
        private static final long serialVersionUID = 828957763368790412L;
        private final String secret;

        SessionSecretWrapper(String secret) {
            this.secret = secret;
        }
    }

    public static final class RenderersUserAuthenticationListener implements UserAuthenticationListener {

        @Override
        public void onLogin(HttpSession session, User user) {
            SessionSecretWrapper secret = new SessionSecretWrapper(user.getUsername() + UUID.randomUUID().toString());
            session.setAttribute(RENDERERS_SESSION_SECRET, secret);
        }

        @Override
        public void onLogout(HttpSession session, User user) {
            session.removeAttribute(RENDERERS_SESSION_SECRET);
        }

    }

}
