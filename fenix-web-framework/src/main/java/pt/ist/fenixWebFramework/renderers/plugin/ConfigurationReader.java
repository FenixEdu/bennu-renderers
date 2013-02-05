package pt.ist.fenixWebFramework.renderers.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import pt.ist.fenixWebFramework._development.LogLevel;
import pt.ist.fenixWebFramework.renderers.exceptions.NoRendererException;
import pt.ist.fenixWebFramework.renderers.exceptions.NoSuchSchemaException;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;
import pt.ist.fenixWebFramework.renderers.schemas.Signature;
import pt.ist.fenixWebFramework.renderers.schemas.SignatureParameter;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderMode;
import pt.ist.fenixWebFramework.renderers.utils.RendererPropertyUtils;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;
import pt.ist.fenixWebFramework.renderers.validators.RequiredValidator;
import pt.ist.fenixframework.artifact.FenixFrameworkArtifact;
import pt.ist.fenixframework.project.exception.FenixFrameworkProjectException;
import pt.utl.ist.fenix.tools.util.Pair;

public class ConfigurationReader {
    private static final Logger logger = Logger.getLogger(ConfigurationReader.class);

    public static void readSchemas(ServletContext context, URL schemaConfig) throws ServletException {
        Element root = readConfigRootElement(context, schemaConfig);

        if (root != null) {
            List schemaElements = root.getChildren("schema");

            for (Iterator schemaIterator = schemaElements.iterator(); schemaIterator.hasNext();) {
                Element schemaElement = (Element) schemaIterator.next();

                String schemaName = schemaElement.getAttributeValue("name");
                String typeName = schemaElement.getAttributeValue("type");
                String extendedSchemaName = schemaElement.getAttributeValue("extends");
                String refinedSchemaName = schemaElement.getAttributeValue("refines");
                String schemaBundle = schemaElement.getAttributeValue("bundle");
                String constructor = schemaElement.getAttributeValue("constructor");

                try {
                    RenderKit.getInstance().findSchema(schemaName);
                    if (LogLevel.ERROR) {
                        logger.error("schema '" + schemaName + "' was already defined");
                    }
                    continue;
                } catch (NoSuchSchemaException e) {
                    // ok
                }

                Class type;
                try {
                    type = getClassForType(typeName, true);
                } catch (ClassNotFoundException e) {
                    if (LogLevel.ERROR) {
                        logger.error("schema '" + schemaName + "' was defined for the undefined type '" + typeName + "'");
                    }
                    e.printStackTrace();
                    continue;
                }

                if (extendedSchemaName != null && refinedSchemaName != null) {
                    if (LogLevel.ERROR) {
                        logger.error("schema '" + schemaName + "' cannot extend '" + extendedSchemaName + "' and refine '"
                                + refinedSchemaName + "' at the same time");
                    }
                    continue;
                }

                Schema extendedSchema;
                try {
                    extendedSchema = RenderKit.getInstance().findSchema(extendedSchemaName);
                } catch (NoSuchSchemaException e) {
                    if (LogLevel.ERROR) {
                        logger.error("schema '" + schemaName + "' cannot extend '" + extendedSchemaName + "', schema not found");
                    }
                    e.printStackTrace();
                    continue;
                }

                Schema refinedSchema;
                try {
                    refinedSchema = RenderKit.getInstance().findSchema(refinedSchemaName);
                } catch (NoSuchSchemaException e) {
                    if (LogLevel.ERROR) {
                        logger.error("schema '" + schemaName + "' cannot refine '" + refinedSchemaName + "', schema not found");
                    }
                    e.printStackTrace();
                    continue;
                }

                if (extendedSchema != null && !extendedSchema.getType().isAssignableFrom(type)) {
                    if (LogLevel.WARN) {
                        logger.warn("schema '" + schemaName + "' is defined for type '" + typeName
                                + "' that is not a subclass of the type '" + extendedSchema.getType().getName()
                                + "' specified in the extended schema");
                    }
                }

                Schema schema;
                if (extendedSchema != null) {
                    schema = new Schema(schemaName, type, extendedSchema);
                } else if (refinedSchema != null) {
                    schema = refinedSchema;
                    schema.setType(type);
                } else {
                    schema = new Schema(schemaName, type);
                }

                List removeElements = schemaElement.getChildren("remove");
                if (extendedSchemaName == null && refinedSchema == null && removeElements.size() > 0) {
                    if (LogLevel.WARN) {
                        logger.warn("schema '" + schemaName
                                + "' specifies slots to be removed but it does not extend or refine schema");
                    }
                } else {
                    for (Iterator removeIterator = removeElements.iterator(); removeIterator.hasNext();) {
                        Element removeElement = (Element) removeIterator.next();

                        String name = removeElement.getAttributeValue("name");

                        SchemaSlotDescription slotDescription = schema.getSlotDescription(name);
                        if (slotDescription == null) {
                            if (LogLevel.WARN) {
                                logger.warn("schema '" + schemaName + "' specifies that slot '" + name
                                        + "' is to be removed but it is not defined in the extended schema");
                            }
                            continue;
                        }

                        schema.removeSlotDescription(slotDescription);
                    }
                }

                List slotElements = schemaElement.getChildren("slot");
                for (Iterator slotIterator = slotElements.iterator(); slotIterator.hasNext();) {
                    Element slotElement = (Element) slotIterator.next();

                    String slotName = slotElement.getAttributeValue("name");
                    String layout = slotElement.getAttributeValue("layout");
                    String key = slotElement.getAttributeValue("key");
                    String arg0 = slotElement.getAttributeValue("arg0");
                    String bundle = slotElement.getAttributeValue("bundle");
                    String slotSchema = slotElement.getAttributeValue("schema");
                    String validatorName = slotElement.getAttributeValue("validator");
                    String requiredValue = slotElement.getAttributeValue("required");
                    String defaultValue = slotElement.getAttributeValue("default");
                    String converterName = slotElement.getAttributeValue("converter");
                    String readOnlyValue = slotElement.getAttributeValue("read-only");
                    String hiddenValue = slotElement.getAttributeValue("hidden");
                    String helpLabelValue = slotElement.getAttributeValue("help");

                    String description = slotElement.getAttributeValue("description");
                    String descriptionFormat = slotElement.getAttributeValue("descriptionFormat");

                    Properties properties = getPropertiesFromElement(slotElement);

                    // Validators
                    List<Pair<Class<HtmlValidator>, Properties>> validators =
                            new ArrayList<Pair<Class<HtmlValidator>, Properties>>();
                    if (validatorName != null) {
                        try {
                            Class<HtmlValidator> validator = getClassForType(validatorName, true);
                            validators.add(new Pair<Class<HtmlValidator>, Properties>(validator, new Properties()));
                        } catch (ClassNotFoundException e) {
                            if (LogLevel.ERROR) {
                                logger.error("in schema '" + schemaName + "': validator '" + validatorName + "' was not found");
                            }
                            e.printStackTrace();
                            continue;
                        }

                    }

                    boolean required = requiredValue == null ? false : Boolean.parseBoolean(requiredValue);
                    if (required) {
                        Class validator = RequiredValidator.class;
                        validators.add(new Pair<Class<HtmlValidator>, Properties>(validator, new Properties()));
                    }

                    List validatorElements = slotElement.getChildren("validator");
                    for (Iterator validatorIterator = validatorElements.iterator(); validatorIterator.hasNext();) {
                        Element validatorElement = (Element) validatorIterator.next();
                        Properties validatorProperties;

                        validatorProperties = getPropertiesFromElement(validatorElement);
                        validatorName = validatorElement.getAttributeValue("class");

                        Class<HtmlValidator> validator = null;
                        if (validatorName != null) {
                            try {
                                validator = getClassForType(validatorName, true);
                            } catch (ClassNotFoundException e) {
                                if (LogLevel.ERROR) {
                                    logger.error("in schema '" + schemaName + "': validator '" + validatorName
                                            + "' was not found");
                                }
                                e.printStackTrace();
                                continue;
                            }
                        }

                        validators.add(new Pair<Class<HtmlValidator>, Properties>(validator, validatorProperties));
                    }

                    Class converter = null;
                    if (converterName != null) {
                        try {
                            converter = getClassForType(converterName, true);
                        } catch (ClassNotFoundException e) {
                            if (LogLevel.ERROR) {
                                logger.error("in schema '" + schemaName + "': converter '" + converterName + "' was not found");
                            }
                            e.printStackTrace();
                            continue;
                        }
                    }

                    boolean readOnly = readOnlyValue == null ? false : Boolean.parseBoolean(readOnlyValue);
                    boolean hidden = hiddenValue == null ? false : Boolean.parseBoolean(hiddenValue);

                    if (bundle == null) {
                        bundle = schemaBundle;
                    }

                    SchemaSlotDescription slotDescription = new SchemaSlotDescription(slotName);

                    slotDescription.setLayout(layout);
                    slotDescription.setKey(key);
                    slotDescription.setArg0(arg0);
                    slotDescription.setBundle(bundle);
                    slotDescription.setProperties(properties);
                    slotDescription.setSchema(slotSchema);
                    slotDescription.setValidators(validators);
                    slotDescription.setConverter(converter);
                    slotDescription.setDefaultValue(defaultValue);
                    slotDescription.setReadOnly(readOnly);
                    slotDescription.setHidden(hidden);
                    slotDescription.setHelpLabel(helpLabelValue);

                    slotDescription.setDescription(description);
                    slotDescription.setDescriptionFormat(descriptionFormat);

                    schema.addSlotDescription(slotDescription);
                }

                Signature construtorSignature = null;
                if (constructor != null) {
                    construtorSignature = parseSignature(schema, constructor);

                    if (construtorSignature != null) {
                        for (SignatureParameter parameter : construtorSignature.getParameters()) {
                            SchemaSlotDescription slotDescription = parameter.getSlotDescription();

                            if (parameter.getSlotDescription() != null) {
                                slotDescription.setSetterIgnored(true);
                            }
                        }
                    }
                }

                schema.setConstructor(construtorSignature);

                List setterElements = schemaElement.getChildren("setter");

                if (!setterElements.isEmpty()) {
                    schema.getSpecialSetters().clear();
                }

                for (Iterator setterIterator = setterElements.iterator(); setterIterator.hasNext();) {
                    Element setterElement = (Element) setterIterator.next();

                    String signature = setterElement.getAttributeValue("signature");

                    Signature setterSignature = parseSignature(schema, signature);
                    if (setterSignature != null) {
                        for (SignatureParameter parameter : setterSignature.getParameters()) {
                            parameter.getSlotDescription().setSetterIgnored(true);
                        }

                        schema.addSpecialSetter(setterSignature);
                    }
                }

                if (refinedSchema != null) {
                    schema = new Schema(schemaName, type, refinedSchema);
                    schema.setConstructor(refinedSchema.getConstructor());
                }

                if (LogLevel.DEBUG) {
                    logger.debug("adding new schema: " + schema.getName());
                }
                RenderKit.getInstance().registerSchema(schema);
            }
        }
    }

