package pt.ist.fenixWebFramework.rendererExtensions.validators;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;

public class NumberRangeValidator extends HtmlValidator {

    private boolean isNumber;

    private Integer upperBound;

    private Integer lowerBound;

    public NumberRangeValidator() {
        super();

        upperBound = null;
        lowerBound = null;
    }

    public NumberRangeValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);

        upperBound = null;
        lowerBound = null;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public void performValidation() {

        String numberText = getComponent().getValue();

        if (!StringUtils.isEmpty(numberText)) {
            try {
                int number = Integer.parseInt(numberText.trim());

                boolean inRange = true;
                isNumber = true;

                if (lowerBound != null) {
                    inRange &= lowerBound <= number;
                }

                if (upperBound != null) {
                    inRange &= upperBound >= number;
                }

                this.setValid(inRange);
            } catch (NumberFormatException e) {
                isNumber = false;
                setValid(false);
            }
        }
    }

    @Override
    public String getErrorMessage() {
        if (!isNumber) {
            return RenderUtils.getResourceString("renderers.validator.number.integer");
        }

        if (lowerBound != null && upperBound != null) {
            return RenderUtils.getFormatedResourceString("renderers.validator.number.range.both", lowerBound, upperBound);
        }

        if (lowerBound != null) {
            return RenderUtils.getFormatedResourceString("renderers.validator.number.range.lower", lowerBound);
        }

        return RenderUtils.getFormatedResourceString("renderers.validator.number.range.upper", upperBound);
    }

    @Override
    public boolean hasJavascriptSupport() {
        return true;
    }

    @Override
    protected String getSpecificValidatorScript() {
        return "function(element) { var text = $(element).attr('value'); var lb = "
                + getLowerBound()
                + "; var ub = "
                + getUpperBound()
                + ";"
                + "return text.length == 0 || ((lb == null || (lb != null && parseInt(text) >= lb)) && (ub == null || (ub != null && parseInt(text) <= ub))); }";
    }

    @Override
    protected String getJavascriptErrorMessage() {
        if (lowerBound != null && upperBound != null) {
            return RenderUtils.getFormatedResourceString("renderers.validator.number.range.both", lowerBound, upperBound);
        }

        if (lowerBound != null) {
            return RenderUtils.getFormatedResourceString("renderers.validator.number.range.lower", lowerBound);
        }

        return RenderUtils.getFormatedResourceString("renderers.validator.number.range.upper", upperBound);
    }
}
