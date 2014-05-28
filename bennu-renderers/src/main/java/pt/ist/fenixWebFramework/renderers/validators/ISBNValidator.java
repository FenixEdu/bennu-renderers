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

public class ISBNValidator extends HtmlValidator {

    public ISBNValidator() {
        setMessage("renderers.validator.isbn.error");
    }

    public ISBNValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
        setMessage("renderers.validator.isbn.error");
    }

    @Override
    public void performValidation() {
        boolean valid = new org.apache.commons.validator.routines.ISBNValidator().isValid(getComponent().getValue());
        setValid(valid);

    }
}
