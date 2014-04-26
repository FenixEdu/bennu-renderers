package pt.ist.fenixWebFramework.renderers.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixWebFramework.rendererExtensions.validators.RequiredAutoCompleteSelectionValidator;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.utils.RendererPropertyUtils;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;
import pt.ist.fenixWebFramework.renderers.validators.RequiredValidator;
import pt.utl.ist.fenix.tools.util.Pair;

/**
 * A MetaSlot is an abstraction of a slot's value of a concrete domain object.
 * The meta slots provides an interface to present and manipulate that slot by
 * the user without changing the domain until it's really neaded. The meta slot
 * also allows to propagete user inserted values through out the interface.
 * 
 * @author cfgi
 */
public class MetaSlot extends MetaObject {

    private final MetaObject metaObject;

    private final String name;

    private String bundle;
    private String labelKey;
    private String labelArg0;

    private String layout;

    private List<Pair<Class<HtmlValidator>, Properties>> validators = new ArrayList<Pair<Class<HtmlValidator>, Properties>>();

    private Class<Converter> converter;

    private String defaultValue;

    private boolean readOnly;
    private boolean setterIgnored;

    private String helpLabel;

    private boolean isCached;
    private MetaObject valueMetaObject;

    private String description;

    private String descriptionFormat;

    public MetaSlot(MetaObject metaObject, String name) {
        super();

        this.metaObject = metaObject;
        this.name = name;

        this.valueMetaObject = null;
    }

    /**
     * Provides access to the meta object that holds this meta slot.
     * 
     * @return the holder meta object
     */
    public MetaObject getMetaObject() {
        return this.metaObject;
    }

    /**
     * @return the name of the slot
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the key that allows to identify this slot
     */
    @Override
    public MetaSlotKey getKey() {
        return new MetaSlotKey(getMetaObject(), getName());
    }

    public boolean hasValidator() {
        return getValidators() != null && !getValidators().isEmpty();
    }

    /**
     * This method allows you to obtain a localized label that can be presented
     * in the interface. This label is obtained by using the provided key and
     * bundle for this slot or the default naming conventions based on the type
     * of the slot's object and this slot's name.
     * 
     * @return a string that can be used in the interface as a label for this
     *         slot
     * 
     * @see #getLabelKey()
     * @see #getBundle()
     * @see RenderUtils#getSlotLabel(Class, String, String, String)
     */
    public String getLabel() {
        final Object object = getMetaObject().getObject();
        if (object != null) {
            if (getLabelArg0() == null) {
                return RenderUtils.getSlotLabel(object.getClass(), getName(), getBundle(), getLabelKey());
            } else {
                return RenderUtils.getSlotLabel(object.getClass(), getName(), getBundle(), getLabelKey(), getLabelArg0());
            }
        }

        Class type;

        if (getMetaObject().getSchema() != null) {
            type = getMetaObject().getSchema().getType();
        } else {
            type = getMetaObject().getType();
        }

        if (getLabelArg0() == null) {
            return RenderUtils.getSlotLabel(type, getName(), getBundle(), getLabelKey());
        } else {
            return RenderUtils.getSlotLabel(type, getName(), getBundle(), getLabelKey(), getLabelArg0());
        }
    }

    public void setLabelKey(String key) {
        this.labelKey = key;
    }

