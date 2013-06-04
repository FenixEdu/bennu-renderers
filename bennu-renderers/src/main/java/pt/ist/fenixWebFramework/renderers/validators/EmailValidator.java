package pt.ist.fenixWebFramework.renderers.validators;

import pt.utl.ist.fenix.tools.util.EMail;

public class EmailValidator extends RegexpValidator {

    public EmailValidator() {
        super(EMail.W3C_EMAIL_SINTAX_VALIDATOR);
        setMessage("renderers.validator.email");
    }

    public EmailValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator, EMail.W3C_EMAIL_SINTAX_VALIDATOR);

        setMessage("renderers.validator.email");
    }
}
