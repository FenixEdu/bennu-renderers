package pt.ist.fenixWebFramework.renderers.validators;

public class ISBNValidator extends HtmlValidator {

    public ISBNValidator() {
        setMessage("renderers.validator.isbn.error");
    }

    public ISBNValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
        setMessage("renderers.validator.isbn.error");
    }

    @Override
    public void performValidation() {
        boolean valid = new org.apache.commons.validator.routines.ISBNValidator().isValid(getComponent().getValue());
        setValid(valid);

    }
}