    /**
     * @return the key representing the label in a resource bundle
     */
    public String getLabelKey() {
        return this.labelKey;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getLabelArg0() {
        return labelArg0;
    }

    public void setLabelArg0(String labelArg0) {
        this.labelArg0 = labelArg0;
    }

    /**
     * @return the bundle that should be used when obtaining the slot's label
     */
    public String getBundle() {
        return this.bundle;
    }

    public void setDescriptionFormat(String descriptionFormat) {
        this.descriptionFormat = descriptionFormat;

    }

    public String getDescriptionFormat() {
        return descriptionFormat;
    }

    public boolean hasConverter() {
        return this.converter != null;
    }

    public Class<Converter> getConverter() {
        return this.converter;
    }

    public void setConverter(Class<Converter> converter) {
        this.converter = converter;
    }

    /**
     * The meta slot does not change the real slot until the commit is issued.
     * This method allows you to check if a value was already setted for this
     * meta slot.
     * 
     * @return <code>true</code> if a value was set in this slot
     */
    public boolean isCached() {
        return isCached;
    }

    protected void setCached(boolean isCached) {
        this.isCached = isCached;
    }

    /**
     * @return this slot's value
     */
    @Override
    public Object getObject() {
        if (isCached()) {
            return getValueMetaObject().getObject();
        } else {
            try {
                if (getName().equals("this")) {
                    return getMetaObject().getObject();
                }
                return PropertyUtils.getProperty(getMetaObject().getObject(), getName());
            } catch (Exception e) {
                throw new RuntimeException("could not read property '" + getName() + "' from object "
                        + getMetaObject().getObject(), e);
            }
        }
    }

    /**
     * @return this slot's type
     */
    @Override
    public Class getType() {
        if (getObject() != null) {
            return getObject().getClass();
        } else {
            return getStaticType();
        }
    }

    /**
     * @return this's slots static type, that is, does not use the current value
     *         to determine the type
     */
    public Class getStaticType() {
        return RendererPropertyUtils.getPropertyType(getMetaObject().getType(), getName());
    }

    /**
     * Change this slot's value.
     */
    public void setObject(Object object) {
        setValueMetaObject(object);
        setCached(true);
    }

    protected void setValueMetaObject(MetaObject metaObject) {
        this.valueMetaObject = metaObject;

        if (this.valueMetaObject != null) {
            this.valueMetaObject.setUser(getUser());
        }
    }

    private void setValueMetaObject(Object object) {
        setValueMetaObject(MetaObjectFactory.createObject(object, getSchema()));
    }

    protected MetaObject getValueMetaObject() {
        if (this.valueMetaObject == null) {
            setValueMetaObject(getObject());
        }

        return this.valueMetaObject;
    }

    @Override
    public void setUser(User user) {
        // When we are using a slot directly instead of accessing it though the
        // base meta object
        if (getMetaObject() != null) {
            User metaObjetUser = getMetaObject().getUser();

            if (metaObjetUser == null || !metaObjetUser.equals(user)) { // avoid
                // recursion
                getMetaObject().setUser(user);
            }
        }

        if (this.valueMetaObject != null) {
            this.valueMetaObject.setUser(user);
        }
    }

    @Override
    public User getUser() {
        if (getMetaObject() != null) {
            return getMetaObject().getUser();
        } else {
            return null;
        }
    }

    @Override
    public List<MetaSlot> getSlots() {
        MetaObject valueMetaObject = getValueMetaObject();

        return valueMetaObject.getSlots();
    }

    @Override
    public void addSlot(MetaSlot slot) {
        // ignore
    }

    @Override
    public boolean removeSlot(MetaSlot slot) {
        // ignored
        return false;
    }

    @Override
    public List<MetaSlot> getHiddenSlots() {
        MetaObject valueMetaObject = getValueMetaObject();

        return valueMetaObject.getHiddenSlots();
    }

    @Override
    public void addHiddenSlot(MetaSlot slot) {
        // ignore
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getLayout() {
        return this.layout;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isSetterIgnored() {
        return this.setterIgnored;
    }

    public void setSetterIgnored(boolean setterIgnored) {
        this.setterIgnored = setterIgnored;
    }

    @Override
    public void commit() {
        // delegate to parent meta object
        getMetaObject().commit();
    }

    public List<Pair<Class<HtmlValidator>, Properties>> getValidators() {
        return validators;
    }

    public List<HtmlValidator> getValidatorsList() {
        List<HtmlValidator> validators = new ArrayList<HtmlValidator>();
        for (Pair<Class<HtmlValidator>, Properties> validatorPair : this.validators) {
            Constructor<HtmlValidator> constructor;
            try {
                constructor = validatorPair.getKey().getConstructor(new Class[] {});

                HtmlValidator validator = constructor.newInstance();
                RenderUtils.setProperties(validator, validatorPair.getValue());

                validators.add(validator);
            } catch (Exception e) {
                throw new RuntimeException("could not create validator '" + validatorPair.getKey().getName() + "' for slot '"
                        + getName() + "': ", e);
            }
        }

        return validators;
    }

    public void setValidators(List<Pair<Class<HtmlValidator>, Properties>> validators) {
        if (validators != null) {
            this.validators = validators;
        }
    }

    public boolean isRequired() {
        for (Pair<Class<pt.ist.fenixWebFramework.renderers.validators.HtmlValidator>, Properties> validator : getValidators()) {
            if (RequiredAutoCompleteSelectionValidator.class.isAssignableFrom(validator.getKey())
                    || RequiredValidator.class.isAssignableFrom(validator.getKey())) {
                return true;
            }
        }
        return false;
    }

    public String getHelpLabel() {
        return helpLabel;
    }

    public void setHelpLabel(String helpLabel) {
        this.helpLabel = helpLabel;
    }

    public boolean hasHelp() {
        return getHelpLabel() != null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        String label = null;
        String key = getDescription();

        if (key != null) {
            label = RenderUtils.getResourceString(bundle, key);
        }

        if (label != null) {
            return label;
        }

        String labelFormat = getDescriptionFormat();

        final Object object = getMetaObject().getObject();
        if (object != null && labelFormat != null) {
            return RenderUtils.getFormattedProperties(labelFormat, object);
        }

        Class type;

        if (getMetaObject().getSchema() != null) {
            type = getMetaObject().getSchema().getType();
        } else {
            type = getMetaObject().getType();
        }

        return RenderUtils.getSlotLabel(type, getName(), getBundle(), getDescription());
    }

}