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

public class HtmlText extends HtmlComponent {

    private String text;
    private boolean escaped;
    private boolean newLineAware;
    private Face face;

    public HtmlText(String text, boolean escaped) {
        this.text = text;
        this.escaped = escaped;
        this.newLineAware = escaped;
    }

    public HtmlText(String text, boolean escaped, boolean newLineAware) {
        this.text = text;
        this.escaped = escaped;
        this.newLineAware = newLineAware;
    }

    public HtmlText(String text) {
        this.text = text;
        this.escaped = true;
        this.newLineAware = true;
    }

    public HtmlText() {
        this.text = "";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isEscaped() {
        return this.escaped;
    }

    public void setEscaped(boolean escaped) {
        this.escaped = escaped;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    public Face getTextFace() {
        return this.face;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        if (tag.hasVisibleAttributes() || hasEffect()) {
            String tagName = getTagName();
            tag.setName(tagName == null ? "span" : tagName);
        } else {
            tag.setName(null);
        }

        if (this.text == null) {
            return tag;
        }

        String finalText = this.escaped ? escape(this.text) : this.text;
        finalText = this.newLineAware ? replaceNewlines(finalText) : finalText;

        tag.setText(finalText);

        return tag;
    }

    private boolean hasEffect() {
        return getTextFace() != null;
    }

    private String getTagName() {
        if (getTextFace() == null) {
            return null;
        }

        switch (getTextFace()) {
        case STANDARD:
            return "span";
        case EMPHASIS:
            return "em";
        case STRONG:
            return "strong";
        case MONOSPACED:
            return "tt";
        case PARAGRAPH:
            return "p";
        case H1:
            return "h1";
        case H2:
            return "h2";
        case H3:
            return "h3";
        case H4:
            return "h4";
        default:
            return null;
        }
    }

    private String replaceNewlines(String string) {
        StringBuilder result = new StringBuilder();

        String[] lines = string.split("\\r\\n|\\n|\\r", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (i > 0) {
                result.append("<br/>");
            }

            result.append(line);
        }

        return result.toString();
    }

    public static String escape(String text) {
        return escape(text, true);
    }

    public static String escape(String text, boolean preserveSpaces) {
        if (text == null) {
            return null;
        }

        String result = text.replaceAll("&", "&amp;") // must appear first
                .replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");

        if (preserveSpaces) {
            return result.replaceAll("  ", " &nbsp;");
        } else {
            return result;
        }
    }
}