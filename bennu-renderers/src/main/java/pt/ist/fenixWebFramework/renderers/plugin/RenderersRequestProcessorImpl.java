package pt.ist.fenixWebFramework.renderers.plugin;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

import pt.ist.fenixWebFramework.renderers.components.state.LifeCycleConstants;
import pt.ist.fenixWebFramework.renderers.plugin.upload.RenderersRequestWrapper;
import pt.ist.fenixWebFramework.renderers.plugin.upload.StrutsFile;
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
 * This processor extends the tiles processor to easily integrate in an application that uses the tiles plugin.
 * 
 * @author cfgi
 */
public class RenderersRequestProcessorImpl {

    public static Class implementationClass = RenderersRequestProcessor.class;

    static ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<HttpServletRequest>();
    static ThreadLocal<ServletContext> currentContext = new ThreadLocal<ServletContext>();

    static ThreadLocal<Map<String, UploadedFile>> fileItems = new ThreadLocal<Map<String, UploadedFile>>();

    public static HttpServletRequest getCurrentRequest() {
        return RenderersRequestProcessorImpl.currentRequest.get();
    }

    public static ServletContext getCurrentContext() {
        return RenderersRequestProcessorImpl.currentContext.get();
    }

    /**
     * @return the form file associated with the given field name or <code>null</code> if no file exists
     */
    public static UploadedFile getUploadedFile(String fieldName) {
        return RenderersRequestProcessorImpl.fileItems.get().get(fieldName);
    }

    public static Collection<UploadedFile> getAllUploadedFiles() {
        return RenderersRequestProcessorImpl.fileItems.get().values();
    }

    public static String getCurrentEncoding() {
        HttpServletRequest currentRequest = getCurrentRequest();
        return currentRequest != null ? currentRequest.getCharacterEncoding() : null;
    }

    protected static HttpServletRequest parseMultipartRequest(HttpServletRequest request, ActionForm form) {
        Hashtable<String, UploadedFile> itemsMap = getNewFileItemsMap(request);

        if (form == null) {
            return request;
        } else {
            if (form.getMultipartRequestHandler() != null) {
                return createWrapperFromActionForm(request, itemsMap, form);
            } else {
                return request;
            }
        }
    }

    protected static Hashtable<String, UploadedFile> getNewFileItemsMap(final HttpServletRequest request) {
        Hashtable<String, UploadedFile> itemsMap =
                (Hashtable<String, UploadedFile>) request.getAttribute(FenixHttpServletRequestWrapper.ITEM_MAP_ATTRIBUTE);
        if (itemsMap == null) {
            itemsMap = new Hashtable<String, UploadedFile>();
        }
        RenderersRequestProcessorImpl.fileItems.set(itemsMap);
        return itemsMap;
    }

    protected static HttpServletRequest createWrapperFromActionForm(HttpServletRequest request,
            Hashtable<String, UploadedFile> itemsMap, ActionForm form) {
        RenderersRequestWrapper wrapper = new RenderersRequestWrapper(request);

        Hashtable<String, String[]> textElements = form.getMultipartRequestHandler().getTextElements();
        for (String name : textElements.keySet()) {
            String[] values = textElements.get(name);

            for (String value : values) {
                wrapper.addParameter(name, value);
            }
        }

        Hashtable<String, FormFile> fileElements = form.getMultipartRequestHandler().getFileElements();
        for (String name : fileElements.keySet()) {
            UploadedFile uploadedFile = new StrutsFile(fileElements.get(name));

            if (uploadedFile.getName() != null && uploadedFile.getName().length() > 0) {
                itemsMap.put(name, uploadedFile);
            }

            wrapper.addParameter(name, uploadedFile.getName());
        }

        Map<String, String[]> existingParameters = request.getParameterMap();
        for (String name : existingParameters.keySet()) {
            String[] values = existingParameters.get(name);
            for (String value : values) {
                wrapper.addParameter(name, value);
            }
        }

        Enumeration enumaration = request.getAttributeNames();
        while (enumaration.hasMoreElements()) {
            String name = (String) enumaration.nextElement();
            wrapper.setAttribute(name, request.getAttribute(name));
        }
        return wrapper;
    }

    protected static boolean hasViewState(HttpServletRequest request) {
        return viewStateNotProcessed(request)
                &&

                (request.getParameterValues(LifeCycleConstants.VIEWSTATE_PARAM_NAME) != null || request
                        .getParameterValues(LifeCycleConstants.VIEWSTATE_LIST_PARAM_NAME) != null);
    }

    protected static boolean viewStateNotProcessed(HttpServletRequest request) {
        return request.getAttribute(LifeCycleConstants.PROCESSED_PARAM_NAME) == null;
    }

    protected static void setViewStateProcessed(HttpServletRequest request) {
        request.setAttribute(LifeCycleConstants.PROCESSED_PARAM_NAME, true);
    }
}
