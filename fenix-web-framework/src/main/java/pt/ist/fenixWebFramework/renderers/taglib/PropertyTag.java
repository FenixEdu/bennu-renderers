package pt.ist.fenixWebFramework.renderers.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixWebFramework._development.LogLevel;

public class PropertyTag extends BodyTagSupport {
    private static final Logger logger = LoggerFactory.getLogger(PropertyTag.class);

    private String name = null;

    private String value = null;

    public PropertyTag() {
    }

    @Override
    public void release() {
        super.release();

        this.name = null;
        this.value = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int doStartTag() throws JspException {
        if (getValue() != null) {
            return SKIP_BODY;
        } else {
            return EVAL_BODY_BUFFERED;
        }
    }

    @Override
    public int doEndTag() throws JspException {
        PropertyContainerTag parent = (PropertyContainerTag) findAncestorWithClass(this, PropertyContainerTag.class);

        if (parent != null) {
            if (getValue() != null) {
                parent.addProperty(getName(), getValue());
            } else {
                parent.addProperty(getName(), getBodyContent().getString());
            }
        } else {
            if (LogLevel.WARN) {
                logger.warn("property tag was using inside an invalid container");
                logger.warn("could not set property: " + getName() + "=" + getValue());
            }
        }

        return super.doEndTag();
    }

}
