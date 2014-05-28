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

public class DoubleValidator extends HtmlValidator {

    public DoubleValidator() {
        super();
    }

    public DoubleValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    @Override
    public String getErrorMessage() {
        return RenderUtils.getResourceString("renderers.validator.number");
    }

    @Override
    public void performValidation() {
        String numberText = getComponent().getValue().trim();

        if (!Strings.isNullOrEmpty(numberText)) {
            try {
                Double.parseDouble(numberText);
                setValid(true);
            } catch (NumberFormatException e) {
                setValid(false);
            }
        }
    }
}