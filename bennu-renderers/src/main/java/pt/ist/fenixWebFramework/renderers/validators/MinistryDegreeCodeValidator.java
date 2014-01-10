package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.Validatable;

public class MinistryDegreeCodeValidator extends HtmlValidator {

    public MinistryDegreeCodeValidator() {
        super();
        setMessage("renderers.validator.ministry.code");
    }

    public MinistryDegreeCodeValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
        setMessage("renderers.validator.ministry.code");
    }

    @Override
    public void performValidation() {
        final Validatable component = getComponent();
        if (component instanceof HtmlSimpleValueComponent) {
            final String value = component.getValue();
            setValid(value == null || value.length() == 0 || value.length() == 4);
        } else {
            setValid(false);
        }
    }

}
