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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.apache.struts.actions.DispatchAction;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.model.Application;
import org.fenixedu.bennu.portal.model.ApplicationRegistry;
import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.bennu.portal.servlet.PortalBackendRegistry;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.plugin.StrutsAnnotationsPlugIn;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RenderersSessionSecret.RenderersUserAuthenticationListener;

@HandlesTypes({ Mapping.class, StrutsApplication.class, StrutsFunctionality.class })
public class RenderersAnnotationProcessor implements ServletContainerInitializer {

    static final String DELEGATE = "$DELEGATE_TO_PARENT$";

    private static final Map<Class<?>, Functionality> functionalityClasses = new HashMap<Class<?>, Functionality>();

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext context) throws ServletException {
        PortalBackendRegistry.registerPortalBackend(new StrutsPortalBackend());
        Authenticate.addUserAuthenticationListener(new RenderersUserAuthenticationListener());

        if (classes != null) {
            Map<Class<?>, Application> applicationClasses = new HashMap<Class<?>, Application>();
            Set<Class<?>> actionsWithoutFunctionality = new HashSet<>();
            for (Class<?> type : classes) {
                Mapping mapping = type.getAnnotation(Mapping.class);
                if (mapping != null) {
                    StrutsAnnotationsPlugIn.registerMapping(type);
                    if (mapping.functionality() != Object.class) {
                        actionsWithoutFunctionality.add(type);
                    }
                }
                StrutsFunctionality functionality = type.getAnnotation(StrutsFunctionality.class);
                if (functionality != null) {
                    String bundle = "resources." + findBundleForFunctionality(type);
                    LocalizedString title = BundleUtil.getLocalizedString(bundle, functionality.titleKey());
                    LocalizedString description =
                            functionality.descriptionKey().equals(DELEGATE) ? title : BundleUtil.getLocalizedString(bundle,
                                    functionality.descriptionKey());
                    functionalityClasses.put(type, new Functionality(StrutsPortalBackend.BACKEND_KEY, computePath(type),
                            functionality.path(), findGroupForFunctionality(type), title, description));
                }
                StrutsApplication application = type.getAnnotation(StrutsApplication.class);
                if (application != null) {
                    String bundle = "resources." + application.bundle();
                    LocalizedString title = BundleUtil.getLocalizedString(bundle, application.titleKey());
                    LocalizedString description =
                            application.descriptionKey().equals(DELEGATE) ? title : BundleUtil.getLocalizedString(bundle,
                                    application.descriptionKey());
                    Application app =
                            new Application(type.getName(), application.path(), application.accessGroup(), title, description,
                                    application.hint());
                    applicationClasses.put(type, app);

                    // Register "Start-Page" functionality
                    if (functionality == null && mapping != null) {
                        registerStartPage(type, app);
                    }
                }
            }
            for (Entry<Class<?>, Functionality> entry : functionalityClasses.entrySet()) {
                Class<?> applicationClass =
                        entry.getKey().isAnnotationPresent(StrutsApplication.class) ? entry.getKey() : entry.getKey()
                                .getAnnotation(StrutsFunctionality.class).app();
                Application app = applicationClasses.get(applicationClass);
                if (app == null) {
                    throw new Error("Functionality " + entry.getKey().getName() + " does not have a defined application");
                }
                app.addFunctionality(entry.getValue());
            }

            for (Application app : applicationClasses.values()) {
                ApplicationRegistry.registerApplication(app);
            }

            for (Class<?> type : actionsWithoutFunctionality) {
                Class<?> functionalityType = type.getAnnotation(Mapping.class).functionality();
                Functionality functionality = functionalityClasses.get(functionalityType);
                if (functionality == null) {
                    throw new Error("Action type " + type.getName() + " declares " + functionalityType.getName()
                            + " but it is not a functionality!");
                }
                functionalityClasses.put(type, functionality);
            }
        }
    }

    private void registerStartPage(Class<?> type, Application app) {
        LocalizedString title = BundleUtil.getLocalizedString("resources.RendererResources", "label.start.page");
        final Functionality functionality =
                new Functionality(StrutsPortalBackend.BACKEND_KEY, computePath(type), "start-page", app.getAccessGroup(), title,
                        title, false);
        functionalityClasses.put(type, functionality);
    }

    private String findGroupForFunctionality(Class<?> type) {
        StrutsFunctionality functionality = type.getAnnotation(StrutsFunctionality.class);
        if (functionality.accessGroup().equals(DELEGATE)) {
            return functionality.app().getAnnotation(StrutsApplication.class).accessGroup();
        } else {
            return functionality.accessGroup();
        }
    }

    private String findBundleForFunctionality(Class<?> type) {
        StrutsFunctionality functionality = type.getAnnotation(StrutsFunctionality.class);
        if (functionality.bundle().equals(DELEGATE)) {
            return functionality.app().getAnnotation(StrutsApplication.class).bundle();
        } else {
            return functionality.bundle();
        }
    }

    private String computePath(Class<?> type) {
        Mapping mapping = type.getAnnotation(Mapping.class);
        StringBuilder path = new StringBuilder();

        if (!mapping.module().isEmpty()) {
            path.append('/').append(mapping.module());
        }

        path.append(mapping.path()).append(".do");

        if (DispatchAction.class.isAssignableFrom(type)) {
            path.append('?').append(mapping.parameter()).append('=').append(findEntryPoint(type));
        }

        return path.toString();
    }

    private String findEntryPoint(Class<?> actionClass) {
        Class<?> type = actionClass;
        while (type != DispatchAction.class) {
            for (Method method : type.getDeclaredMethods()) {
                if (method.isAnnotationPresent(EntryPoint.class)) {
                    return method.getName();
                }
            }
            type = type.getSuperclass();
        }
        throw new Error("Functionality class " + actionClass + " does not have a entry point!");
    }

    public static Functionality getFunctionalityForType(Class<?> actionClass) {
        return functionalityClasses.get(actionClass);
    }

}
