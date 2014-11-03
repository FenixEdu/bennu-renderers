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
package pt.ist.fenixWebFramework.renderers.components.state;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.fenixedu.bennu.core.security.Authenticate;

public class EditRequest extends HttpServletRequestWrapper {

    private List<IViewState> viewStates;

    private final String publicModuleName = "publico";

    public EditRequest(HttpServletRequest request) {
        super(request);
    }

    public List<IViewState> getAllViewStates() throws IOException, ClassNotFoundException {
        if (this.viewStates == null) {
            this.viewStates = ViewState.decodeFromBase64(getParameter(LifeCycleConstants.VIEWSTATE_PARAM_NAME));
        }

        String contextPath = ((HttpServletRequest) getRequest()).getContextPath();
        String requestURI = ((HttpServletRequest) getRequest()).getRequestURI().toString();

        for (IViewState viewState : this.viewStates) {
            viewState.setRequest(this);

            checkUserIdentity(viewState, requestURI, contextPath);
        }

        return this.viewStates;
    }

    private void checkUserIdentity(IViewState viewState, String requestURI, String contextPath) {
        if (!requestURI.startsWith(contextPath + "/" + publicModuleName + "/")
                && !Objects.equals(viewState.getUser(), Authenticate.getUser())) {
            throw new ViewStateUserChangedException();
        }
    }

    public static class ViewStateUserChangedException extends RuntimeException {

        public ViewStateUserChangedException() {
            super("viewstate.user.changed");
        }
    }
}
