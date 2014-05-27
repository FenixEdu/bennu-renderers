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

public class ISSNValidator extends RegexpValidator {

    private boolean required;

    public ISSNValidator() {
        super("[0-9]{4}-[0-9]{3}[0-9X]");
        setMessage("renderers.validator.issn");
        setKey(true);
        setRequired(false);
    }

    public ISSNValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator, "[0-9]{4}-[0-9]{3}[0-9X]");
        setMessage("renderers.validator.issn");
        setKey(true);
        setRequired(false);
    }

    @Override
    public void performValidation() {
        if (hasValue()) {
            super.performValidation();
            if (isValid()) {
                setValid(isCheckSumValid(getComponent().getValue()));
            }
        } else {
            setValid(!isRequired());
        }
    }

    private boolean isCheckSumValid(String value) {
        value = value.replaceAll("-", "");
        int res = 0;
        for (int i = 0; i < 7; i++) {
            res += (8 - i) * (value.charAt(i) - '0');
        }

        int remainder = res % 11;
        int checkDigit = 11 - remainder;

        char result;
        if (remainder == 0) {
            result = '0';
        } else {
            if (checkDigit == 10) {
                result = 'X';
            } else {
                result = (char) (checkDigit + '0');
            }
        }
        return value.charAt(7) == result;
    }

    private boolean hasValue() {
        return (getComponent().getValue() != null && getComponent().getValue().length() > 0);
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

}
