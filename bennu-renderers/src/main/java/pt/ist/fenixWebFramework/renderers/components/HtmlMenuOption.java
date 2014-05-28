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

public class HtmlMenuOption extends HtmlMenuEntry {

    private boolean selected;

    private String value;

    private String text;

    private HtmlComponent body;

    public HtmlMenuOption() {
        super(null, false);
    }

    public HtmlMenuOption(String text) {
        super(null, false);

        this.text = text;
    }

    public HtmlMenuOption(String text, String value) {
        this(text);

        this.value = value;
    }

    public HtmlMenuOption(HtmlComponent body) {
        super(null, false);

        this.body = body;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void setSelected(String value) {
        setSelected(getValue() != null && getValue().equals(value));
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public HtmlComponent getBody() {
        return this.body;
    }

    public void setBody(HtmlComponent body) {
        this.body = body;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("option");

        if (getText() != null) {
            tag.setText(getText());
        }

        if (isSelected()) {
            tag.setAttribute("selected", "selected");
        }

        tag.setAttribute("value", HtmlText.escape(getValue() == null ? getText() : getValue()));

        if (getBody() != null) {
            tag.addChild(getBody().getOwnTag(context));
        }

        return tag;
    }

}
