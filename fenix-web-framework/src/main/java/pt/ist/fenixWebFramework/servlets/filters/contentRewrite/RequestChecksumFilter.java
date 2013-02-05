package pt.ist.fenixWebFramework.servlets.filters.contentRewrite;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.CharEncoding;

import pt.ist.fenixWebFramework.FenixWebFramework;

public class RequestChecksumFilter implements Filter {

    private static final String ENCODING = CharEncoding.UTF_8;

    public static interface ChecksumPredicate {
        public boolean shouldFilter(HttpServletRequest request);
    }

    public static Set<ChecksumPredicate> predicates = new HashSet<ChecksumPredicate>();

    public static void registerFilterRule(ChecksumPredicate predicate) {
        predicates.add(predicate);
    }

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        if (FenixWebFramework.getConfig().getFilterRequestWithDigest()) {
            try {
                applyFilter(servletRequest, servletResponse, filterChain);
            } catch (UrlTamperingException ex) {
                final HttpServletRequest request = (HttpServletRequest) servletRequest;
                final HttpServletResponse response = (HttpServletResponse) servletResponse;
                final HttpSession httpSession = request.getSession(false);
                if (httpSession != null) {
                    httpSession.invalidate();
                }
                redirectByTampering(request, response);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    protected void redirectByTampering(HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.sendRedirect(FenixWebFramework.getConfig().getTamperingRedirect());
    }

    private void applyFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (shoudFilterReques(httpServletRequest)) {
            verifyRequestChecksum(httpServletRequest);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    protected boolean shoudFilterReques(final HttpServletRequest httpServletRequest) {
        for (ChecksumPredicate predicate : predicates) {
            if (!predicate.shouldFilter(httpServletRequest)) {
                return false;
            }
        }
        return true;
    }

    public static class UrlTamperingException extends Error {
        public UrlTamperingException() {
            super("error.url.tampering");
        }
    }

    private void verifyRequestChecksum(final HttpServletRequest httpServletRequest) {
        String checksum = httpServletRequest.getParameter(GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME);
        if (checksum == null || checksum.length() == 0) {
            checksum = (String) httpServletRequest.getAttribute(GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME);
        }
        if (!isValidChecksum(httpServletRequest, checksum)) {
            // if (LogLevel.ERROR) {
            // final User user = UserView.getUser();
            // final String userString = ((user == null) ? "<no user logged in>"
            // : user.getUsername()) + " key : "
            // + ((user == null) ? "No user" :
            // user.getPrivateConstantForDigestCalculation());
            // final String url = httpServletRequest.getRequestURI() + '?' +
            // httpServletRequest.getQueryString();
            // final StringBuilder stringBuilder = new StringBuilder();
            // stringBuilder.append("Detected url tampering by user: ");
            // stringBuilder.append(userString);
            // stringBuilder.append("\n           url: ");
            // stringBuilder.append(url);
            // stringBuilder.append("\n   decoded url iso-8859-1: ");
            // stringBuilder.append(decodeURL(url, "ISO-8859-1"));
            // stringBuilder.append("\n   decoded url utf-8: ");
            // stringBuilder.append(decodeURL(url, "UTF-8"));
            // stringBuilder.append("\n          from: ");
            // stringBuilder.append(httpServletRequest.getRemoteHost());
            // stringBuilder.append(" (");
            // stringBuilder.append(httpServletRequest.getRemoteAddr());
            // stringBuilder.append(")");
            // for (final Enumeration<String> headerNames =
            // httpServletRequest.getHeaderNames();
            // headerNames.hasMoreElements();) {
            // final String name = headerNames.nextElement();
            // stringBuilder.append("\n        header: ");
            // stringBuilder.append(name);
            // stringBuilder.append(" = ");
            // stringBuilder.append(httpServletRequest.getHeader(name));
            // }
            //
            // HttpSession session = httpServletRequest.getSession(false);
            // if (session != null) {
            // stringBuilder.append("\nSession creation: ");
            // stringBuilder.append(session.getCreationTime());
            // stringBuilder.append(" Session Id: ");
            // stringBuilder.append(session.getId());
            // stringBuilder.append(" Max inactive time: ");
            // stringBuilder.append(session.getMaxInactiveInterval());
            // stringBuilder.append(" Last time access: ");
            // stringBuilder.append(session.getLastAccessedTime());
            // stringBuilder.append(" Current time: ");
            // stringBuilder.append(System.currentTimeMillis());
            // }
            // System.out.println(stringBuilder.toString());
            // }
            throw new UrlTamperingException();
        }
    }

    private String decodeURL(final String url, final String encoding) {
        if (url == null) {
            return null;
        }
        try {
            return URLDecoder.decode(url, encoding);
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    private boolean isValidChecksum(final HttpServletRequest httpServletRequest, final String checksum) {
        final String uri = decodeURL(httpServletRequest.getRequestURI(), ENCODING);

        return isValidChecksum(uri, decodeURL(httpServletRequest.getQueryString(), ENCODING), checksum) ||

        isValidChecksum(uri, httpServletRequest.getQueryString(), checksum) ||

        isValidChecksumIgnoringPath(uri, checksum, decodeURL(httpServletRequest.getQueryString(), ENCODING)) ||

        isValidChecksumIgnoringPath(uri, checksum, httpServletRequest.getQueryString());

    }

    private boolean isValidChecksum(String uri, String queryString, String checksum) {
        String request = (queryString != null) ? uri + "?" + queryString : uri;
        return checksum != null && checksum.length() > 0 && checksum.equals(GenericChecksumRewriter.calculateChecksum(request));
    }

    private boolean isValidChecksumIgnoringPath(final String uri, final String checksum, String queryString) {
        if (uri.endsWith(".faces")) {
            final int lastSlash = uri.lastIndexOf('/');
            if (lastSlash >= 0) {
                final String chopedUri = uri.substring(lastSlash + 1);
                final String request = queryString != null ? chopedUri + '?' + queryString : chopedUri;
                final String calculatedChecksum = GenericChecksumRewriter.calculateChecksum(request);
                return checksum != null && checksum.length() > 0 && checksum.equals(calculatedChecksum);
            }
        }
        return false;
    }

}
