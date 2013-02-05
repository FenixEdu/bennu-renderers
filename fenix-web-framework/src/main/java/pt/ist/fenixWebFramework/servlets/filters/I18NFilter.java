/*
 * Created on 2005/05/13
 * 
 */
package pt.ist.fenixWebFramework.servlets.filters;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;

import pt.utl.ist.fenix.tools.util.i18n.Language;

/**
 * 
 * @author Luis Cruz
 */
public class I18NFilter implements Filter {

    public static final String LOCALE_KEY = I18NFilter.class.getName() + "_LOCAL_KEY";

    ServletContext servletContext;

    FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        this.servletContext = filterConfig.getServletContext();
    }

    @Override
    public void destroy() {
        this.servletContext = null;
        this.filterConfig = null;
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final String localParameter = request.getParameter("locale");
        final HttpSession httpSession;
        final Locale locale;
        if (localParameter != null) {
            final String[] localTokens = localParameter.split("_");
            locale = localTokens.length > 1 ? new Locale(localTokens[0], localTokens[1]) : new Locale(localParameter);
            httpSession = getOrCreateSession(request);
        } else {
            httpSession = request.getSession(true);
            final Locale localeFromSession = (Locale) httpSession.getAttribute(LOCALE_KEY);
            locale = localeFromSession == null ? Language.getDefaultLocale() : localeFromSession;
        }

        setRequestReconstructor(request);
        setLocale(request, httpSession, locale);
        filterChain.doFilter(request, response);
    }

    private HttpSession getOrCreateSession(final HttpServletRequest request) {
        final HttpSession httpSession = request.getSession(false);
        return httpSession == null ? request.getSession(true) : httpSession;
    }

    public static void setDefaultLocale(final HttpServletRequest httpServletRequest, final HttpSession httpSession) {
        setLocale(httpServletRequest, httpSession, Language.getDefaultLocale());
    }

    public static void setLocale(final HttpServletRequest httpServletRequest, final HttpSession httpSession, final Locale locale) {
        httpSession.removeAttribute(LOCALE_KEY);
        httpSession.setAttribute(LOCALE_KEY, locale);
        httpSession.removeAttribute(Globals.LOCALE_KEY);
        httpSession.setAttribute(Globals.LOCALE_KEY, locale);
        httpServletRequest.removeAttribute(Globals.LOCALE_KEY);
        httpServletRequest.setAttribute(Globals.LOCALE_KEY, locale);
        Language.setLocale(locale);
    }

    private void setRequestReconstructor(final HttpServletRequest request) {
        request.setAttribute("requestReconstructor", new RequestReconstructor(request));
    }

}
