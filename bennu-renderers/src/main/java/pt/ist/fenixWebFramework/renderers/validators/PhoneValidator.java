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
import pt.utl.ist.fenix.tools.util.PhoneUtil;

public class PhoneValidator extends HtmlValidator {

    public PhoneValidator() {
        super();
    }

    public PhoneValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    @Override
    public void performValidation() {
        setValid(PhoneUtil.isValidNumber(getComponent().getValue()));
    }

    @Override
    public String getErrorMessage() {
        return RenderUtils.getResourceString("renderers.validator.phone.number");
    }

}
