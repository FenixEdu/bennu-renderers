package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.utl.ist.fenix.tools.util.PhoneUtil;

public class PhoneValidator extends HtmlValidator {

    public PhoneValidator() {
        super();
    }

    public PhoneValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    @Override
    public void performValidation() {
        setValid(PhoneUtil.isValidNumber(getComponent().getValue()));
    }

    @Override
    public String getErrorMessage() {
        return RenderUtils.getResourceString("renderers.validator.phone.number");
    }

}
