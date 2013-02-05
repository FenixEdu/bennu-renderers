package pt.ist.fenixWebFramework.rendererExtensions.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.Action;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.pstm.Transaction;

public abstract class NavigationAction extends Action {

    public static final String NAVIGATION_SHOW = "show";

    protected long getGivenOid(HttpServletRequest request) {
        return Long.parseLong(request.getParameter("oid"));
    }

    protected String getGivenSchema(HttpServletRequest request) {
        return request.getParameter("schema");
    }

    protected String getGivenLayout(HttpServletRequest request) {
        return request.getParameter("layout");
    }

    protected DomainObject getTargetObject(HttpServletRequest request) {
        final long oid = getGivenOid(request);
        return Transaction.getObjectForOID(oid);
    }

}
