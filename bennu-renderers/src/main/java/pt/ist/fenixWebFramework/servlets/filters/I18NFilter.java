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
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.fenixedu.commons.i18n.I18N;

import pt.utl.ist.fenix.tools.util.i18n.Language;

/**
 * 
 * @author Luis Cruz
 */
public class I18NFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        // Handle Language Change from Renderers context
        if (request.getParameter("locale") != null) {
            String[] localeParts = request.getParameter("locale").split("_");
            Locale locale = localeParts.length == 1 ? new Locale(localeParts[0]) : new Locale(localeParts[0], localeParts[1]);
            HttpSession session = request.getSession();

            // Tell I18N to use the new locale
            I18N.setLocale(session, locale);

            // And also inform Struts
            session.setAttribute(Globals.LOCALE_KEY, locale);
        }

        try {
            // Fetch Locale from I18N
            Locale locale = I18N.getLocale();
            Language.setLocale(locale);
            request.setAttribute(Globals.LOCALE_KEY, locale);

            request.setAttribute("requestReconstructor", new RequestReconstructor(request));

            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            Language.setLocale(null);
        }
    }

}
