package pt.ist.fenixWebFramework.renderers.taglib;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;

/**
 * @author cfgi
 */
public class CreateObjectTag extends EditObjectTag {

    private static final Logger logger = LoggerFactory.getLogger(CreateObjectTag.class);

    private List<DefaultValue> defaultValues;

    public CreateObjectTag() {
        super();

        this.defaultValues = new ArrayList<DefaultValue>();
    }

    @Override
    public void release() {
        super.release();

        this.defaultValues = new ArrayList<DefaultValue>();
    }

    @Override
    protected MetaObject getNewMetaObject(Object targetObject, Schema schema) {
        ContextTag parent = (ContextTag) findAncestorWithClass(this, ContextTag.class);

        if (parent == null || getSlot() == null) {
            return MetaObjectFactory.createObject((Class) targetObject, schema);
        }

        MetaObject metaObject = parent.getMetaObject();
        if (metaObject == null) {
            metaObject = MetaObjectFactory.createObject((Class) targetObject, schema);
            parent.setMetaObject(metaObject);
        } else {
            SchemaSlotDescription slotDescription = schema.getSlotDescription(getSlot());

            if (slotDescription != null) { // when hidden values are given
                MetaSlot slot = MetaObjectFactory.createSlot(metaObject, slotDescription);
                metaObject.addSlot(slot);
            }
        }

        return metaObject;
    }

    @Override
    protected Object getTargetObject() throws JspException {
        if (isPostBack()) {
            return getViewState().getMetaObject().getType();
        }

        try {
            return Class.forName(getType());
        } catch (ClassNotFoundException e) {
            throw new JspException("could not get class named " + getType(), e);
        }
    }

    @Override
    protected MetaObject createMetaObject(Object targetObject, Schema schema) {
        MetaObject metaObject = super.createMetaObject(targetObject, schema);

        if (getSlot() != null && metaObject instanceof MetaSlot) {
            MetaSlot slot = (MetaSlot) metaObject;

            for (DefaultValue defaultValue : this.defaultValues) {
                if (slot.getName().equals(defaultValue.getSlot())) {
                    Object value = getConvertedValue(slot, defaultValue.getConverter(), defaultValue.getValue());
                    slot.setObject(value);
                }
            }
        } else {
            for (DefaultValue defaultValue : this.defaultValues) {
                for (MetaSlot slot : metaObject.getSlots()) {
                    if (slot.getName().equals(defaultValue.getSlot())) {
                        Object value = getConvertedValue(slot, defaultValue.getConverter(), defaultValue.getValue());
                        slot.setObject(value);
                    }
                }
            }
        }

        return metaObject;
    }

    private Object getConvertedValue(MetaSlot slot, Class<Converter> converterClass, Object value) {
        Class type = slot.getStaticType();

        if (converterClass == null) {
            if (value == null) {
                return null;
            }

            if (type.isAssignableFrom(value.getClass())) {
                return value;
            } else {
                if (value instanceof String) {
                    return ConvertUtils.convert((String) value, type);
                } else {
                    throw new RuntimeException("no converter given and default value '" + value + "' does not match type '"
                            + type + "' of slot '" + slot.getName() + "'");
                }
            }
        } else {
            try {
                Converter converter = converterClass.newInstance();
                return converter.convert(type, value);
            } catch (Exception e) {
                throw new RuntimeException("could not convert default value '" + value + "' using converter '" + converterClass
                        + "'", e);
            }
        }
    }

    public void setDefaultValue(String slot, Object value, Class<Converter> converter) {
        this.defaultValues.add(new DefaultValue(slot, value, converter));
    }

    private static class DefaultValue {
        private String slot;
        private Object value;
        private Class<Converter> converter;

        public DefaultValue(String slot, Object value, Class<Converter> converter) {
            super();

            this.slot = slot;
            this.value = value;
            this.converter = converter;
        }

        public Class<Converter> getConverter() {
            return this.converter;
        }

        public String getSlot() {
            return this.slot;
        }

        public Object getValue() {
            return this.value;
        }

    }
}