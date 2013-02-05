/**
 * 
 */
package pt.ist.fenixWebFramework.struts.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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

import pt.ist.fenixWebFramework.Config;
import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixWebFramework.struts.annotations.ExceptionHandling;
import pt.ist.fenixWebFramework.struts.annotations.Exceptions;
import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Input;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixWebFramework.struts.tiles.FenixDefinitionsFactory;
import pt.ist.fenixWebFramework.struts.tiles.PartialTileDefinition;
import pt.ist.fenixframework.artifact.FenixFrameworkArtifact;
import pt.ist.fenixframework.project.exception.FenixFrameworkProjectException;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
public class StrutsAnnotationsPlugIn implements PlugIn {

    private static final String STRUTS_DEFAULT_RESOURCE_MESSAGE = "org.apache.struts.action.MESSAGE";

    private static final String PAGE_FILE_EXTENSION = ".jsp";

    private static final String INPUT_PAGE_AND_METHOD = ".do?page=0&method=";

    private static final String INPUT_DEFAULT_PAGE_AND_METHOD = ".do?page=0&method=prepare";

    private static final String EXCEPTION_KEY_DEFAULT_PREFIX = "resources.Action.exceptions.";

    private static final List<String> UPPER_BOUND_SUPERCLASSES = Arrays.asList("DispatchAction", "Action", "Object");

    private static boolean initialized = false;

    private static final Set<Class<?>> actionClasses = new HashSet<Class<?>>();

    @Override
    public void destroy() {
    }

    @Override
    public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {

        if (!initialized) {
            loadActionsFromFile(actionClasses);
            PartialTileDefinition.init();
            initialized = true;
        }

        final String modulePrefix = StringUtils.removeStart(config.getPrefix(), "/");

        for (Class<?> actionClass : actionClasses) {
            Mapping mapping = actionClass.getAnnotation(Mapping.class);
            if (mapping == null || !modulePrefix.equals(mapping.module())) {
                continue;
            }

            final ActionMapping actionMapping = createCustomActionMapping(mapping);
            if (actionMapping == null) {
                continue;
            }

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
                registerInput(actionMapping, mapping.input());
            }

            String defaultResourcesName = getDefaultResourcesName(config);
            Forwards forwards = actionClass.getAnnotation(Forwards.class);
            if (forwards != null) {
                for (final Forward forward : forwards.value()) {
                    registerForward(actionMapping, forward, forwards, mapping, defaultResourcesName);
                }
            }
            registerSuperclassForwards(actionMapping, actionClass.getSuperclass(), mapping, defaultResourcesName);

            Exceptions exceptions = actionClass.getAnnotation(Exceptions.class);
            if (exceptions != null) {
                registerExceptionHandling(actionMapping, exceptions);
            }

            initializeActionMappingProperties(actionMapping, mapping.customMappingProperties());

            config.addActionConfig(actionMapping);

        }

    }

    private static void registerExceptionHandling(final ActionMapping actionMapping, Exceptions exceptions) {
        for (final ExceptionHandling exception : exceptions.value()) {
            final ExceptionConfig exceptionConfig = new ExceptionConfig();

            Class<? extends Exception> exClass = exception.type();
            Class<? extends ExceptionHandler> handlerClass = exception.handler();

            String exceptionHandler = (handlerClass == null ? null : handlerClass.getName());
            if (exceptionHandler == null) {
                final Config appConfig = FenixWebFramework.getConfig();
                exceptionHandler =
                        (appConfig.getExceptionHandlerClassname() == null ? ExceptionHandler.class.getName() : appConfig
                                .getExceptionHandlerClassname());
            }

            String key = (exception.key() == null ? EXCEPTION_KEY_DEFAULT_PREFIX + exClass.getSimpleName() : exception.key());

            exceptionConfig.setKey(key);
            exceptionConfig.setHandler(exceptionHandler);
            exceptionConfig.setType(exClass.getName());

            if (!StringUtils.isEmpty(exception.path())) {
                exceptionConfig.setPath(exception.path());
            }

            if (!StringUtils.isEmpty(exception.scope())) {
                exceptionConfig.setScope(exception.scope());
            }

            actionMapping.addExceptionConfig(exceptionConfig);
        }
    }

    private static ActionMapping createCustomActionMapping(Mapping mapping) {
        try {
            return mapping.customMappingClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void initializeActionMappingProperties(ActionMapping actionMapping, String[] properties) {
        Method[] mappingMethods = actionMapping.getClass().getMethods();
        for (int i = 0; i < properties.length; i += 2) {
            String property = properties[i];
            String value = properties[i + 1];

            String setterName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Method setterMethod = null;
            for (Method mappingMethod : mappingMethods) {
                if (mappingMethod.getName().equals(setterName) && mappingMethod.getParameterTypes().length == 1) {
                    setterMethod = mappingMethod;
                    break;
                }
            }

            if (setterMethod == null) {
                continue;
            }

            try {
                setterMethod.invoke(actionMapping, value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private static void registerSuperclassForwards(final ActionMapping actionMapping, Class<?> superclass, Mapping mapping,
            String defaultResourcesName) {
        if (UPPER_BOUND_SUPERCLASSES.contains(superclass.getSimpleName())) {
            return;
        }
        Forwards forwards = superclass.getAnnotation(Forwards.class);
        if (forwards == null) {
            return;
        }
        for (final Forward forward : forwards.value()) {
            try {
                actionMapping.findForward(forward.name());
            } catch (NullPointerException ex) {
                // Forward wasn't registered in any subclass, so register it.
                registerForward(actionMapping, forward, forwards, mapping, defaultResourcesName);
            }
        }
        registerSuperclassForwards(actionMapping, superclass.getSuperclass(), mapping, defaultResourcesName);
    }

    private static void registerInput(final ActionMapping actionMapping, String input) {
        if (isSimplePageFile(input)) {
            PartialTileDefinition tileDefinition = new PartialTileDefinition(input);
            FenixDefinitionsFactory.registerDefinition(tileDefinition);
            actionMapping.setInput(tileDefinition.getName());
        } else {
            // The input is using an existing tile definition
            actionMapping.setInput(input);
        }
    }

    private static void registerForward(final ActionMapping actionMapping, final Forward forward, Forwards forwards,
            Mapping mapping, String defaultResourcesName) {
        if (forward.useTile() && isSimplePageFile(forward.path())) {
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
        return (str.endsWith(PAGE_FILE_EXTENSION)) && (StringUtils.countMatches(str, PAGE_FILE_EXTENSION) == 1);
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

    private void loadActionsFromFile(final Set<Class<?>> actionClasses) {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Properties properties = new Properties();
            try (InputStream stream = loader.getResourceAsStream("/configuration.properties")) {
                if (stream == null) {
                    throw new RuntimeException();
                }
                properties.load(stream);
            }

            for (FenixFrameworkArtifact artifact : FenixFrameworkArtifact.fromName(properties.getProperty("app.name"))
                    .getArtifacts()) {
                try (InputStream stream = loader.getResourceAsStream(artifact.getName() + "/.actionAnnotationLog")) {
                    if (stream != null) {
                        List<String> classnames = IOUtils.readLines(stream);
                        for (String classname : classnames) {
                            try {
                                Class<?> type = loader.loadClass(classname);
                                actionClasses.add(type);
                            } catch (final ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException | FenixFrameworkProjectException e) {
            e.printStackTrace();
        }
    }
}
