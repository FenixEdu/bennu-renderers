package pt.ist.fenixWebFramework.renderers.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public interface ModuleResolver {

    public String maybeResolveModule(HttpServletRequest request);

    public String maybeResolveActionMapping(String mapping, PageContext ctx);

}
