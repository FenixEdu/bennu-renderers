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
package org.fenixedu.bennu.struts.base;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Inteface used to mark an action as an exception handler for the renderers request processor.
 * 
 * @author cfgi
 */
public interface ExceptionHandler {
    /**
     * This method wil lbe called by the {@link RenderersRequestProcessor} when an exception occurs
     * during the processing of a viewstate. Those exceptions normally represent problems when converting
     * values or when updating certain object in the application's domain.
     * 
     * @param request the current request
     * @param mapping
     * @param input an action forward that maps to the place were the viewstate was created
     * @param e the exception that occured
     * 
     * @return an action forward representing were to go next or <code>null</code> to let struts decide
     */
    public ActionForward processException(HttpServletRequest request, ActionMapping mapping, ActionForward input, Exception e);
}
