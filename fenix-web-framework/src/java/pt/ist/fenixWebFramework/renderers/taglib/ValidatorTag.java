package pt.ist.fenixWebFramework.renderers.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class ValidatorTag extends TagSupport implements PropertyContainerTag {

    private ValidatorContainerTag parent;

    private String name;

    @Override
    public void release() {
	super.release();

	this.name = null;
	this.parent = null;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int doStartTag() throws JspException {
	this.parent = (ValidatorContainerTag) findAncestorWithClass(this, ValidatorContainerTag.class);

	if (this.parent == null) {
	    throw new RuntimeException("validator tag can only be used inside an input tag or a schema slot description tag");
	}

	String name = getName();
	if (name != null) {
	    this.parent.addValidator(getName());
	}

	return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
	return EVAL_PAGE;
    }

    public void addProperty(String name, String value) {
	this.parent.addValidatorProperty(getName(), name, value);
    }

}
