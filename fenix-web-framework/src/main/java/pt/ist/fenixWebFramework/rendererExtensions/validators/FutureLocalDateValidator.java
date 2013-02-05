package pt.ist.fenixWebFramework.rendererExtensions.validators;

import org.joda.time.LocalDate;

import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;

public class FutureLocalDateValidator extends LocalDateValidator {

    public FutureLocalDateValidator() {
        super();
    }

    public FutureLocalDateValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    @Override
    public void performValidation() {
        HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getComponent();

        String value = component.getValue();

        if (value == null || value.length() == 0) {
            setMessage("renderers.validator.dateTime.required");
            setValid(!isRequired());
        } else {
            super.performValidation();

            if (isValid()) {
                LocalDate localDate = getCalculatedDate();

                if (localDate.isBefore(new LocalDate())) {
                    setMessage("renderers.validator.dateTime.beforeNow");
                    setValid(false);
                }
            }
        }
    }

}
