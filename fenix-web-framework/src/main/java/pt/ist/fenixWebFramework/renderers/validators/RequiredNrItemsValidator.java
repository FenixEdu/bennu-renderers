package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBoxList;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class RequiredNrItemsValidator extends HtmlValidator {
    private Integer nrRequiredItems;

    public RequiredNrItemsValidator() {
        super();
    }

    public RequiredNrItemsValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    @Override
    public void performValidation() {
        HtmlCheckBoxList component = (HtmlCheckBoxList) getComponent();

        String values[] = component.getValues();
        defineMessage();

        setValid(values.length >= getNrRequiredItems().intValue());

    }

    private void defineMessage() {
        setKey(false);
        if (getNrRequiredItems() == null) {
            throw new RuntimeException("renderers.validator.nr.items.not.specified");
        } else {
            setMessage(RenderUtils.getFormatedResourceString(getBundle(), "renderers.validator.invalid.nrItems",
                    getNrRequiredItems()));
        }
    }

    public void setNrRequiredItems(Integer nrRequiredItems) {
        this.nrRequiredItems = nrRequiredItems;
    }

    public Integer getNrRequiredItems() {
        return nrRequiredItems;
    }

}
