package pt.ist.fenixWebFramework.rendererExtensions.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.Action;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

public abstract class NavigationAction extends Action {

    public static final String NAVIGATION_SHOW = "show";

    protected String getGivenSchema(HttpServletRequest request) {
        return request.getParameter("schema");
    }

    protected String getGivenLayout(HttpServletRequest request) {
        return request.getParameter("layout");
    }

    protected DomainObject getTargetObject(HttpServletRequest request) {
        return FenixFramework.getDomainObject(request.getParameter("oid"));
    }
}
