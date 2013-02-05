package pt.ist.fenixWebFramework.servlets.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class ProfilingFilter implements Filter {

    private final Logger logger = Logger.getLogger("pt.ist.fenixWebFramework.servlets.filters.ProfilingFilter");

    @Override
    public void init(final FilterConfig arg0) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final String uri = httpServletRequest.getRequestURI();

        final long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            final long end = System.currentTimeMillis();
            log(uri, start, end);
        }
    }

    protected void log(final String uri, final long start, final long end) {
        final long time = end - start;

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(time);
        stringBuilder.append("ms] - ");
        stringBuilder.append(uri);
        logger.info(stringBuilder.toString());
    }

}
