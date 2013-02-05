package pt.ist.fenixWebFramework.servlets.filters;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;

import pt.ist.fenixWebFramework.security.User;
import pt.ist.fenixWebFramework.security.UserView;
import pt.ist.fenixWebFramework.servlets.commons.CommonsFile;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

/**
 * 17/Fev/2003
 * 
 * @author jpvl
 */
public class RequestWrapperFilter implements Filter {

    private static SoftReference<Map<String, Boolean>> sessionLogHashRefs = null;

    private static void logSessionUsage(String arg0, Object arg1) {
        Map<String, Boolean> sessionLogHashs = sessionLogHashRefs == null ? null : sessionLogHashRefs.get();
        if (sessionLogHashs == null) {
            synchronized (RequestWrapperFilter.class) {
                sessionLogHashs = sessionLogHashRefs == null ? null : sessionLogHashRefs.get();
                if (sessionLogHashs == null) {
                    sessionLogHashs = new Hashtable<String, Boolean>();
                    sessionLogHashRefs = new SoftReference<Map<String, Boolean>>(sessionLogHashs);
                }
            }
        }
        final String classType = arg1 == null ? null : arg1.getClass().getName();
        final String hash = calcHash(classType);

        if (!sessionLogHashs.containsKey(hash)) {
            sessionLogHashs.put(hash, Boolean.TRUE);
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Adding object of type: ");
            stringBuilder.append(classType);
            stringBuilder.append(" to session as attribute: ");
            stringBuilder.append(arg0);
            stringBuilder.append(".\n");
            for (final StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                stringBuilder.append("   ");
                stringBuilder.append(stackTraceElement.getClassName());
                stringBuilder.append(".");
                stringBuilder.append(stackTraceElement.getMethodName());
                stringBuilder.append(" : ");
                stringBuilder.append(stackTraceElement.getLineNumber());
                stringBuilder.append("\n");
            }
            System.out.println(stringBuilder.toString());
        }

    }

    private static String calcHash(final String classType) {
        long hash = classType.hashCode();
        for (final StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            hash +=
                    stackTraceElement.getClassName().hashCode() + stackTraceElement.getMethodName().hashCode()
                            + stackTraceElement.getLineNumber();
        }
        return Long.toString(hash);
    }

    public static class FenixSessionWrapper implements HttpSession {

        public static Logger logger = Logger.getLogger(FenixSessionWrapper.class.getName());
        private HttpSession session;

        public FenixSessionWrapper(HttpSession session) {
            this.session = session;
        }

        @Override
        public Object getAttribute(String arg0) {
            return session.getAttribute(arg0);
        }

        @Override
        public Enumeration getAttributeNames() {
            return session.getAttributeNames();
        }

        @Override
        public long getCreationTime() {
            return session.getCreationTime();
        }

        @Override
        public String getId() {
            return session.getId();
        }

        @Override
        public long getLastAccessedTime() {
            return session.getLastAccessedTime();
        }

        @Override
        public int getMaxInactiveInterval() {
            return session.getMaxInactiveInterval();
        }

        @Override
        public ServletContext getServletContext() {
            return session.getServletContext();
        }

        @Override
        public HttpSessionContext getSessionContext() {
            return session.getSessionContext();
        }

        @Override
        public Object getValue(String arg0) {
            return session.getValue(arg0);
        }

        @Override
        public String[] getValueNames() {
            return session.getValueNames();
        }

        @Override
        public void invalidate() {
            session.invalidate();

        }

        @Override
        public boolean isNew() {
            return session.isNew();
        }

        @Override
        public void putValue(String arg0, Object arg1) {
            session.putValue(arg0, arg1);
        }

        @Override
        public void removeAttribute(String arg0) {
            session.removeAttribute(arg0);
        }

        @Override
        public void removeValue(String arg0) {
            session.removeValue(arg0);
        }

        @Override
        public void setAttribute(String arg0, Object arg1) {
            logSessionUsage(arg0, arg1);
            if (arg1 != null && !(arg1 instanceof Serializable)) {
                try {
                    throw new Error("Trying to find out where's the serialization problem");
                } catch (Error e) {
                    final StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("arg0: ");
                    stringBuilder.append(arg0);
                    stringBuilder.append(" - arg1: ");
                    stringBuilder.append(arg1);
                    stringBuilder.append("\n");
                    logger.warning(stringBuilder.toString());
                    e.printStackTrace();
                }
            }
            session.setAttribute(arg0, arg1);
        }

