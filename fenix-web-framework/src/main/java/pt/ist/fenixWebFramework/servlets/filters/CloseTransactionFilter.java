package pt.ist.fenixWebFramework.servlets.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import pt.ist.fenixframework.Atomic;

public class CloseTransactionFilter implements Filter {

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    @Atomic(readOnly = true)
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        /* 
         * No need to do anything, due to @Atomic, FenixFramework will ensure
         * this method runs inside a read-only transaction.
         */
        chain.doFilter(request, response);
    }

}
