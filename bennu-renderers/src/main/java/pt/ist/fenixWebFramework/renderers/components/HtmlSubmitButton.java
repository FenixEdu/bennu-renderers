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
package pt.ist.fenixWebFramework.renderers.components;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlSubmitButton extends HtmlInputButton {

    public HtmlSubmitButton() {
        super("submit");
    }

    public HtmlSubmitButton(String text) {
        this();

        setText(text);
    }

    public String getStyleClass() {
        return "btn-primary";
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("button");

        tag.setAttribute("type", getType());

        if (isDisabled()) {
            tag.setAttribute("disabled", true);
        }

        tag.setAttribute("class", "btn " + getStyleClass());
        tag.setAttribute("alt", getAlternateText());
        tag.setAttribute("tabindex", getTabIndex());
        tag.setAttribute("accesskey", getAccessKey());
        tag.setAttribute("size", getSize());
        tag.setAttribute("onchange", getOnChange());
        tag.setAttribute("onfocus", getOnFocus());
        tag.setAttribute("onblur", getOnBlur());

        tag.setText(getText());

        return tag;
    }
}
