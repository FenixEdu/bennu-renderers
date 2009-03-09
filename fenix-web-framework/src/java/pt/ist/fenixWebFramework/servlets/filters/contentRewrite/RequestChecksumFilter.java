package pt.ist.fenixWebFramework.servlets.filters.contentRewrite;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixWebFramework._development.LogLevel;
import pt.ist.fenixWebFramework.security.User;
import pt.ist.fenixWebFramework.security.UserView;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

public class RequestChecksumFilter implements Filter {

    public void init(FilterConfig config) {
    }

    public void destroy() {
    }

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
		response.sendRedirect(FenixWebFramework.getConfig().getTamperingRedirect());
	    }
	} else {
	    filterChain.doFilter(servletRequest, servletResponse);
	}
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
	    if (LogLevel.ERROR) {
		final User user = UserView.getUser();
		final String userString = ((user == null) ? "<no user logged in>" : user.getUsername())
			+ " digest in current user view: " + UserView.getUser().getPrivateConstantForDigestCalculation();
		final String url = httpServletRequest.getRequestURI() + '?' + httpServletRequest.getQueryString();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Detected url tampering by user: ");
		stringBuilder.append(userString);
		stringBuilder.append("\n           url: ");
		stringBuilder.append(url);
		stringBuilder.append("\n   decoded url iso-8859-1: ");
		stringBuilder.append(decodeURL(url, "ISO-8859-1"));
		stringBuilder.append("\n   decoded url utf-8: ");
		stringBuilder.append(decodeURL(url, "UTF-8"));
		stringBuilder.append("\n          from: ");
		stringBuilder.append(httpServletRequest.getRemoteHost());
		stringBuilder.append(" (");
		stringBuilder.append(httpServletRequest.getRemoteAddr());
		stringBuilder.append(")");
		for (final Enumeration<String> headerNames = httpServletRequest.getHeaderNames(); headerNames.hasMoreElements();) {
		    final String name = headerNames.nextElement();
		    stringBuilder.append("\n        header: ");
		    stringBuilder.append(name);
		    stringBuilder.append(" = ");
		    stringBuilder.append(httpServletRequest.getHeader(name));
		}

		HttpSession session = httpServletRequest.getSession(false);
		if (session != null) {
		    stringBuilder.append("\nSession creation: ");
		    stringBuilder.append(session.getCreationTime());
		    stringBuilder.append(" Session Id: ");
		    stringBuilder.append(session.getId());
		    stringBuilder.append(" Max inactive time: ");
		    stringBuilder.append(session.getMaxInactiveInterval());
		    stringBuilder.append(" Last time access: ");
		    stringBuilder.append(session.getLastAccessedTime());
		    stringBuilder.append(" Current time: ");
		    stringBuilder.append(System.currentTimeMillis());
		}
		System.out.println(stringBuilder.toString());
	    }
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
	final String uri = decodeURL(httpServletRequest.getRequestURI(), "ISO-8859-1");

	return isValidChecksum(uri, decodeURL(httpServletRequest.getQueryString(), "ISO-8859-1"), checksum) ||

	isValidChecksum(uri, httpServletRequest.getQueryString(), checksum) ||

	isValidChecksumIgnoringPath(httpServletRequest, checksum, "ISO-8859-1");

    }

    private boolean isValidChecksum(String uri, String queryString, String checksum) {
	String request = (queryString != null) ? uri + "?" + queryString : uri;
	return checksum != null && checksum.length() > 0 && checksum.equals(GenericChecksumRewriter.calculateChecksum(request));
    }

    private boolean isValidChecksumIgnoringPath(final HttpServletRequest httpServletRequest, final String checksum,
	    final String encoding) {
	final String uri = decodeURL(httpServletRequest.getRequestURI(), encoding);
	if (uri.endsWith(".faces")) {
	    final int lastSlash = uri.lastIndexOf('/');
	    if (lastSlash >= 0) {
		final String chopedUri = uri.substring(lastSlash + 1);
		final String queryString = decodeURL(httpServletRequest.getQueryString(), encoding);
		final String request = queryString != null ? chopedUri + '?' + queryString : chopedUri;
		final String calculatedChecksum = GenericChecksumRewriter.calculateChecksum(request);
		return checksum != null && checksum.length() > 0 && checksum.equals(calculatedChecksum);
	    }
	}
	return false;
    }

}
