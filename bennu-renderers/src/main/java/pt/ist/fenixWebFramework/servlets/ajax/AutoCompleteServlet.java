package pt.ist.fenixWebFramework.servlets.ajax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter.UrlTamperingException;
import pt.ist.fenixWebFramework.servlets.json.JsonObject;

public abstract class AutoCompleteServlet extends HttpServlet {

    public static final String STYLE_CLASS = "styleClass";

    public static final String VALUE_FIELD = "valueField";

    public static final String FORMAT = "format";

    public static final String LABEL_FIELD = "labelField";

    public static final String MAX_COUNT = "maxCount";

    private static final String JAVASCRIPT_LIBRARY_ENCODING = CharEncoding.UTF_8;

    private static final int DEFAULT_MAX_COUNT = 20;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding(JAVASCRIPT_LIBRARY_ENCODING);

        //let's take care of the checksum validation
        try {
            validateChecksum(request);

        } catch (UrlTamperingException ex) {
            if (request.getSession() != null) {
                request.getSession().invalidate();
            }
            response.setContentType("application/json; charset=" + JAVASCRIPT_LIBRARY_ENCODING);
            response.sendError(response.SC_BAD_REQUEST);
        }

        String value = new String(request.getParameter("value").getBytes(), JAVASCRIPT_LIBRARY_ENCODING);
        Map<String, String> argsMap = getArgsMap(request.getParameter("args"));
        int maxCount = getNumber(request.getParameter(MAX_COUNT), DEFAULT_MAX_COUNT);

        Collection result = getSearchResult(argsMap, value, maxCount);

        String labelField = request.getParameter(LABEL_FIELD);
        String format = request.getParameter(FORMAT);
        String valueField = request.getParameter(VALUE_FIELD);
        String styleClass = request.getParameter(STYLE_CLASS);

        response.setContentType("application/json; charset=" + JAVASCRIPT_LIBRARY_ENCODING);
        response.getWriter().write(getResponseHtml(result, labelField, format, valueField, styleClass, maxCount));
    }

    private void validateChecksum(HttpServletRequest request) throws RequestChecksumFilter.UrlTamperingException {
        String checksum = request.getParameter(GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME);
        if (checksum == null || checksum.length() == 0) {
            checksum = (String) request.getAttribute(GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME);
        }

        if (!isValidChecksum(request, checksum)) {
            throw new RequestChecksumFilter.UrlTamperingException();
        }

    }

    private boolean isValidChecksum(final HttpServletRequest request, final String checksum) {
        //let's just extract the meaningful/important parameters. i.e. the ones
        //parsed to the getResponseHtml
        String checksumRelevantString = "";
        checksumRelevantString += request.getParameter(MAX_COUNT);
        checksumRelevantString += request.getParameter(LABEL_FIELD);
        checksumRelevantString += request.getParameter(FORMAT);
        checksumRelevantString += request.getParameter(VALUE_FIELD);
        checksumRelevantString += request.getParameter(STYLE_CLASS);

        return checksum.length() > 0
                && (checksum.equals(GenericChecksumRewriter.calculateChecksum(checksumRelevantString)) || checksum
                        .equals(GenericChecksumRewriter.calculateChecksum(RequestChecksumFilter.decodeURL(checksumRelevantString,
                                JAVASCRIPT_LIBRARY_ENCODING))));

    }

    private int getNumber(String parameter, int defaultValue) {
        if (parameter == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(parameter);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    protected abstract Collection getSearchResult(Map<String, String> argsMap, String value, int maxCount);

    private Map<String, String> getArgsMap(String encodedServiceArgs) {
        String[] serviceArgsArray = StringUtils.split(encodedServiceArgs, ',');
        Map<String, String> serviceArgsMap = new HashMap<String, String>();

        for (String serviceArg : serviceArgsArray) {
            String[] argNameArgValue = StringUtils.split(serviceArg, '=');
            serviceArgsMap.put(argNameArgValue[0], argNameArgValue[1]);
        }

        return serviceArgsMap;
    }

    private String getResponseHtml(Collection result, String labelField, String format, String valueField, String styleClass,
            int maxCount) {
        List<JsonObject> jsonObjects = new ArrayList<JsonObject>();
        try {
            int count = 0;
            for (final Object element : result) {
                if (count++ >= maxCount) {
                    break;
                }

                final String labelProperty = BeanUtils.getProperty(element, labelField);
                if (format == null) {
                    jsonObjects.add(new JsonObject(BeanUtils.getProperty(element, valueField), labelProperty));
                } else {
                    jsonObjects.add(new JsonObject(BeanUtils.getProperty(element, valueField), RenderUtils
                            .getFormattedProperties(format, element)));
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error getting field property (see label and value fields)", ex);

        }

        return JsonObject.getJsonArrayString(jsonObjects);
    }
}
