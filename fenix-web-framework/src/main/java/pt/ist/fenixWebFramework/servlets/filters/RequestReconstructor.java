package pt.ist.fenixWebFramework.servlets.filters;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import pt.utl.ist.fenix.tools.util.Pair;

public class RequestReconstructor {

    final StringBuilder url = new StringBuilder();

    final List<Pair<String, String>> attributes = new ArrayList<Pair<String, String>>();

    public RequestReconstructor(final HttpServletRequest httpServletRequest) {
        url.append(httpServletRequest.getContextPath());
        url.append(httpServletRequest.getServletPath());

        final String queryString = httpServletRequest.getQueryString();
        for (final Object object : httpServletRequest.getParameterMap().keySet()) {
            String key = (String) object;
            if (key != null && keepKey(key)) {
                final boolean isParam;
                if (queryString == null) {
                    isParam = false;
                } else {
                    final int paramIndex = queryString.indexOf(key);
                    final int nextChar = paramIndex + key.length();
                    isParam =
                            paramIndex >= 0 && queryString.length() > nextChar && queryString.charAt(nextChar) == '='
                                    && (paramIndex == 0 || queryString.charAt(paramIndex - 1) == '&');
                }
                for (final String value : httpServletRequest.getParameterValues(key)) {
                    if (isParam) {
                        addParameter(key, value);
                    } else {
                        addAttribute(key, value);
                    }
                }
            }
        }

        for (final Enumeration<String> e = httpServletRequest.getAttributeNames(); e.hasMoreElements();) {
            final String key = e.nextElement();
            if (!key.equals("locale")) {
                final Object object = httpServletRequest.getAttribute(key);
                if (object.getClass().isArray()) {
                    for (final Object value : java.util.Arrays.asList(object)) {
                        if (value instanceof String) {
                            addAttribute(key, (String) value);
                        }
                    }
                } else if (object instanceof String) {
                    addAttribute(key, (String) object);
                } else {
                    // 	Not sure how to procede here...
                }
            }
        }
    }

    private boolean keepKey(final String key) {
        return !key.equals("locale") && !key.equals("_request_checksum_"); // && !key.equals("contentContextPath_PATH");
    }

    private void addAttribute(final String key, final String value) {
        attributes.add(new Pair<String, String>(key, value));
    }

    private void addParameter(final String key, final String value) {
        final char prefix = url.indexOf("?") < 0 ? '?' : '&';
        url.append(prefix);
        url.append(key);
        url.append('=');
        url.append(value);
    }

    public StringBuilder getUrl() {
        return url;
    }

    public String getUrlSwitch(final String locale) {
        final char prefix = url.indexOf("?") < 0 ? '?' : '&';
        return url.toString() + prefix + "locale=" + locale;
    }

    public List<Pair<String, String>> getAttributes() {
        return attributes;
    }

}
