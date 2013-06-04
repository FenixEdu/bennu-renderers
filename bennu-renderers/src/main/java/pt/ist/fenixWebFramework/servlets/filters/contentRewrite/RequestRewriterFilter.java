package pt.ist.fenixWebFramework.servlets.filters.contentRewrite;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.ist.fenixWebFramework._development.LogLevel;

public class RequestRewriterFilter implements Filter {

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        final ResponseWrapper responseWrapper = new ResponseWrapper(httpServletResponse);

        if (LogLevel.DEBUG) {
            continueChainAndWriteResponseWithTimeLog(filterChain, httpServletRequest, responseWrapper);
        } else {
            continueChainAndWriteResponse(filterChain, httpServletRequest, responseWrapper);
        }
    }

    private void continueChainAndWriteResponse(final FilterChain filterChain, final HttpServletRequest httpServletRequest,
            final ResponseWrapper responseWrapper) throws IOException, ServletException {
        filterChain.doFilter(httpServletRequest, responseWrapper);
        writeResponse(filterChain, httpServletRequest, responseWrapper);
    }

    private void continueChainAndWriteResponseWithTimeLog(final FilterChain filterChain,
            final HttpServletRequest httpServletRequest, final ResponseWrapper responseWrapper) throws IOException,
            ServletException {
        final long start1 = System.currentTimeMillis();
        filterChain.doFilter(httpServletRequest, responseWrapper);
        final long end1 = System.currentTimeMillis();

        final long start2 = System.currentTimeMillis();
        writeResponse(filterChain, httpServletRequest, responseWrapper);
        final long end2 = System.currentTimeMillis();

        final long time1 = end1 - start1;
        final long time2 = end2 - start2;
        final long percent = time1 == 0 ? 0 : (100 * time2) / time1;
        System.out.println("Actual response took: " + time1 + " ms. Parse and replace took: " + time2 + " ms. Performance loss: "
                + percent + " %.");
    }

    protected void writeResponse(final FilterChain filterChain, final HttpServletRequest httpServletRequest,
            final ResponseWrapper responseWrapper) throws IOException, ServletException {
        responseWrapper.writeRealResponse(new GenericChecksumRewriter(httpServletRequest));
    }

}
