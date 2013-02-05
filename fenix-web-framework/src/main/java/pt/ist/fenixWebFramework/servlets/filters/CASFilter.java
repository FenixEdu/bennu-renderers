package pt.ist.fenixWebFramework.servlets.filters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.CharEncoding;

import pt.ist.fenixWebFramework.Config.CasConfig;
import pt.ist.fenixWebFramework.FenixWebFramework;

/**
 * 
 * @author naat
 * 
 */
public class CASFilter implements Filter {

    protected static final String URL_ENCODING = CharEncoding.UTF_8;

    @Override
    public void init(final FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws ServletException, IOException {
        final String serverName = servletRequest.getServerName();
        final CasConfig casConfig = FenixWebFramework.getConfig().getCasConfig(serverName);
        if (casConfig != null && casConfig.isCasEnabled()) {
            if (!isHttpResource(servletRequest, servletResponse)) {
                throw new ServletException("CASFilter only applies to HTTP resources");
            }

            if (notHasTicket(servletRequest)) {
                // send user to CAS to get a ticket
                redirectToCAS(casConfig, (HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    private boolean isHttpResource(final ServletRequest request, final ServletResponse response) {
        return request instanceof HttpServletRequest && response instanceof HttpServletResponse;
    }

    private boolean notHasTicket(final ServletRequest request) {
        final String ticket = request.getParameter("ticket");
        return ticket == null || ticket.equals("");
    }

    protected String encodeUrl(final String casUrl) throws UnsupportedEncodingException {
        return URLEncoder.encode(casUrl, URL_ENCODING);
    }

    /**
     * Redirects the user to CAS, determining the service from the request.
     */
    protected void redirectToCAS(final CasConfig casConfig, final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        final String serviceString = encodeUrl(casConfig.getServiceUrl());
        final String casLoginUrl = casConfig.getCasLoginUrl();
        final String casLoginString = casLoginUrl + "?service=" + serviceString;
        response.sendRedirect(casLoginString);
    }

    @Override
    public void destroy() {
    }

}
