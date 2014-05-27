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
package pt.ist.fenixWebFramework.renderers.plugin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.Project;
import pt.utl.ist.fenix.tools.util.Pair;

public class ConfigurationReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationReader.class);

    public static void readSchemas(URL schemaConfig) throws ServletException {
        Element root = readConfigRootElement(schemaConfig);

        if (root != null) {
            List<?> schemaElements = root.getChildren("schema");

            for (Object element : schemaElements) {
                Element schemaElement = (Element) element;

                String schemaName = schemaElement.getAttributeValue("name");
                String typeName = schemaElement.getAttributeValue("type");
                String extendedSchemaName = schemaElement.getAttributeValue("extends");
                String refinedSchemaName = schemaElement.getAttributeValue("refines");
                String schemaBundle = schemaElement.getAttributeValue("bundle");
                String constructor = schemaElement.getAttributeValue("constructor");

                if (RenderKit.getInstance().hasSchema(schemaName)) {
                    logger.error("Schema '{}' was already defined. Ignoring re-declaration.", schemaName);
                    continue;
                }

                Class<?> type;
                try {
                    type = getClassForType(typeName, true);
                } catch (ClassNotFoundException e) {
                    logger.error("schema '" + schemaName + "' was defined for the undefined type '" + typeName
                            + "'. Ignoring it.", e);
                    continue;
                }

                if (extendedSchemaName != null && refinedSchemaName != null) {
                    logger.error("schema '" + schemaName + "' cannot extend '" + extendedSchemaName + "' and refine '"
                            + refinedSchemaName + "' at the same time. Ignoring it.");
                    continue;
                }

                Schema extendedSchema;
                try {
                    extendedSchema = RenderKit.getInstance().findSchema(extendedSchemaName);
                } catch (NoSuchSchemaException e) {
                    logger.error("schema '" + schemaName + "' cannot extend '" + extendedSchemaName
                            + "', schema not found. Ignoring it.", e);
                    continue;
                }

                Schema refinedSchema;
                try {
                    refinedSchema = RenderKit.getInstance().findSchema(refinedSchemaName);
                } catch (NoSuchSchemaException e) {
                    logger.error("schema '" + schemaName + "' cannot refine '" + refinedSchemaName
                            + "', schema not found. Ignoring it.", e);
                    continue;
                }

                if (extendedSchema != null && !extendedSchema.getType().isAssignableFrom(type)) {
                    logger.warn(
                            "schema '{}' is defined for type '{}' that is not a subclass of the type '{}' specified in the extended schema",
                            schemaName, typeName, extendedSchema.getType().getName());
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

                List<?> removeElements = schemaElement.getChildren("remove");
                if (extendedSchemaName == null && refinedSchema == null && removeElements.size() > 0) {
                    logger.warn("schema '{}' specifies slots to be removed but it does not extend or refine schema", schemaName);
                } else {
                    for (Object remove : removeElements) {
                        Element removeElement = (Element) remove;

                        String name = removeElement.getAttributeValue("name");

                        SchemaSlotDescription slotDescription = schema.getSlotDescription(name);
                        if (slotDescription == null) {
                            logger.warn(
                                    "schema '{}' specifies that slot '{}' is to be removed but it is not defined in the extended schema",
                                    schemaName, name);
                            continue;
                        }

                        schema.removeSlotDescription(slotDescription);
                    }
                }

                List<?> slotElements = schemaElement.getChildren("slot");
                for (Object slot : slotElements) {
                    Element slotElement = (Element) slot;

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
                            logger.error("in schema '" + schemaName + "': validator '" + validatorName
                                    + "' was not found. Ignoring slot declaration.", e);
                            continue;
                        }

                    }

                    boolean required = requiredValue == null ? false : Boolean.parseBoolean(requiredValue);
                    if (required) {
                        Class validator = RequiredValidator.class;
                        validators.add(new Pair<Class<HtmlValidator>, Properties>(validator, new Properties()));
                    }

                    List<?> validatorElements = slotElement.getChildren("validator");
                    for (Object valid : validatorElements) {
                        Element validatorElement = (Element) valid;
                        Properties validatorProperties;

                        validatorProperties = getPropertiesFromElement(validatorElement);
                        validatorName = validatorElement.getAttributeValue("class");

                        Class<HtmlValidator> validator = null;
                        if (validatorName != null) {
                            try {
                                validator = getClassForType(validatorName, true);
                            } catch (ClassNotFoundException e) {
                                logger.error("in schema '" + schemaName + "': validator '" + validatorName
                                        + "' was not found. Ignoring validator declaration.", e);
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
                            logger.error("in schema '" + schemaName + "': converter '" + converterName
                                    + "' was not found. Ignoring slot", e);
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

                logger.debug("Registered new schema '{}' for type '{}'", schema.getName(), typeName);
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
                logger.error("in schema {}: malformed signature '{}', missing ')'", schema.getName(), signature);
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
            if (slotDescription == null) {
                logger.error("in schema {}: malformed signature '{}', slot '{}' is not defined", schema.getName(), signature,
                        slotName);
            }

            Class slotType;

            if (typeName != null) {
                try {
                    slotType = getClassForType(typeName, false);
                } catch (ClassNotFoundException e) {
                    logger.error("in schema " + schema.getName() + ": malformed signature '" + signature
                            + "', could not find type '" + typeName + "'", e);
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

    public static void readRenderers(URL renderConfig) throws ServletException {
        Element root = readConfigRootElement(renderConfig);

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

                    if (hasRenderer(layout, objectClass, mode)) {
                        logger.warn("[{}] Duplicated renderer definition for type {} and layout '{}'", modeName, objectClass,
                                layout);
                    }

                    logger.debug("[{}] adding new renderer: {}/{}/{}/{}", modeName, objectClass, layout, rendererClass,
                            rendererProperties);
                    RenderKit.getInstance().registerRenderer(mode, objectClass, layout, rendererClass, rendererProperties);
                } catch (ClassNotFoundException e) {
                    logger.error("Could not register renderer for type '" + type + "', class not found", e);
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

    private static Element readConfigRootElement(URL config) throws ServletException {
        try {
            SAXBuilder build = new SAXBuilder();
            build.setExpandEntities(true);
//            final InputSource EMPTY_SOURCE = new InputSource(new ByteArrayInputStream(new byte[0]));
            build.setEntityResolver(new EntityResolver() {

                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    final String[] split = systemId.split("!");
                    if (split.length > 1) {
                        final String fileName = split[1];
                        return new InputSource(getClass().getResourceAsStream(fileName));
                    } else {
                        return null;
                    }
                }
            });
            return build.build(config).getRootElement();
        } catch (JDOMException | IOException e) {
            throw new ServletException(e);
        }
    }

    public static void readAll(ServletContext context) throws ServletException {
        RenderKit.reset();

        RendererPropertyUtils.initCache();

        try {
            for (Project project : FenixFramework.getProject().getProjects()) {
                URL renderConfig = context.getResource("/WEB-INF/" + project.getName() + "/renderers-config.xml");
                if (renderConfig != null) {
                    ConfigurationReader.readRenderers(renderConfig);
                }
                URL schemaConfig = context.getResource("/WEB-INF/" + project.getName() + "/schemas-config.xml");
                if (schemaConfig != null) {
                    ConfigurationReader.readSchemas(schemaConfig);
                }
            }
        } catch (IOException e) {
            throw new ServletException(e);
        }

        RendererPropertyUtils.destroyCache();
    }
}
