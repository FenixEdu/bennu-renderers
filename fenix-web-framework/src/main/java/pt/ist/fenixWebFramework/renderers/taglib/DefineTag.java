package pt.ist.fenixWebFramework.renderers.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.PropertyUtils;

import pt.ist.fenixWebFramework.renderers.components.Constants;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;

public class DefineTag extends TagSupport {

    private String id;

    private String property;

    private String type;

    public DefineTag() {
        super();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int doEndTag() throws JspException {
        MetaObject object = (MetaObject) pageContext.findAttribute(Constants.TEMPLATE_OBJECT_NAME);

        if (object == null) {
            throw new JspException("This tag can only be used inside a render template.");
        }

        String property = getProperty();
        if (property == null) {
            pageContext.setAttribute(getId(), object.getObject());
        } else {
            try {
                pageContext.setAttribute(getId(), PropertyUtils.getProperty(object.getObject(), getProperty()));
            } catch (Exception e) {
                // print exception but keep going
                e.printStackTrace();
            }
        }

        return EVAL_PAGE;
    }

    @Override
    public void release() {
        super.release();

        this.property = null;
    }
}
