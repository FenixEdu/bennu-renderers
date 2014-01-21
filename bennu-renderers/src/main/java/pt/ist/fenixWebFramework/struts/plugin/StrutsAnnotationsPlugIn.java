/**
 * 
 */
package pt.ist.fenixWebFramework.struts.plugin;

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
import org.apache.struts.config.MessageResourcesConfig;
import org.apache.struts.config.ModuleConfig;

import pt.ist.fenixWebFramework.renderers.plugin.RenderersRequestProcessor;
import pt.ist.fenixWebFramework.struts.annotations.ExceptionHandling;
import pt.ist.fenixWebFramework.struts.annotations.Exceptions;
import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Input;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixWebFramework.struts.tiles.FenixDefinitionsFactory;
import pt.ist.fenixWebFramework.struts.tiles.PartialTileDefinition;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
public class StrutsAnnotationsPlugIn implements PlugIn {

    private static final String STRUTS_DEFAULT_RESOURCE_MESSAGE = "org.apache.struts.action.MESSAGE";

    private static final String PAGE_FILE_EXTENSION = ".jsp";

    private static final String INPUT_PAGE_AND_METHOD = ".do?page=0&method=";

    private static final String INPUT_DEFAULT_PAGE_AND_METHOD = ".do?page=0&method=prepare";

    private static final List<String> UPPER_BOUND_SUPERCLASSES = Arrays.asList("DispatchAction", "Action", "Object");

    private static boolean initialized = false;

    private static final Set<Class<?>> actionClasses = new HashSet<Class<?>>();

    @Override
    public void destroy() {
    }

    @Override
    public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {

        if (!initialized) {
            PartialTileDefinition.init();
            initialized = true;
        }

        final String modulePrefix = CharMatcher.is('/').trimLeadingFrom(config.getPrefix());

        boolean isTilesModule =
                config.getControllerConfig().getProcessorClass().equals(RenderersRequestProcessor.class.getName());

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
                registerInput(actionMapping, mapping.input(), isTilesModule);
            }

            String defaultResourcesName = getDefaultResourcesName(config);
            Forwards forwards = actionClass.getAnnotation(Forwards.class);
            if (forwards != null) {
                for (final Forward forward : forwards.value()) {
                    registerForward(actionMapping, forward, forwards, mapping, defaultResourcesName, isTilesModule);
                }
            }
            registerSuperclassForwards(actionMapping, actionClass.getSuperclass(), mapping, defaultResourcesName, isTilesModule);

            Exceptions exceptions = actionClass.getAnnotation(Exceptions.class);
            if (exceptions != null) {
                registerExceptionHandling(actionMapping, exceptions);
            }

            config.addActionConfig(actionMapping);

        }
    }

    private static void registerExceptionHandling(final ActionMapping actionMapping, Exceptions exceptions) {
        for (final ExceptionHandling exception : exceptions.value()) {
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

    private static void registerSuperclassForwards(final ActionMapping actionMapping, Class<?> superclass, Mapping mapping,
            String defaultResourcesName, boolean isTilesModule) {
        if (UPPER_BOUND_SUPERCLASSES.contains(superclass.getSimpleName())) {
            return;
        }
        Forwards forwards = superclass.getAnnotation(Forwards.class);
        if (forwards != null) {
            for (final Forward forward : forwards.value()) {
                try {
                    actionMapping.findForward(forward.name());
                } catch (NullPointerException ex) {
                    // Forward wasn't registered in any subclass, so register it.
                    registerForward(actionMapping, forward, forwards, mapping, defaultResourcesName, isTilesModule);
                }
            }
        }
        registerSuperclassForwards(actionMapping, superclass.getSuperclass(), mapping, defaultResourcesName, isTilesModule);
    }

    private static void registerInput(final ActionMapping actionMapping, String input, boolean isTilesModule) {
        if (isTilesModule && isSimplePageFile(input)) {
            PartialTileDefinition tileDefinition = new PartialTileDefinition(input);
            FenixDefinitionsFactory.registerDefinition(tileDefinition);
            actionMapping.setInput(tileDefinition.getName());
        } else {
            // The input is using an existing tile definition
            actionMapping.setInput(input);
        }
    }

    private static void registerForward(final ActionMapping actionMapping, final Forward forward, Forwards forwards,
            Mapping mapping, String defaultResourcesName, boolean isTilesModule) {
        if (isTilesModule && forward.useTile() && isSimplePageFile(forward.path())) {
            PartialTileDefinition tileDefinition = new PartialTileDefinition(forward, forwards, mapping, defaultResourcesName);
            FenixDefinitionsFactory.registerDefinition(tileDefinition);
            actionMapping.addForwardConfig(new ActionForward(forward.name(), tileDefinition.getName(), forward.redirect(),
                    forward.contextRelative()));
        } else {
            // The forward is using an existing tile definition
            actionMapping.addForwardConfig(new ActionForward(forward.name(), forward.path(), forward.redirect(), forward
                    .contextRelative()));
        }
    }

    private static boolean isSimplePageFile(String str) {
        return str.endsWith(PAGE_FILE_EXTENSION)
                && !str.substring(0, str.length() - PAGE_FILE_EXTENSION.length()).contains(PAGE_FILE_EXTENSION);
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

    private static String getDefaultResourcesName(ModuleConfig config) {
        MessageResourcesConfig resourcesConfig = config.findMessageResourcesConfig(STRUTS_DEFAULT_RESOURCE_MESSAGE);
        return (resourcesConfig == null) ? null : resourcesConfig.getParameter();
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