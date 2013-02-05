package pt.ist.fenixWebFramework.servlets.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.security.User;
import pt.ist.fenixWebFramework.security.UserView;

public class SetUserViewFilter implements Filter {

    public static final String USER_SESSION_ATTRIBUTE = "USER_SESSION_ATTRIBUTE";

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void destroy() {
    }

    protected HttpSession getHttpSession(final ServletRequest servletRequest) {
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            return httpServletRequest.getSession(false);
        }
        return null;
    }

    protected <T extends User> T getUserView(final ServletRequest servletRequest) {
        final HttpSession httpSession = getHttpSession(servletRequest);
        return (T) (httpSession == null ? null : httpSession.getAttribute(USER_SESSION_ATTRIBUTE));
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        final User user = getUserView(servletRequest);
        if (user != null) {
            final DateTime lastLogoutDateTime = user.getLastLogoutDateTime();
            if (lastLogoutDateTime == null || user.getUserCreationDateTime().isAfter(lastLogoutDateTime)) {
                UserView.setUser(user);
            } else {
                UserView.setUser(null);
            }
        } else {
            UserView.setUser(null);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserView.setUser(null);
        }
    }

}
