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

public class HtmlLabel extends HtmlComponent {

    private String text;
    private HtmlComponent body;

    private String forName;
    private String onBlur;
    private String onFocus;
    private HtmlFormComponent forComponent;

    public HtmlLabel() {
        super();
    }

    public HtmlLabel(String text) {
        super();

        setText(text);
    }

    public String getText() {
        return this.text;
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

    public String getFor() {
        return this.forName;
    }

    public void setFor(String forName) {
        this.forName = forName;
    }

    public void setFor(HtmlFormComponent component) {
        this.forComponent = component;
    }

    protected HtmlFormComponent getForComponent() {
        return forComponent;
    }

    protected void setForComponent(HtmlFormComponent forComponent) {
        this.forComponent = forComponent;
    }

    public String getOnBlur() {
        return this.onBlur;
    }

    public void setOnBlur(String onBlur) {
        this.onBlur = onBlur;
    }

    public String getOnFocus() {
        return this.onFocus;
    }

    public void setOnFocus(String onFocus) {
        this.onFocus = onFocus;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("label");

        tag.setAttribute("for", calculateFor());
        tag.setAttribute("onblur", getOnBlur());
        tag.setAttribute("onfocus", getOnFocus());

        if (getText() != null) {
            tag.setText(getText());
        }

        if (getBody() != null) {
            tag.addChild(getBody().getOwnTag(context));
        }

        return tag;
    }

    private String calculateFor() {
        if (getFor() != null) {
            return getFor();
        } else {
            if (getForComponent() != null) {
                return getForComponent().getId();
            }
        }

        return null;
    }

}