        @Override
        public void setMaxInactiveInterval(int arg0) {
            session.setMaxInactiveInterval(arg0);
        }

    }

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        chain.doFilter(getFenixHttpServletRequestWrapper(httpServletRequest), response);
        setSessionTimeout(httpServletRequest);
    }

    private void setSessionTimeout(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(7200);
        }
    }

    public FenixHttpServletRequestWrapper getFenixHttpServletRequestWrapper(final HttpServletRequest httpServletRequest) {
        return new FenixHttpServletRequestWrapper(httpServletRequest);
    }

    public static class FenixPrincipal implements Principal {

        @Override
        public String getName() {
            final User user = UserView.getUser();
            return user == null ? null : user.getUsername();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Principal) {
                final Principal principal = (Principal) obj;
                final String name = getName();
                return name != null && name.equals(principal.getName());
            }
            return false;
        }

        @Override
        public int hashCode() {
            final String name = getName();
            return name == null ? 0 : name.hashCode();
        }

        @Override
        public String toString() {
            return getName();
        }

    }

    public static class FenixHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public static final String ITEM_MAP_ATTRIBUTE = "FenixHttpServletRequestWrapper_itemsMap";

        private static final String PAGE_DEFAULT = "0";

        private static final String[] PAGE_DEFAULT_ARRAY = { PAGE_DEFAULT };

        final Hashtable<String, UploadedFile> itemsMap = new Hashtable<String, UploadedFile>();

        final Map<String, List<String>> parameters = new HashMap<String, List<String>>();

        public FenixHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            if (FileUpload.isMultipartContent(request)) {
                try {
                    parseRequest(request);
                } catch (FileUploadException e) {
                    throw new Error(e);
                } catch (UnsupportedEncodingException e) {
                    throw new Error(e);
                }
            }
            request.setAttribute(ITEM_MAP_ATTRIBUTE, itemsMap);
        }

        private void parseRequest(final HttpServletRequest request) throws FileUploadException, UnsupportedEncodingException {
            final List fileItems = new FileUpload(new DefaultFileItemFactory()).parseRequest(request);

            String characterEncoding = request.getCharacterEncoding();

            for (final Object object : fileItems) {
                final FileItem item = (FileItem) object;

                if (item.isFormField()) {
                    addParameter(item.getFieldName(),
                            characterEncoding != null ? item.getString(characterEncoding) : item.getString());
                } else {
                    UploadedFile uploadedFile = new CommonsFile(item);

                    String uploadFileName = uploadedFile.getName();
                    String decodedName = null;
                    if (uploadFileName != null && uploadFileName.length() > 0) {
                        itemsMap.put(item.getFieldName(), uploadedFile);
                        decodedName =
                                characterEncoding != null ? new String(uploadFileName.getBytes(), characterEncoding) : new String(
                                        uploadFileName.getBytes());
                    }

                    addParameter(item.getFieldName(), decodedName);
                }
            }
        }

        private void addParameter(final String fieldName, final String value) {
            List<String> strings = parameters.get(fieldName);
            if (strings == null) {
                strings = new ArrayList<String>();
                final String[] values = super.getParameterValues(fieldName);
                if (values != null) {
                    for (final String v : values) {
                        strings.add(v);
                    }
                }
                parameters.put(fieldName, strings);
            }
            strings.add(value);
        }

        @Override
        public Enumeration getParameterNames() {
            final Vector params = new Vector();

            final Enumeration paramEnum = super.getParameterNames();
            boolean gotPageParameter = false;
            while (paramEnum.hasMoreElements()) {
                final String parameterName = (String) paramEnum.nextElement();
                if (paramEnum.equals("page")) {
                    gotPageParameter = true;
                }
                params.add(parameterName);
            }
            if (!gotPageParameter) {
                params.add("page");
            }
            for (final String name : parameters.keySet()) {
                params.add(name);
            }

            return params.elements();
        }

        @Override
        public String[] getParameterValues(final String parameter) {
            if (parameters.containsKey(parameter)) {
                final List<String> parameterList = parameters.get(parameter);
                return parameterList.toArray(new String[0]);
            }
            final String[] parameterValues = super.getParameterValues(parameter);
            return parameterValues == null && parameter.equals("page") ? PAGE_DEFAULT_ARRAY : parameterValues;
        }

        @Override
        public String getParameter(final String parameter) {
            if (parameters.containsKey(parameter)) {
                final List<String> parameterList = parameters.get(parameter);
                return parameterList.size() > 0 ? parameterList.get(0) : null;
            }
            final String parameterValue = super.getParameter(parameter);
            return parameterValue == null && parameter.equals("page") ? PAGE_DEFAULT : parameterValue;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> resultMap = new Hashtable<String, String[]>(super.getParameterMap());

            for (String parameter : this.parameters.keySet()) {
                resultMap.put(parameter, parameters.get(parameter).toArray(new String[0]));
            }

            return resultMap;
        }

        @Override
        public HttpSession getSession() {
            return new FenixSessionWrapper(super.getSession());
        }

        @Override
        public boolean isUserInRole(String role) {
            final User user = UserView.getUser();
            return user == null ? false : user.hasRole(role);
        }

        @Override
        public String getRemoteUser() {
            final User user = UserView.getUser();
            return user == null ? super.getRemoteUser() : user.getUsername();
        }

        @Override
        public Principal getUserPrincipal() {
            return new FenixPrincipal();
        }

    }

}