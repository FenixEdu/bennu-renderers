package org.fenixedu.bennu.portal;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;

import pt.ist.fenixWebFramework.servlets.filters.RequestWrapperFilter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.ResponseWrapper;

class StrutsSemanticURLHandler implements SemanticURLHandler {

    @Override
    public void handleRequest(MenuFunctionality functionality, HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        request.getRequestDispatcher(functionality.getItemKey()).forward(
                RequestWrapperFilter.getFenixHttpServletRequestWrapper(request), responseWrapper);
        responseWrapper.writeRealResponse(request.getSession(false));
    }

}
