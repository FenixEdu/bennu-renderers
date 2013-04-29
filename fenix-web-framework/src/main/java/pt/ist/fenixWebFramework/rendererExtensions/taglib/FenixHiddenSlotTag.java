package pt.ist.fenixWebFramework.rendererExtensions.taglib;

import java.util.Collection;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;

import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyArrayConverter;
import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyConverter;
import pt.ist.fenixWebFramework.renderers.components.state.HiddenSlot;
import pt.ist.fenixWebFramework.renderers.converters.EnumConverter;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.taglib.HiddenSlotTag;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

public class FenixHiddenSlotTag extends HiddenSlotTag {

    private String oid;

    private boolean isCollection;

    public String getOid() {
        return this.oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    @Override
    public void release() {
        super.release();

        this.isCollection = false;
    }

    @Override
    protected Object findObject() throws JspException {
        if (getName() != null) {
            return super.findObject();
        } else {
            if (getOid() == null) {
                throw new JspException("must specify at 'name' or 'oid' and 'type'");
            }

            Object object = getPersistentObject();

            if (object == null) {
                throw new JspException("could not find object " + getOid());
            }

            return object;
        }
    }

    @Override
    protected void addHiddenSlot(String slot, Object value, String converterName) throws JspException {
        if (value == null && converterName == null) {
            return;
        }

        if (value instanceof Collection) {
            this.isCollection = true;

            Collection collection = (Collection) value;

            for (Object object : collection) {
                addHiddenSlot(slot, object, converterName);
            }
        } else if (value instanceof DomainObject) {
            String usedConverterName = getNextDomainObjectConverter();

            DomainObject domainObject = (DomainObject) value;
            String objectValue = MetaObjectFactory.createObject(domainObject, null).getKey().toString();

            addHiddenSlot(slot, objectValue, usedConverterName);
        } else if (value instanceof Enum) {
            String enumConverterName = EnumConverter.class.getName();
            addHiddenSlot(slot, ((Enum) value).name(), enumConverterName);
        } else {
            super.addHiddenSlot(slot, value, converterName);
        }
    }

    @Override
    protected boolean isMultiple() {
        return super.isMultiple() || this.isCollection;
    }

    private String getNextDomainObjectConverter() {
        if (getConverter() != null) {
            return getConverter();
        }

        HiddenSlot slot = getContainerParent().getHiddenSlot(getSlot());
        if (slot != null) {
            return DomainObjectKeyArrayConverter.class.getName();
        }

        if (isMultiple()) {
            return DomainObjectKeyArrayConverter.class.getName();
        }

        return DomainObjectKeyConverter.class.getName();
    }

    protected Object getPersistentObject() throws JspException {
        if (getOid() != null) {
            return FenixFramework.getDomainObject(getOid());
        }
        return null;
    }

    protected int getScopeByName(String scope) throws JspException {
        return TagUtils.getInstance().getScope(scope);
    }
}
