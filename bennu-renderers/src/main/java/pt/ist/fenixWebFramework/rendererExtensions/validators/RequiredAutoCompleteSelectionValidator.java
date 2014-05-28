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

import pt.ist.fenixWebFramework.rendererExtensions.AutoCompleteInputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;

import com.google.common.base.Strings;

public class RequiredAutoCompleteSelectionValidator extends HtmlValidator {

    private boolean allowsCustom = false;

    public RequiredAutoCompleteSelectionValidator() {
        super();
    }

    public RequiredAutoCompleteSelectionValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    @Override
    public void performValidation() {
        HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getComponent();

        String value = component.getValue();
        if (value == null || value.length() == 0
                || (!this.isAllowsCustom() && value.equals(AutoCompleteInputRenderer.TYPING_VALUE))) {
            setValid(false);
        } else {
            setValid(true);
        }
    }

    @Override
    public boolean hasJavascriptSupport() {
        return true;
    }

    @Override
    protected String getSpecificValidatorScript() {

        StringBuilder validatorBuilder =
                new StringBuilder(
                        "function(element) { return $(element).prevAll(\"input[name$=_AutoComplete]\").attr('value').length > 0 ");
        if (this.isAllowsCustom()) {
            validatorBuilder.append("|| ($(element).prevAll(\"input[name$=_AutoComplete]\").attr('value') == 'custom')");
        } else {
            validatorBuilder.append("&& ($(element).prevAll(\"input[name$=_AutoComplete]\").attr('value') != 'custom')");
        }
        validatorBuilder.append("; }");

        return validatorBuilder.toString();
    }

    @Override
    protected String bindJavascriptEventsTo(HtmlFormComponent formComponent) {
        return "#" + RenderUtils.escapeId(formComponent.getId().replace("_AutoComplete", ""));
    }

    @Override
    protected String getValidatableId(HtmlFormComponent formComponent) {
        return RenderUtils.escapeId(formComponent.getId().replace("_AutoComplete", ""));
    }

    @Override
    public String getMessage() {
        if (Strings.isNullOrEmpty(super.getMessage())) {
            setMessage("renderers.validator.autoComplete.required");
        }
        return super.getMessage();
    }

    public boolean isAllowsCustom() {
        return allowsCustom;
    }

    public void setAllowsCustom(boolean allowsCustom) {
        this.allowsCustom = allowsCustom;
    }

}