    private static Signature parseSignature(Schema schema, String signature) {

        String name;
        String parameters;

        int indexOfStartParent = signature.indexOf("(");
        if (indexOfStartParent != -1) {
            name = signature.substring(0, indexOfStartParent).trim();

            int indexOfCloseParen = signature.indexOf(")", indexOfStartParent);

            if (indexOfCloseParen == -1) {
                if (LogLevel.ERROR) {
                    logger.error("in schema " + schema.getName() + ": malformed signature '" + signature + "', missing ')'");
                }
                return null;
            }

            parameters = signature.substring(indexOfStartParent + 1, indexOfCloseParen);
        } else {
            name = null;
            parameters = signature.trim();
        }

        Signature programmaticSignature = new Signature(name);
        if (parameters.trim().length() == 0) {
            return programmaticSignature;
        }

        String[] allParameters = parameters.split(",");
        for (String allParameter : allParameters) {
            String singleParameter = allParameter.trim();

            String slotName;
            String typeName;

            int index = singleParameter.indexOf(":");
            if (index != -1) {
                slotName = singleParameter.substring(0, index).trim();
                typeName = singleParameter.substring(index + 1).trim();
            } else {
                slotName = singleParameter;
                typeName = null;
            }

            SchemaSlotDescription slotDescription = schema.getSlotDescription(slotName);
            if (LogLevel.ERROR) {
                if (slotDescription == null) {
                    logger.error("in schema " + schema.getName() + ": malformed signature '" + signature + "', slot '" + slotName
                            + "' is not defined");
                }
            }

            Class slotType;

            if (typeName != null) {
                try {
                    slotType = getClassForType(typeName, false);
                } catch (ClassNotFoundException e) {
                    if (LogLevel.ERROR) {
                        logger.error("in schema " + schema.getName() + ": malformed signature '" + signature
                                + "', could not find type '" + typeName + "'");
                    }
                    return null;
                }
            } else {
                slotType = RendererPropertyUtils.getPropertyType(schema.getType(), slotName);
            }

            programmaticSignature.addParameter(slotDescription, slotType);
        }

        return programmaticSignature;
    }

