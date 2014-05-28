/**
 * Copyright © 2008 Instituto Superior Técnico
 *
 * This file is part of Bennu Renderers Framework.
 *
 * Bennu Renderers Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Renderers Framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Renderers Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixWebFramework.rendererExtensions.validators;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;

import com.google.common.base.Strings;

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

        if (!Strings.isNullOrEmpty(numberText)) {
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
