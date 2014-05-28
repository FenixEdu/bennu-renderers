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

import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.Validatable;

import com.google.common.base.Strings;

public class RequiredValidator extends HtmlValidator {

    public RequiredValidator() {
        super();
    }

    public RequiredValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    @Override
    public void performValidation() {
        Validatable component = getComponent();

        // TODO: cfgi, clear the semantic and uses of the Validatable interface
        // try to use only the interface instead of a check on the type

        if (component instanceof HtmlSimpleValueComponent) {
            if (component.getValue() == null) {
                setValid(false);
            } else {
                setValid(!component.getValue().equals(""));
            }
        } else {
            String[] values = component.getValues();

            if (values == null) {
                setValid(false);
            } else {
                setValid(values.length > 0);
            }
        }
    }

    @Override
    public boolean hasJavascriptSupport() {
        return true;
    }

    @Override
    protected String getSpecificValidatorScript() {
        return "function(element) { return $(element).attr('value').length > 0; }";
    }

    @Override
    public String getMessage() {
        if (Strings.isNullOrEmpty(super.getMessage())) {
            setMessage("renderers.validator.required");
        }
        return super.getMessage();
    }
}
