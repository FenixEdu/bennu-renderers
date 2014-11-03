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
package org.fenixedu.bennu.struts.portal;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.bennu.portal.servlet.BennuPortalDispatcher;
import org.fenixedu.bennu.portal.servlet.PortalBackend;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;

public class StrutsPortalBackend implements PortalBackend {

    public static final String BACKEND_KEY = "struts";

    private static final SemanticURLHandler HANDLER = new StrutsSemanticURLHandler();

    @Override
    public SemanticURLHandler getSemanticURLHandler() {
        return HANDLER;
    }

    @Override
    public boolean requiresServerSideLayout() {
        return true;
    }

    @Override
    public String getBackendKey() {
        return BACKEND_KEY;
    }

    public static boolean chooseSelectedFunctionality(HttpServletRequest request, Class<?> actionClass) {
        if (BennuPortalDispatcher.getSelectedFunctionality(request) == null) {
            Functionality model = RenderersAnnotationProcessor.getFunctionalityForType(actionClass);
            if (model == null) {
                // Action class does not contain a functionality
                return true;
            }
            MenuFunctionality functionality = MenuFunctionality.findFunctionality(BACKEND_KEY, model.getKey());
            if (functionality == null || !functionality.isAvailableForCurrentUser()) {
                return false;
            }
            BennuPortalDispatcher.selectFunctionality(request, functionality);
        }
        return true;
    }

}
