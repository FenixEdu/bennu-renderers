/**
 * Copyright © 2008 Instituto Superior Técnico
 *
 * This file is part of Bennu Renderers Framework.
 *
 * Bennu Renderers Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Renderers Framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Renderers Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
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

import pt.ist.fenixWebFramework.RenderersConfigurationManager;

import com.google.common.base.Charsets;

public class RequestChecksumFilter implements Filter {

    private static final String ENCODING = Charsets.UTF_8.name();

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
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (RenderersConfigurationManager.getConfiguration().filterRequestWithDigest()) {
            if (shouldValidateChecksum(request) && !isValidChecksum(request)) {
                handleInvalidChecksum(request, (HttpServletResponse) servletResponse);
                return;
            }
        }
        ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);
        filterChain.doFilter(servletRequest, responseWrapper);
        responseWrapper.writeRealResponse(request.getSession(false));
    }

    protected void handleInvalidChecksum(HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            httpSession.invalidate();
        }
        response.sendRedirect(RenderersConfigurationManager.getConfiguration().tamperingRedirect());
    }

    protected boolean shouldValidateChecksum(final HttpServletRequest httpServletRequest) {
        for (ChecksumPredicate predicate : predicates) {
            if (!predicate.shouldFilter(httpServletRequest)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidChecksum(final HttpServletRequest httpServletRequest) {
        String checksum = httpServletRequest.getParameter(GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME);
        if (checksum == null || checksum.length() == 0) {
            checksum = (String) httpServletRequest.getAttribute(GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME);
        }
        return isValidChecksum(httpServletRequest, checksum);
    }

    public static String decodeURL(final String url, final String encoding) {
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
        final HttpSession session = httpServletRequest.getSession(false);
        final String uri = decodeURL(httpServletRequest.getRequestURI(), ENCODING);
        return isValidChecksum(uri, decodeURL(httpServletRequest.getQueryString(), ENCODING), checksum, session)
                || isValidChecksum(uri, httpServletRequest.getQueryString(), checksum, session);
    }

    private boolean isValidChecksum(String uri, String queryString, String checksum, HttpSession session) {
        String request = (queryString != null) ? uri + "?" + queryString : uri;
        return checksum != null && checksum.length() > 0
                && checksum.equals(GenericChecksumRewriter.calculateChecksum(request, session));
    }

}
