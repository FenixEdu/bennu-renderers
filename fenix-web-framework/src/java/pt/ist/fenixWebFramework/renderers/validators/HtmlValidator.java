package pt.ist.fenixWebFramework.renderers.validators;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.Validatable;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public abstract class HtmlValidator extends AbstractHtmlValidator {

    private HtmlChainValidator htmlChainValidator;

    private String message;

    private boolean isKey;

    private String bundle;

    protected HtmlValidator() {
	super();
	setKey(true);
	setBundle("RENDERER_RESOURCES");
    }

    public HtmlValidator(HtmlChainValidator htmlChainValidator) {
	this();
	htmlChainValidator.addValidator(this);
	this.htmlChainValidator = htmlChainValidator;
    }

    public Validatable getComponent() {
	return this.htmlChainValidator.getComponent();
    }

    public String getMessage() {
	return this.message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public String getErrorMessage() {
	if (isKey()) {
	    String errorMessage = getResourceMessage(getMessage());

	    if (errorMessage != null) {
		return errorMessage;
	    } else {
		return getMessage();
	    }
	} else {
	    return getMessage();
	}
    }

    protected String getResourceMessage(String message) {
	return RenderUtils.getResourceString(getBundle(), message);
    }

    protected void setHtmlChainValidator(HtmlChainValidator htmlChainValidator) {
	this.htmlChainValidator = htmlChainValidator;
    }

    public boolean isKey() {
	return this.isKey;
    }

    public void setKey(boolean isKey) {
	this.isKey = isKey;
    }

    public String getBundle() {
	return bundle;
    }

    public void setBundle(String bundle) {
	this.bundle = bundle;
    }

    public boolean hasJavascriptSupport() {
	return false;
    }

    public HtmlScript bindJavascript(HtmlFormComponent formComponent) {
	HtmlScript script = new HtmlScript();
	String escapeId = RenderUtils.escapeId(formComponent.getId());
	script.setScript(
		getSpecificValidatorScript(escapeId) 
		+ "$(\"#" + escapeId + "\").keydown(function() {var submitButton = $(this).parents(\"form\").children(\"input[type=submit]:first\"); submitButton.removeAttr('disabled'); submitButton.removeClass('disabled'); $(this).parents(\"td\").next(\"td:last\").empty(); });"
		+ "$(\"#" + escapeId + "\").click(function() { var submitButton = $(this).parents(\"form\").children(\"input[type=submit]:first\"); submitButton.removeAttr('disabled'); submitButton.removeClass('disabled'); $(this).parents(\"td\").next(\"td:last\").empty(); });");
	return script;
    }

    protected String invalidOutput() {
	return "$(this).parents(\"td\").next(\"td:last\").html('<span>" + getErrorMessage() + "</span>');" 
	+ " var submitButton = $(this).parents(\"form\").children(\"input[type=submit]:first\");" 
	+ " submitButton.attr('disabled','true'); submitButton.addClass('disabled');";
    }
    
    protected String getSpecificValidatorScript(String componentId) {
	return StringUtils.EMPTY;
    }
}
