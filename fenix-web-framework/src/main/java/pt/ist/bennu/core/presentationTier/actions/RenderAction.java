package pt.ist.bennu.core.presentationTier.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.bennu.dispatch.RenderersAnnotationProcessor.Forwarder;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/render")
public class RenderAction extends Action {
    private static Map<String, Forwarder> actionMap;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String path = request.getParameter("f");
        if (actionMap.containsKey(path)) {
            return actionMap.get(path).forward();
        }
        return null;
    }

    public static void initializeMap(Map<String, Forwarder> actionMap) {
        RenderAction.actionMap = actionMap;
    }
}