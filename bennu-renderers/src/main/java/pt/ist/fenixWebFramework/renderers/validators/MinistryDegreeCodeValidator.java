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

public class MinistryDegreeCodeValidator extends HtmlValidator {

    public MinistryDegreeCodeValidator() {
        super();
        setMessage("renderers.validator.ministry.code");
    }

    public MinistryDegreeCodeValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
        setMessage("renderers.validator.ministry.code");
    }

    @Override
    public void performValidation() {
        final Validatable component = getComponent();
        if (component instanceof HtmlSimpleValueComponent) {
            final String value = component.getValue();
            setValid(value == null || value.length() == 0 || value.length() == 4);
        } else {
            setValid(false);
        }
    }

}
