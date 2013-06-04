package pt.ist.fenixWebFramework.renderers.components.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import pt.ist.fenixWebFramework.renderers.model.UserIdentity;
import pt.ist.fenixWebFramework.renderers.model.UserIdentityFactory;

public class EditRequest extends HttpServletRequestWrapper {

    private List<IViewState> viewStates;

    private String publicModuleName = "publico";

    public EditRequest(HttpServletRequest request) {
        super(request);
    }

    public List<IViewState> getAllViewStates() throws IOException, ClassNotFoundException {
        if (this.viewStates == null) {
            String[] encodedViewStates = getParameterValues(LifeCycleConstants.VIEWSTATE_PARAM_NAME);
            if (encodedViewStates != null) {
                this.viewStates = new ArrayList<IViewState>();

                for (String encodedSingleViewState : encodedViewStates) {
                    IViewState viewState = ViewState.decodeFromBase64(encodedSingleViewState);
                    this.viewStates.add(viewState);
                }
            } else {
                this.viewStates = ViewState.decodeListFromBase64(getParameter(LifeCycleConstants.VIEWSTATE_LIST_PARAM_NAME));
            }
        }

        String contextPath = ((HttpServletRequest) getRequest()).getContextPath();
        String requestURI = ((HttpServletRequest) getRequest()).getRequestURI().toString();

        UserIdentity userIdentity = UserIdentityFactory.create(this);
        for (IViewState viewState : this.viewStates) {
            viewState.setRequest(this);

            checkUserIdentity(viewState, userIdentity, requestURI, contextPath);
        }

        return this.viewStates;
    }

    private void checkUserIdentity(IViewState viewState, UserIdentity userIdentity, String requestURI, String contextPath) {
        if (!requestURI.startsWith(contextPath + "/" + publicModuleName + "/") && !viewState.getUser().equals(userIdentity)) {
            throw new ViewStateUserChangedException();
        }
    }

    public static class ViewStateUserChangedException extends RuntimeException {

        public ViewStateUserChangedException() {
            super("viewstate.user.changed");
        }
    }
}
