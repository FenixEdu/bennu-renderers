package pt.ist.fenixWebFramework.servlets.filters;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import pt.utl.ist.fenix.tools.util.PropertiesManager;

public class PathAccessControlFilter implements Filter {
    private static final Logger logger = Logger.getLogger(PathAccessControlFilter.class);

    private static final String ACCESS_CONTROL_PROPERTIES = "/.pathAccessControl.properties";
    private static final String ACCESS_CONTROL_PATH_PREFIX = "host.control.path.";
    private static final String REQUEST_PATH_PARAM = "use-request-path";

    private Map<String, List<InetAddress>> pathConfiguration;

    private boolean requestPathUsed;

    public boolean isRequestPathUsed() {
        return this.requestPathUsed;
    }

    public void setRequestPathUsed(boolean requestPathUsed) {
        this.requestPathUsed = requestPathUsed;
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        setupParameters(config);
        setupConfiguration();
    }

    private void setupParameters(FilterConfig config) {
        setRequestPathUsed(false);

        if (config.getInitParameter(REQUEST_PATH_PARAM) != null) {
            setRequestPathUsed(new Boolean(config.getInitParameter(REQUEST_PATH_PARAM)));
        }
    }

    private void setupConfiguration() throws ServletException {
        this.pathConfiguration = new HashMap<String, List<InetAddress>>();

        try {
            Properties properties = new Properties();
            PropertiesManager.loadProperties(properties, ACCESS_CONTROL_PROPERTIES);

            for (Object key : properties.keySet()) {
                String keyName = (String) key;

                if (keyName.startsWith(ACCESS_CONTROL_PATH_PREFIX)) {
                    String path = keyName.substring(ACCESS_CONTROL_PATH_PREFIX.length());
                    String[] hostList = properties.get(key).toString().split(",");

                    for (String element : hostList) {
                        String host = element.trim();

                        if (host.length() > 0) {
                            addHost(path, host);
                        }
                    }

                }
            }
        } catch (IOException e) {
            throw new ServletException("failed to read the host access control properties '" + ACCESS_CONTROL_PROPERTIES + "'", e);
        }
    }

    private void addHost(String path, String host) {
        List<InetAddress> hostList = this.pathConfiguration.get(path);

        if (hostList == null) {
            hostList = new ArrayList<InetAddress>();

            this.pathConfiguration.put(path, hostList);
        }

        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);

            for (InetAddress addresse : addresses) {
                hostList.add(addresse);
            }
        } catch (UnknownHostException e) {
            logger.warn("could not find host '" + host + "', host ignored.");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        try {

            if (isAllowed(servletRequest.getRemoteAddr(), servletRequest.getServletPath(), servletRequest.getRequestURI())) {
                chain.doFilter(servletRequest, response);
            } else {
                reportError(servletRequest, servletResponse);
            }
        } catch (StackOverflowError ex) {
            System.err.println(ex.getMessage() + " processing request: " + servletRequest.getRequestURI()
                    + servletRequest.getQueryString());
            throw ex;
        }
    }

    private boolean isAllowed(final String address, final String servletPath, final String requestURI) {
        List<InetAddress> hostList = this.pathConfiguration.get(servletPath);

        try {
            final InetAddress remoteAddress = InetAddress.getByName(address);

            if (hostList != null) {
                for (final InetAddress allowedHost : hostList) {
                    if (remoteAddress.equals(allowedHost)) {
                        return true;
                    }
                }

                return false;
            }

            if (isRequestPathUsed()) {
                for (final String path : this.pathConfiguration.keySet()) {
                    if (requestURI.contains(path)) {
                        hostList = this.pathConfiguration.get(path);

                        for (InetAddress allowedHost : hostList) {
                            if (remoteAddress.equals(allowedHost)) {
                                return true;
                            }
                        }

                        return false;
                    }
                }
            }

            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private void reportError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access to this path is restrited");
    }

    @Override
    public void destroy() {
        // do nothing
    }

}
