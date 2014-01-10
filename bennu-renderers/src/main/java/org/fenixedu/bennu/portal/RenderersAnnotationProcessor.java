package org.fenixedu.bennu.portal;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.fenixedu.bennu.core.domain.groups.Group;
import org.fenixedu.bennu.core.presentationTier.actions.ContextBaseAction;
import org.fenixedu.bennu.core.presentationTier.actions.RenderAction;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.domain.ApplicationInfo;
import org.fenixedu.bennu.portal.domain.BundleDetails;
import org.fenixedu.bennu.portal.domain.FunctionalityInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter.ChecksumPredicate;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixWebFramework.struts.plugin.StrutsAnnotationsPlugIn;

@HandlesTypes({ Mapping.class })
public class RenderersAnnotationProcessor implements ServletContainerInitializer {
    public static class Forwarder implements Serializable {
        private String path;

        private String groupExpression;

        private String redirect;

        private transient Group group;

        public Forwarder(String path, String redirect, String groupExpression) {
            this.path = path;
            this.redirect = redirect;
            this.groupExpression = groupExpression;
        }

        private Group group() {
            if (group == null) {
                group = Group.parse(groupExpression);
            }
            return group;
        }

        public ActionForward forward() {
            if (group().isMember(Authenticate.getUser())) {
                return new ActionForward(redirect + '&' + ContextBaseAction.CONTEXT_PATH + '=' + path);
            }
            return null;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(RenderersAnnotationProcessor.class);
    private static Map<String, Forwarder> actionMap = new HashMap<>();

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext context) throws ServletException {
        if (classes != null) {
            Map<Class<?>, ApplicationInfo> apps = new HashMap<>();
            for (Class<?> type : classes) {
                Mapping mapping = type.getAnnotation(Mapping.class);
                final Application app = type.getAnnotation(Application.class);
                if (app != null) {
                    extractApp(apps, type);
                }
                if (mapping != null) {
                    StrutsAnnotationsPlugIn.registerMapping(type);
                    scanEntryPoints(apps, mapping.path(), type);
                }
            }
            for (ApplicationInfo application : apps.values()) {
                AppServer.registerApp(application);
            }
            RenderAction.initializeMap(actionMap);
        }
        RequestChecksumFilter.registerFilterRule(new ChecksumPredicate() {
            @Override
            public boolean shouldFilter(HttpServletRequest request) {
                return !request.getRequestURI().endsWith("/render.do");
            }
        });
    }

    private void scanEntryPoints(Map<Class<?>, ApplicationInfo> apps, String path, Class<?> type) {
        for (Method method : type.getMethods()) {
            Functionality functionality = method.getAnnotation(Functionality.class);
            if (functionality != null) {
                extractFunctionality(apps, functionality);
                actionMap.put(functionality.app().getAnnotation(Application.class).path() + "/" + functionality.path(),
                        new Forwarder(path, path + ".do?method=" + method.getName(), functionality.group()));
            }
        }
    }

    public void extractFunctionality(Map<Class<?>, ApplicationInfo> apps, Functionality functionality) {
        extractApp(apps, functionality.app());
        BundleDetails details = new BundleDetails(functionality.bundle(), functionality.title(), functionality.description());
        final ApplicationInfo app = apps.get(functionality.app());
        final FunctionalityInfo functionalityInfo =
                new FunctionalityInfo(app.getPath() + "/" + functionality.path(), functionality.group(), details);
        app.addFunctionality(functionalityInfo);
    }

    private void extractApp(Map<Class<?>, ApplicationInfo> apps, Class<?> app) {
        if (apps.containsKey(app)) {
            return;
        }
        Application application = app.getAnnotation(Application.class);
        if (application != null) {
            if (!hasAppMethod(app)) {
                throw new Error(String.format("Application class %s doesn't have app method.", app.getName()));
            }
            BundleDetails details = new BundleDetails(application.bundle(), application.title(), application.description());
            apps.put(app, new ApplicationInfo("render.do?f=" + application.path(), application.group(), details));
            actionMap.put(application.path(),
                    new Forwarder(app.getAnnotation(Mapping.class).path(), app.getAnnotation(Mapping.class).path() + ".do"
                            + "?method=app", application.group()));
        } else {
            throw new Error();
        }
    }

    private boolean hasAppMethod(Class<?> app) {
        for (Method method : app.getMethods()) {
            if ("app".equals(method.getName())) {
                return true;
            }
        }
        return false;
    }
}