package pt.ist.fenixWebFramework.renderers.components;

import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.ConvertUtils;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public abstract class HtmlSimpleValueComponent extends HtmlFormComponent {

    private String value;

    public HtmlSimpleValueComponent() {
        super();
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String[] getValues() {
        if (getValue() == null) {
            return null;
        }

        return new String[] { getValue() };
    }

    @Override
    public Object getConvertedValue() {
        if (hasConverter()) {
            return getConverter().convert(Object.class, getValue());
        }

        return ConvertUtils.convert(getValue(), Object.class);
    }

    @Override
    public Object getConvertedValue(MetaSlot slot) {
        if (hasConverter()) {
            return getConverter().convert(slot.getStaticType(), getValue());
        }

        if (slot.hasConverter()) {
            try {
                return slot.getConverter().newInstance().convert(slot.getStaticType(), getValue());
            } catch (Exception e) {
                throw new RuntimeException("converter specified in meta slot generated an exception: " + e, e);
            }
        }

        if (getValue() == null) {
            return null;
        }
        return ConvertUtils.convert(getValue(), slot.getStaticType());
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setAttribute("value", HtmlText.escape(this.value));

        return tag;
    }
}