    private static Properties getPropertiesFromElement(Element element) {
        Properties properties = new Properties();

        List propertyElements = element.getChildren("property");
        for (Iterator propertyIterator = propertyElements.iterator(); propertyIterator.hasNext();) {
            Element propertyElement = (Element) propertyIterator.next();

            String name = propertyElement.getAttributeValue("name");
            String value = propertyElement.getAttributeValue("value");

            if (value == null && !propertyElement.getContent().isEmpty()) {
                value = propertyElement.getText();
            }

            if (value != null) {
                properties.setProperty(name, value);
            }
        }

        return properties;
    }

    public static void readRenderers(ServletContext context, URL renderConfig) throws ServletException {
        Element root = readConfigRootElement(context, renderConfig);

        if (root != null) {
            List renderers = root.getChildren();

            for (Iterator iter = renderers.iterator(); iter.hasNext();) {
                Element rendererElement = (Element) iter.next();

                String type = rendererElement.getAttributeValue("type");
                String layout = rendererElement.getAttributeValue("layout");
                String className = rendererElement.getAttributeValue("class");

                Properties rendererProperties = getPropertiesFromElement(rendererElement);

                try {
                    Class objectClass = getClassForType(type, true);
                    Class rendererClass = Class.forName(className);

                    String modeName = rendererElement.getAttributeValue("mode");
                    if (modeName == null) {
                        modeName = "output";
                    }

                    RenderMode mode = RenderMode.getMode(modeName);

                    if (LogLevel.WARN) {
                        if (hasRenderer(layout, objectClass, mode)) {
                            logger.warn(String.format("[%s] duplicated definition for type '%s' and layout '%s'", modeName,
                                    objectClass, layout));
                        }
                    }

                    if (LogLevel.DEBUG) {
                        logger.debug("[" + modeName + "] adding new renderer: " + objectClass + "/" + layout + "/"
                                + rendererClass + "/" + rendererProperties);
                    }
                    RenderKit.getInstance().registerRenderer(mode, objectClass, layout, rendererClass, rendererProperties);
                } catch (ClassNotFoundException e) {
                    if (LogLevel.ERROR) {
                        logger.error("could not register new renderer: " + e);
                    }
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean hasRenderer(String layout, Class objectClass, RenderMode mode) {
        try {
            return RenderKit.getInstance().getExactRendererDescription(mode, objectClass, layout) != null;
        } catch (NoRendererException e) {
            return false;
        }
    }

    private static Class getClassForType(String type, boolean prefixedLangPackage) throws ClassNotFoundException {
        String[] primitiveTypesNames = { "void", "boolean", "byte", "short", "int", "long", "char", "float", "double" };
        Class[] primitiveTypesClass =
                { Void.TYPE, Boolean.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Character.TYPE, Float.TYPE,
                        Double.TYPE };

        for (int i = 0; i < primitiveTypesNames.length; i++) {
            if (type.equals(primitiveTypesNames[i])) {
                return primitiveTypesClass[i];
            }
        }

        if (!prefixedLangPackage && type.indexOf(".") == -1) {
            return Class.forName("java.lang." + type);
        }
        return Class.forName(type);
    }

    private static Element readConfigRootElement(final ServletContext context, URL config) throws ServletException {
        try {
            SAXBuilder build = new SAXBuilder();
            build.setExpandEntities(true);
            return build.build(config).getRootElement();
        } catch (JDOMException | IOException e) {
            throw new ServletException(e);
        }
    }

    public static void readAll(ServletContext context) throws ServletException {
        RenderKit.reset();

        RendererPropertyUtils.initCache();

        try {
            Properties properties = new Properties();
            try (InputStream stream = ConfigurationReader.class.getResourceAsStream("/configuration.properties")) {
                if (stream == null) {
                    logger.error("configuration.properties not found found in classpath");
                    throw new RuntimeException();
                }
                properties.load(stream);
            }

            for (FenixFrameworkArtifact artifact : FenixFrameworkArtifact.fromName(properties.getProperty("app.name"))
                    .getArtifacts()) {
                URL renderConfig = context.getResource("/WEB-INF/" + artifact.getName() + "/renderers-config.xml");
                if (renderConfig != null) {
                    ConfigurationReader.readRenderers(context, renderConfig);
                }
                URL schemaConfig = context.getResource("/WEB-INF/" + artifact.getName() + "/schemas-config.xml");
                if (schemaConfig != null) {
                    ConfigurationReader.readSchemas(context, schemaConfig);
                }
            }
        } catch (IOException | FenixFrameworkProjectException e) {
            throw new ServletException(e);
        }

        RendererPropertyUtils.destroyCache();
        if (LogLevel.INFO) {
            logger.info("configuration read");
        }
    }
}
