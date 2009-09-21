package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class StringLengthValidator extends HtmlValidator {
    private Integer min;
    private Integer max;

    public StringLengthValidator() {
	super();
	setMessage("renderers.validator.invalid.length");
    }

    public StringLengthValidator(HtmlChainValidator htmlChainValidator) {
	super(htmlChainValidator);

	setMessage("renderers.validator.invalid.length");
    }

    @Override
    public void performValidation() {
	HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getComponent();

	String string = component.getValue();

	setValid(string.length() >= getMin() && (getMax() == null || string.length() <= getMax()));
    }

    public Integer getMax() {
	return max;
    }

    public void setMax(Integer max) {
	this.max = max;
    }

    public Integer getMin() {
	return min;
    }

    public void setMin(Integer min) {
	this.min = min;
    }

    @Override
    public boolean hasJavascriptSupport() {
	return true;
    }

    @Override
    protected String getSpecificValidatorScript(String componentId) {
	return "$(\"#" + componentId + "\").blur(" + "function() { var text = $(this).attr('value');"
		+ "if(text.length > 0 && (text.length <" + getMin() + (getMax() != null ? " || text.length > " + getMax() : "")
		+ ")) {" + invalidOutput() + "}});";
    }
}
