package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.Validatable;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class RequiredValidator extends HtmlValidator {

    public RequiredValidator() {
	super();
	setMessage("renderers.validator.required");
    }

    public RequiredValidator(HtmlChainValidator htmlChainValidator) {
	super(htmlChainValidator);

	setMessage("renderers.validator.required");
    }

    @Override
    public void performValidation() {
	Validatable component = getComponent();

	// TODO: cfgi, clear the semantic and uses of the Validatable interface
	// try to use only the interface instead of a check on the type

	if (component instanceof HtmlSimpleValueComponent) {
	    if (component.getValue() == null) {
		setValid(false);
	    } else {
		setValid(!component.getValue().equals(""));
	    }
	} else {
	    String[] values = component.getValues();

	    if (values == null) {
		setValid(false);
	    } else {
		setValid(values.length > 0);
	    }
	}
    }

    @Override
    public boolean hasJavascriptSupport() {
	return true;
    }

    @Override
    protected String getSpecificValidatorScript(String componentId) {
	return "$(\"#" + componentId + "\").blur(" + "function() { var text = $(this).attr('value');" + "if(text.length == 0) {"
		+ invalidOutput() + "}});";
    }
}
