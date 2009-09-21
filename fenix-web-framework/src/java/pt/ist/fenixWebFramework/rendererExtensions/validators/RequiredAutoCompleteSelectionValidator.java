package pt.ist.fenixWebFramework.rendererExtensions.validators;

import pt.ist.fenixWebFramework.rendererExtensions.AutoCompleteInputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;

public class RequiredAutoCompleteSelectionValidator extends HtmlValidator {

    public RequiredAutoCompleteSelectionValidator() {
	super();
	setMessage("renderers.validator.autoComplete.required");
    }

    public RequiredAutoCompleteSelectionValidator(HtmlChainValidator htmlChainValidator) {
	super(htmlChainValidator);

	setMessage("renderers.validator.autoComplete.required");
    }

    @Override
    public void performValidation() {
	HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getComponent();

	String value = component.getValue();
	if (value == null || value.length() == 0 || value.equals(AutoCompleteInputRenderer.TYPING_VALUE)) {
	    setValid(false);
	} else {
	    setValid(true);
	}
    }

    @Override
    public boolean hasJavascriptSupport() {
	return true;
    }

    @Override
    public HtmlScript bindJavascript(HtmlFormComponent formComponent) {
	String supplierId = RenderUtils.escapeId(formComponent.getId().replace("_AutoComplete", ""));
	String supplierHidden = RenderUtils.escapeId(formComponent.getId());
	
	HtmlScript script = new HtmlScript();
	script.setScript("$(\"#" + supplierId + "\").blur("
		+ "function() { var text = $(\"#" + supplierHidden + "\").attr('value');" 
		+ "if(text.length == 0) {"
			+ invalidOutput()
		+ "}});" 
		+ "$(\"#" + supplierId + "\").keydown(function() { $(this).parents(\"td\").next(\"td:last\").empty(); });"
		+ "$(\"#" + supplierId + "\").click(function() { $(this).parents(\"td\").next(\"td:last\").empty(); });");
	return script;
    }
   }
