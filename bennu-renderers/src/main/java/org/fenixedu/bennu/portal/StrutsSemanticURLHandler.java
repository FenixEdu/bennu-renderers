/**
 * Copyright © 2008 Instituto Superior Técnico
 *
 * This file is part of Bennu Renderers Framework.
 *
 * Bennu Renderers Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Renderers Framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Renderers Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
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
