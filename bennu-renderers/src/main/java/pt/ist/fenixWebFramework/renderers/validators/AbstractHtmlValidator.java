package pt.ist.fenixWebFramework.renderers.validators;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.Validatable;
import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public abstract class AbstractHtmlValidator extends HtmlComponent {

    private boolean valid;

    protected AbstractHtmlValidator() {
        setValid(true);
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public abstract void performValidation();

    public abstract String getErrorMessage();

    public abstract String getMessage();

    public abstract boolean isKey();

    public abstract Validatable getComponent();

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("span");
        if (!isValid()) {
            tag.setText(getErrorMessage());
        }

        return tag;
    }

}
