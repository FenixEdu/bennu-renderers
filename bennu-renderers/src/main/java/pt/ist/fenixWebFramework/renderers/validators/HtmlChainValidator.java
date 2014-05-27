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

import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.components.Validatable;

public class HtmlChainValidator extends AbstractHtmlValidator {

    private Validatable component;

    private List<HtmlValidator> validators;

    private HtmlValidator failedHtmlValidator;

    public HtmlChainValidator(Validatable component) {
        super();
        this.component = component;
        this.validators = new ArrayList<HtmlValidator>();
        component.setChainValidator(this);
    }

    public HtmlChainValidator(Validatable component, List<HtmlValidator> validators) {
        this(component);
        for (HtmlValidator htmlValidator : validators) {
            addValidator(htmlValidator);
        }
    }

    @Override
    public void performValidation() {
        for (HtmlValidator htmlValidator : validators) {
            htmlValidator.performValidation();
            if (!htmlValidator.isValid()) {
                setValid(false);
                failedHtmlValidator = htmlValidator;
                break;
            }
        }
    }

    @Override
    public Validatable getComponent() {
        return this.component;
    }

    public void addValidator(HtmlValidator htmlValidator) {
        this.validators.add(htmlValidator);
        htmlValidator.setHtmlChainValidator(this);
    }

    public void addValidator(HtmlChainValidator htmlChainValidator) {
        if (htmlChainValidator != null && htmlChainValidator != this) {
            for (HtmlValidator htmlValidator : htmlChainValidator.validators) {
                this.addValidator(htmlValidator);
            }
        }
    }

    public boolean isEmpty() {
        return validators.isEmpty();
    }

    @Override
    public boolean isKey() {
        return failedHtmlValidator != null ? failedHtmlValidator.isKey() : false;
    }

    @Override
    public String getErrorMessage() {
        return failedHtmlValidator != null ? failedHtmlValidator.getErrorMessage() : "";
    }

    @Override
    public String getMessage() {
        return failedHtmlValidator != null ? failedHtmlValidator.getMessage() : "";
    }

    public List<HtmlValidator> getSupportedJavascriptValidators() {
        List<HtmlValidator> validators = new ArrayList<HtmlValidator>();
        for (HtmlValidator validator : this.validators) {
            if (validator.hasJavascriptSupport()) {
                validators.add(validator);
            }
        }
        return validators;
    }
}
