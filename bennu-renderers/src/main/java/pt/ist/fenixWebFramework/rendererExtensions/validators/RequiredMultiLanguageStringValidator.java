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

import java.util.Collection;

import pt.ist.fenixWebFramework.rendererExtensions.MultiLanguageStringInputRenderer.LanguageBean;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;

public class RequiredMultiLanguageStringValidator extends MultiLanguageStringValidator {

    public RequiredMultiLanguageStringValidator() {
        super();
        setMessage("renderers.validator.language.required");
    }

    public RequiredMultiLanguageStringValidator(HtmlChainValidator htmlChainValidator) {
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
        Collection<LanguageBean> beans = LanguageBean.importAllFromString(component.getValue());

        for (LanguageBean bean : beans) {
            if (bean.value != null && bean.value.length() > 0) {
                setValid(true);
                return;
            }
        }

        setValid(false);
    }
}
