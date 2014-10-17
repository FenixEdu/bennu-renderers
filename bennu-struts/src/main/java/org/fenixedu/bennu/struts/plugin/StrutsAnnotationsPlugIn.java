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
/**
 * 
 */
package org.fenixedu.bennu.struts.plugin;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ExceptionConfig;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.config.ModuleConfig;
import org.fenixedu.bennu.struts.annotations.ExceptionHandling;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Input;
import org.fenixedu.bennu.struts.annotations.Mapping;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
public class StrutsAnnotationsPlugIn implements PlugIn {

    private static final String INPUT_PAGE_AND_METHOD = ".do?page=0&method=";

    private static final String INPUT_DEFAULT_PAGE_AND_METHOD = ".do?page=0&method=prepare";

    private static final List<String> UPPER_BOUND_SUPERCLASSES = Arrays.asList("DispatchAction", "Action", "Object");

    private static final Set<Class<?>> actionClasses = new HashSet<Class<?>>();

    @Override
    public void destroy() {
    }

    @Override
    public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {

        final String modulePrefix = CharMatcher.is('/').trimLeadingFrom(config.getPrefix());

        for (Class<?> actionClass : actionClasses) {
            Mapping mapping = actionClass.getAnnotation(Mapping.class);
            if (mapping == null || !modulePrefix.equals(mapping.module())) {
                continue;
            }

            final ActionMapping actionMapping = new ActionMapping();

            actionMapping.setPath(mapping.path());
            actionMapping.setType(actionClass.getName());
            actionMapping.setScope(mapping.scope());
            actionMapping.setParameter(mapping.parameter());
            actionMapping.setValidate(mapping.validate());

            if (mapping.formBeanClass() != ActionForm.class) {
                final String formName = mapping.formBeanClass().getName();
                createFormBeanConfigIfNecessary(config, mapping, formName);
                actionMapping.setName(formName);
            } else if (!mapping.formBean().isEmpty()) {
                actionMapping.setName(mapping.formBean());
            }

            if (mapping.input().isEmpty()) {
                actionMapping.setInput(findInputMethod(actionClass, mapping));
            } else {
                actionMapping.setInput(mapping.input());
            }

            for (final Forward forward : actionClass.getAnnotationsByType(Forward.class)) {
                registerForward(actionMapping, forward);
            }
            registerSuperclassForwards(actionMapping, actionClass.getSuperclass());

            registerExceptionHandling(actionMapping, actionClass);

            config.addActionConfig(actionMapping);

        }
    }

    private static void registerExceptionHandling(final ActionMapping actionMapping, Class<?> actionClass) {
        for (final ExceptionHandling exception : actionClass.getAnnotationsByType(ExceptionHandling.class)) {
            final ExceptionConfig exceptionConfig = new ExceptionConfig();

            Class<? extends Exception> exClass = exception.type();
            Class<? extends ExceptionHandler> handlerClass = exception.handler();

            exceptionConfig.setKey(Strings.emptyToNull(exception.key()));
            exceptionConfig.setHandler(handlerClass.getName());
            exceptionConfig.setType(exClass.getName());

            if (!Strings.isNullOrEmpty(exception.path())) {
                exceptionConfig.setPath(exception.path());
            }

            if (!Strings.isNullOrEmpty(exception.scope())) {
                exceptionConfig.setScope(exception.scope());
            }

            actionMapping.addExceptionConfig(exceptionConfig);
        }
    }

    private static void registerSuperclassForwards(final ActionMapping actionMapping, Class<?> superclass) {
        if (UPPER_BOUND_SUPERCLASSES.contains(superclass.getSimpleName())) {
            return;
        }
        for (final Forward forward : superclass.getAnnotationsByType(Forward.class)) {
            try {
                actionMapping.findForward(forward.name());
            } catch (NullPointerException ex) {
                // Forward wasn't registered in any subclass, so register it.
                registerForward(actionMapping, forward);
            }
        }
        registerSuperclassForwards(actionMapping, superclass.getSuperclass());
    }

    @SuppressWarnings("deprecation")
    private static void registerForward(final ActionMapping actionMapping, final Forward forward) {
        actionMapping.addForwardConfig(new ActionForward(forward.name(), forward.path(), forward.redirect(), forward
                .contextRelative()));
    }

    private void createFormBeanConfigIfNecessary(ModuleConfig config, Mapping mapping, final String formName) {
        FormBeanConfig formBeanConfig = config.findFormBeanConfig(formName);
        if (formBeanConfig == null) {
            formBeanConfig = new FormBeanConfig();
            formBeanConfig.setType(mapping.formBeanClass().getName());
            formBeanConfig.setName(formName);
            config.addFormBeanConfig(formBeanConfig);
        }
    }

    private String findInputMethod(Class<?> actionClass, Mapping mapping) {
        for (Method method : actionClass.getMethods()) {
            final Input input = method.getAnnotation(Input.class);
            if (input != null) {
                return mapping.path() + INPUT_PAGE_AND_METHOD + method.getName();
            }
        }
        return mapping.path() + INPUT_DEFAULT_PAGE_AND_METHOD;
    }

    public static void registerMapping(Class<?> type) {
        actionClasses.add(type);
    }
}