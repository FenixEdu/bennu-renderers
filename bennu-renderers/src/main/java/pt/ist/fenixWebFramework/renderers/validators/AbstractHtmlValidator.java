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

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.Validatable;
import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public abstract class AbstractHtmlValidator extends HtmlComponent {

    private boolean valid;

    protected AbstractHtmlValidator() {
        setValid(true);
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public abstract void performValidation();

    public abstract String getErrorMessage();

    public abstract String getMessage();

    public abstract boolean isKey();

    public abstract Validatable getComponent();

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("span");
        if (!isValid()) {
            tag.setText(getErrorMessage());
        }

        return tag;
    }

}
