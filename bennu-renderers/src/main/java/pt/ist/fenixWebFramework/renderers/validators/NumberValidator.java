package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

import com.google.common.base.Strings;

public class NumberValidator extends HtmlValidator {

    private int base;

    public NumberValidator() {
        super();
        setBase(10);
    }

    public NumberValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
        setBase(10);
    }

    public NumberValidator(HtmlChainValidator htmlChainValidator, int base) {
        this(htmlChainValidator);

        setBase(base);
    }

    public int getBase() {
        return this.base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    @Override
    public String getErrorMessage() {
        return RenderUtils.getResourceString("renderers.validator.number");
    }

    @Override
    public void performValidation() {

        String numberText = getComponent().getValue();

        if (!Strings.isNullOrEmpty(numberText)) {
            try {
                Integer.parseInt(numberText.trim(), getBase());
                setValid(true);
            } catch (NumberFormatException e) {
                setValid(false);
            }
        }
    }

    @Override
    public boolean hasJavascriptSupport() {
        return true;
    }

    @Override
    protected String getSpecificValidatorScript() {
        return "function(element) { var text = $(element).attr('value');"
                + "return text.length == 0 || text.search(/^[0-9]+$/) == 0; }";
    }
}