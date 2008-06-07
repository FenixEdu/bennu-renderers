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

import pt.ist.fenixWebFramework.security.UserView;

public class SetUserViewFilter implements Filter {

    public static final String USER_SESSION_ATTRIBUTE = "USER_SESSION_ATTRIBUTE";

    public void init(FilterConfig config) {
    }

    public void destroy() {
    }

    protected HttpSession getHttpSession(final ServletRequest servletRequest) {
	if (servletRequest instanceof HttpServletRequest) {
	    final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
	    return httpServletRequest.getSession(false);
	}
	return null;
    }

    protected <T> T getUserView(final ServletRequest servletRequest) {
	final HttpSession httpSession = getHttpSession(servletRequest);
	return (T) (httpSession == null ? null : httpSession.getAttribute(USER_SESSION_ATTRIBUTE));
    }

    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
    		throws IOException, ServletException {
	UserView.setUser(getUserView(servletRequest));
	filterChain.doFilter(servletRequest, servletResponse);
    }

}

