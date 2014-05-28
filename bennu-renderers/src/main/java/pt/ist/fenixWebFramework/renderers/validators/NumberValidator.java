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