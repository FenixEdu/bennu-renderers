package pt.ist.fenixWebFramework.renderers.plugin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import pt.ist.fenixWebFramework.renderers.components.state.LifeCycleConstants;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;
import pt.ist.fenixWebFramework.servlets.filters.RequestWrapperFilter.FenixHttpServletRequestWrapper;

/**
 * The standard renderers request processor. This processor is responsible for
 * handling any viewstate present in the request. It will parse the request,
 * retrieve all viewstates, and start the necessary lifecycle associated with
 * them before continuing with the standard struts processing.
 * 
 * <p>
 * If any exception is thrown during the processing of a viewstate it will be handled by struts like if the exceptions occured in
 * the destiny action. This default behaviour can be overriden by making the destiny action implement the
 * {@link pt.ist.fenixWebFramework.renderers.plugin.ExceptionHandler} interface.
 * 
 * <p>
 * The processor ensures that the current request and context are available through {@link #getCurrentRequest()} and
 * {@link #getCurrentContext()} respectively during the entire request lifetime. The processor also process multipart requests to
 * allow any renderer to retrieve on uploaded file with {@link #getUploadedFile(String)}.
 * 
 * <p>
 * 
 * @author cfgi
 */
public class RenderersRequestProcessorImpl {

    static final ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<>();

    public static HttpServletRequest getCurrentRequest() {
        return currentRequest.get();
    }

    /**
     * @return the form file associated with the given field name or <code>null</code> if no file exists
     */
    @SuppressWarnings("unchecked")
    public static UploadedFile getUploadedFile(String fieldName) {
        Map<String, UploadedFile> map =
                (Map<String, UploadedFile>) getCurrentRequest().getAttribute(FenixHttpServletRequestWrapper.ITEM_MAP_ATTRIBUTE);
        return map == null ? null : map.get(fieldName);
    }

    public static String getCurrentEncoding() {
        HttpServletRequest currentRequest = getCurrentRequest();
        return currentRequest != null ? currentRequest.getCharacterEncoding() : null;
    }

    protected static boolean hasViewState(HttpServletRequest request) {
        return viewStateNotProcessed(request)
                && (request.getParameterValues(LifeCycleConstants.VIEWSTATE_PARAM_NAME) != null || request
                        .getParameterValues(LifeCycleConstants.VIEWSTATE_LIST_PARAM_NAME) != null);
    }

    protected static boolean viewStateNotProcessed(HttpServletRequest request) {
        return request.getAttribute(LifeCycleConstants.PROCESSED_PARAM_NAME) == null;
    }

    protected static void setViewStateProcessed(HttpServletRequest request) {
        request.setAttribute(LifeCycleConstants.PROCESSED_PARAM_NAME, true);
    }
}
