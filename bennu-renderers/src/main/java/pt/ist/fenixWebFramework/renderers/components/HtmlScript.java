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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlScript extends HtmlComponent {

    private String charset;
    private String contentType;
    private String source;
    private boolean defer;
    private boolean conditional;

    private CharSequence script;

    public HtmlScript() {
        super();
    }

    public HtmlScript(String contentType, String source) {
        super();

        this.contentType = contentType;
        this.source = source;
    }

    public HtmlScript(String contentType, String source, boolean conditional) {
        this(contentType, source);

        this.conditional = conditional;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isDefer() {
        return this.defer;
    }

    public void setDefer(boolean defer) {
        this.defer = defer;
    }

    public boolean isConditional() {
        return conditional;
    }

    public void setConditional(boolean conditional) {
        this.conditional = conditional;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public CharSequence getScript() {
        return this.script;
    }

    public void setScript(CharSequence script) {
        this.script = script;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        if (isConditional() && wasIncluded(context)) {
            tag.setName(null);
            return tag;
        }

        tag.setName("script");
        tag.setAttribute("charset", getCharset());
        tag.setAttribute("type", getContentType());
        tag.setAttribute("src", getSource());

        if (isDefer()) {
            tag.setAttribute("defer", "defer");
        }

        if (getScript() != null) {
            tag.setText(getScript().toString());
        }

        return tag;
    }

    private boolean wasIncluded(PageContext context) {
        String includeId = null;

        if (getSource() != null) {
            includeId = getSource();
        } else if (getScript() != null) {
            includeId = String.valueOf(getScript().hashCode());
        }

        if (includeId == null) {
            return false;
        }

        ServletRequest request = context.getRequest();
        String conditionalName = getClass().getName() + "/included/" + includeId;

        if (request.getAttribute(conditionalName) != null) {
            return true;
        } else {
            request.setAttribute(conditionalName, true);
            return false;
        }
    }
}
