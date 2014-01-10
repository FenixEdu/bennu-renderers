package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.Validatable;

public class DegreeIdCardNameValidator extends HtmlValidator {

    public DegreeIdCardNameValidator() {
        super();
        setMessage("renderers.validator.degree.id.card.name");
    }

    public DegreeIdCardNameValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
        setMessage("renderers.validator.degree.id.card.name");
    }

    @Override
    public void performValidation() {
        final Validatable component = getComponent();
        if (component instanceof HtmlSimpleValueComponent) {
            final String value = component.getValue();
            setValid(value != null && value.length() > 0 && value.length() <= 42);
        } else {
            setValid(false);
        }
    }

}
