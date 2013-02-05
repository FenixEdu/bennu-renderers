package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;

public class StringLengthValidator extends HtmlValidator {
    private Integer min;
    private Integer max;

    public StringLengthValidator() {
        super();
        setMessage("renderers.validator.invalid.length");
    }

    public StringLengthValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);

        setMessage("renderers.validator.invalid.length");
    }

    @Override
    public void performValidation() {
        HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getComponent();

        String string = component.getValue();

        setValid(string.length() >= getMin() && (getMax() == null || string.length() <= getMax()));
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    @Override
    public boolean hasJavascriptSupport() {
        return true;
    }

    @Override
    protected String getSpecificValidatorScript() {
        return "function(element) { var text = $(element).attr('value');" + "return text.length == 0 || (text.length >"
                + getMin() + (getMax() != null ? " && text.length < " + getMax() : "") + ";}";
    }
}
