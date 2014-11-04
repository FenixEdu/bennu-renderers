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

import java.util.Locale;

import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class RequiredLocalizedStringValidator extends LocalizedStringValidator {

    public RequiredLocalizedStringValidator() {
        super();
        setMessage("renderers.validator.language.required");
    }

    public RequiredLocalizedStringValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);

        setMessage("renderers.validator.language.required");
    }

    @Override
    public void performValidation() {
        super.performValidation();

        if (!isValid()) {
            return;
        }

        HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getComponent();
        if (Strings.isNullOrEmpty(component.getValue())) {
            setValid(false);
            return;
        }
        JsonElement json = new JsonParser().parse(component.getValue());

        LocalizedString string = LocalizedString.fromJson(json);

        for (Locale locale : string.getLocales()) {
            if (!string.getContent(locale).isEmpty()) {
                setValid(true);
                return;
            }
        }

        setValid(false);
    }
}
