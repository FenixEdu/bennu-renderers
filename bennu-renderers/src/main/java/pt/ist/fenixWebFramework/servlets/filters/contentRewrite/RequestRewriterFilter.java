package pt.ist.fenixWebFramework.servlets.filters.contentRewrite;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;

import pt.ist.fenixWebFramework.RenderersConfigurationManager;

public class RequestRewriterFilter implements Filter {

    private static final String RENDERERS_SESSION_SECRET = "DIGEST_SECRET_ATTRIBUTE";

    public static interface RequestRewriterFactory {
        public RequestRewriter createRequestRewriter(HttpServletRequest request);
    }

    private static final ConcurrentLinkedQueue<RequestRewriterFactory> rewriters = new ConcurrentLinkedQueue<>();

    public static void registerRequestRewriter(RequestRewriterFactory rewriter) {
        rewriters.add(rewriter);
    }

    private static final ThreadLocal<String> currentSecret = new InheritableThreadLocal<>();

    @Override
    public void init(FilterConfig config) {
        registerRequestRewriter(new RequestRewriterFactory() {
            @Override
            public RequestRewriter createRequestRewriter(HttpServletRequest request) {
                return new GenericChecksumRewriter(request);
            }
        });
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

        try {
            setSessionKey(httpServletRequest);
            filterChain.doFilter(httpServletRequest, responseWrapper);
            responseWrapper.writeRealResponse(httpServletRequest, rewriters);
        } finally {
            currentSecret.remove();
        }
    }

    private void setSessionKey(HttpServletRequest request) {
        if (RenderersConfigurationManager.getConfiguration().filterRequestWithDigest()) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                String secret = (String) session.getAttribute(RENDERERS_SESSION_SECRET);
                if (secret == null) {
                    secret = computeSecret(session);

                }
                currentSecret.set(secret);
            }
        }
    }

    private String computeSecret(HttpSession session) {
        User user = Authenticate.getUser();
        if (user != null) {
            String secret = user.getUsername() + UUID.randomUUID().toString();
            session.setAttribute(RENDERERS_SESSION_SECRET, secret);
        }
        // Logged-out users get no secret
        return null;
    }

    static String getSessionSecret() {
        return currentSecret.get();
    }

}
