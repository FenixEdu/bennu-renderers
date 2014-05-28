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
/* 
* @(#)ExceptionHandlerFilter.java 
* 
* Copyright 2009 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package org.fenixedu.bennu.core.presentationTier.servlets.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Artur Ventura
 * @author João Neves
 * @author Pedro Santos
 * @author João Figueiredo
 * @author Luis Cruz
 * 
 * @deprecated
 *             Use Bennu's ExceptionHandlerFilter instead
 */
@Deprecated
public class ExceptionHandlerFilter implements Filter {

    @Deprecated
    public static interface CustomHandler {

        public boolean isCustomizedFor(final Throwable t);

        public void handle(final HttpServletRequest httpServletRequest, final ServletResponse response, final Throwable t)
                throws ServletException, IOException;

    }

    private static final List<CustomHandler> customHandlers = new ArrayList<CustomHandler>();

    public static void register(final CustomHandler customHandler) {
        customHandlers.add(customHandler);
    }

    public static void unregister(final CustomHandler customHandler) {
        customHandlers.remove(customHandler);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        try {
            filterChain.doFilter(request, response);
        } catch (final Throwable t) {
            for (final CustomHandler customHandler : customHandlers) {
                if (customHandler.isCustomizedFor(t)) {
                    customHandler.handle(httpServletRequest, response, t);
                    return;
                }
            }
            throw t;
        }
    }
}
