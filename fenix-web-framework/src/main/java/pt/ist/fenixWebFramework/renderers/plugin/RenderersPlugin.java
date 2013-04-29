package pt.ist.fenixWebFramework.renderers.plugin;

import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.ControllerConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixWebFramework._development.LogLevel;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.model.SchemaFactory;
import pt.ist.fenixWebFramework.renderers.model.UserIdentityFactory;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;

public class RenderersPlugin implements PlugIn {
    private static Logger logger = LoggerFactory.getLogger(RenderersPlugin.class);

    // TODO: allow per module configuration, this includes factories
    private static boolean initialized = false;

    private String metaObjectFactory;

    private String userIdentityFactory;

    private String schemaFactory;

    // Keep a static variable to render kit. the plugin will be called before
    // any renderkit reference...
    // This will only allow unloading of the kit if the plugin is also unloaded
    // from the jvm.
    // If this happens, then the framework will init() the plugin before
    // refering to the kit.
    protected RenderKit renderKit;

    public String getMetaObjectFactory() {
        return metaObjectFactory;
    }

    public void setMetaObjectFactory(String metaObjectFactory) {
        this.metaObjectFactory = metaObjectFactory;
    }

    public String getSchemaFactory() {
        return schemaFactory;
    }

    public void setSchemaFactory(String schemaFactory) {
        this.schemaFactory = schemaFactory;
    }

    public String getUserIdentityFactory() {
        return userIdentityFactory;
    }

    public void setUserIdentityFactory(String userIdentityFactory) {
        this.userIdentityFactory = userIdentityFactory;
    }

    @Override
    public void destroy() {
        setUserIdentityFactory(null);
        setMetaObjectFactory(null);

        MetaObjectFactory.setCurrentFactory(MetaObjectFactory.DEFAULT_FACTORY);
        UserIdentityFactory.setCurrentFactory(UserIdentityFactory.DEFAULT_FACTORY);
        SchemaFactory.setCurrentFactory(SchemaFactory.DEFAULT_FACTORY);

        initialized = false;
    }

    @Override
    public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {
        if (!initialized) {
            initialized = true;
            ConfigurationReader.readAll(servlet.getServletContext());
            renderKit = RenderKit.getInstance();
            initFactories(servlet, config);
        }

        initProcessor(servlet, config);
    }

    private void initProcessor(ActionServlet servlet, ModuleConfig config) throws ServletException {
        String ourProcessorClassname = RenderersRequestProcessorImpl.implementationClass.getName();
        ControllerConfig controllerConfig = config.getControllerConfig();
        String configProcessorClassname = controllerConfig.getProcessorClass();

        // Check if specified classname exist
        Class configProcessorClass;
        try {
            configProcessorClass = RequestUtils.applicationClass(configProcessorClassname);

        } catch (ClassNotFoundException ex) {
            if (LogLevel.FATAL) {
                logger.error("Can't set RequestProcessor: bad class name '" + configProcessorClassname + "'.");
            }
            throw new ServletException(ex);
        }

        if (configProcessorClassname.equals(RequestProcessor.class.getName())
                || configProcessorClassname.endsWith(ourProcessorClassname)) {

            controllerConfig.setProcessorClass(ourProcessorClassname);
            return;
        }

        // Check if specified request processor is compatible with ours.
        Class ourProcessorClass = RenderersRequestProcessorImpl.implementationClass;
        if (!ourProcessorClass.isAssignableFrom(configProcessorClass)) {
            if (LogLevel.FATAL) {
                logger.error("Specified processor is incopatible with " + RequestProcessor.class.getName());
            }
            throw new ServletException("invalid processor was specified");
        }
    }

    private void initFactories(ActionServlet servlet, ModuleConfig config) throws ServletException {
        if (getMetaObjectFactory() != null) {
            try {
                MetaObjectFactory factory = (MetaObjectFactory) RequestUtils.applicationInstance(getMetaObjectFactory());
                MetaObjectFactory.setCurrentFactory(factory);
            } catch (Exception e) {
                throw new ServletException("Could not create meta object factory", e);
            }
        }

        if (getUserIdentityFactory() != null) {
            try {
                UserIdentityFactory factory = (UserIdentityFactory) RequestUtils.applicationInstance(getUserIdentityFactory());
                UserIdentityFactory.setCurrentFactory(factory);
            } catch (Exception e) {
                throw new ServletException("Could not create user identity factory", e);
            }
        }

        if (getSchemaFactory() != null) {
            try {
                SchemaFactory factory = (SchemaFactory) RequestUtils.applicationInstance(getSchemaFactory());
                SchemaFactory.setCurrentFactory(factory);
            } catch (Exception e) {
                throw new ServletException("Could not create user identity factory", e);
            }
        }
    }
}
