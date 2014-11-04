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

import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;

import com.google.common.base.Strings;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class LocalizedStringValidator extends HtmlValidator {

    public LocalizedStringValidator() {
        super();
    }

    public LocalizedStringValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    @Override
    public void performValidation() {
        HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getComponent();

        if (Strings.isNullOrEmpty(component.getValue())) {
            setValid(true);
            return;
        }

        try {
            new JsonParser().parse(component.getValue()).getAsJsonObject();
            setValid(true);
        } catch (JsonSyntaxException | IllegalStateException e) {
            setValid(false);
            setMessage("renderers.validator.invalid.json");
        }

    }

}
