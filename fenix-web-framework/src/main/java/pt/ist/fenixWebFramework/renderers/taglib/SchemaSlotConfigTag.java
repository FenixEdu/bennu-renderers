package pt.ist.fenixWebFramework.renderers.taglib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;
import pt.ist.fenixWebFramework.renderers.validators.RequiredValidator;
import pt.utl.ist.fenix.tools.util.Pair;

public class SchemaSlotConfigTag extends BodyTagSupport implements PropertyContainerTag, ValidatorContainerTag {

    private static final Logger logger = Logger.getLogger(SchemaSlotConfigTag.class);

    private String name;
    private String bundle;
    private String key;
    private String arg0;

    private String validator;
    private boolean readOnly;
    private boolean required = false;
    private String layout;
    private String help;
    private SchemaConfigTag schemaTag;

    private Map<String, Properties> validators;

    private Properties properties;

    public SchemaSlotConfigTag() {

    }

    @Override
    public int doStartTag() throws JspException {
        this.validators = new HashMap<String, Properties>();
        this.properties = new Properties();
        schemaTag = (SchemaConfigTag) findAncestorWithClass(this, SchemaConfigTag.class);
        if (schemaTag == null) {
            throw new RuntimeException("Slot tag can only be used inside a schema tag");
        }
        return super.doStartTag();
    }

    @Override
    public int doEndTag() throws JspException {
        SchemaSlotDescription slot = new SchemaSlotDescription(getName());
        slot.setBundle(getBundle());
        slot.setKey(getKey());
        slot.setArg0(getArg0());
        slot.setArg0(getArg0());
        slot.setReadOnly(isReadOnly());
        slot.setHelpLabel(getHelp());
        slot.setLayout(getLayout());

        if (this.validator != null) {
            addValidator(this.validator);
        }

        if (isRequired()) {
            validators.put(RequiredValidator.class.getName(), new Properties());
        }

        slot.setValidators(getValidatorsClass());

        SchemaConfigTag parent = (SchemaConfigTag) findAncestorWithClass(this, SchemaConfigTag.class);
        parent.addSlots(slot);
        slot.setProperties(this.properties);
        release();
        return super.doEndTag();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBundle() {
        return bundle == null ? schemaTag.getBundle() : bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValidator() {
        return validator;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    @Override
    public void release() {
        this.validators = null;
        this.properties = null;
        this.required = false;
        super.release();
    }

    @Override
    public void addProperty(String name, String value) {
        this.properties.put(name, value);
    }

    @Override
    public void addValidator(String validatorClassName) {
        validators.put(validatorClassName, new Properties());
    }

    @Override
    public void addValidatorProperty(String validatorClassName, String propertyName, String propertyValue) {
        this.validators.get(validatorClassName).setProperty(propertyName, propertyValue);
    }

    protected List<Pair<Class<HtmlValidator>, Properties>> getValidatorsClass() {
        List<Pair<Class<HtmlValidator>, Properties>> res = new ArrayList<Pair<Class<HtmlValidator>, Properties>>();

        for (Entry<String, Properties> entry : this.validators.entrySet()) {
            try {
                Class<HtmlValidator> validatorClass = (Class<HtmlValidator>) Class.forName(entry.getKey());
                res.add(new Pair<Class<HtmlValidator>, Properties>(validatorClass, entry.getValue()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("specified validator does not exist: " + entry.getKey(), e);
            }
        }
        return res;
    }

    public String getArg0() {
        return arg0;
    }

    public void setArg0(String arg0) {
        this.arg0 = arg0;
    }

}
