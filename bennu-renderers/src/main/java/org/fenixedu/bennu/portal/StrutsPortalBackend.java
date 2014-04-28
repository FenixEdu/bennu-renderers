package org.fenixedu.bennu.portal;

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
