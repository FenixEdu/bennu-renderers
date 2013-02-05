package pt.ist.fenixWebFramework.renderers.validators;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.Validatable;

public class RequiredValidator extends HtmlValidator {

    public RequiredValidator() {
        super();
    }

    public RequiredValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
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
    protected String getSpecificValidatorScript() {
        return "function(element) { return $(element).attr('value').length > 0; }";
    }

    @Override
    public String getMessage() {
        if (StringUtils.isEmpty(super.getMessage())) {
            setMessage("renderers.validator.required");
        }
        return super.getMessage();
    }
}
