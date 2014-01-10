package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

import com.google.common.base.Strings;

public class DoubleValidator extends HtmlValidator {

    public DoubleValidator() {
        super();
    }

    public DoubleValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    @Override
    public String getErrorMessage() {
        return RenderUtils.getResourceString("renderers.validator.number");
    }

    @Override
    public void performValidation() {
        String numberText = getComponent().getValue().trim();

        if (!Strings.isNullOrEmpty(numberText)) {
            try {
                Double.parseDouble(numberText);
                setValid(true);
            } catch (NumberFormatException e) {
                setValid(false);
            }
        }
    }
}