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
package pt.ist.fenixWebFramework.rendererExtensions.components;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class HtmlEditor extends HtmlSimpleValueComponent {

    private int width;
    private int heigth;

    public HtmlEditor() {
        super();

        setWidth(600);
        setHeigth(400);
    }

    public int getHeigth() {
        return this.heigth;
    }

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private HtmlTag scriptTag(String script) {
        HtmlTag tag = new HtmlTag("script");

        tag.setAttribute("type", "text/javascript");
        tag.setAttribute("language", "Javascript");

        tag.setText("<!--\n" + script + "\n//-->");

        return tag;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName(null);

        HtmlTag scriptTagInit = scriptTag("initEditor();");
        HtmlTag scriptTagWrite =
                scriptTag("writeRichText('" + getName() + "', '" + (getValue() == null ? "" : getValue().trim()) + "', "
                        + getWidth() + ", " + getHeigth() + ", true, false);" + "\n//-->");

        HtmlTag noscriptTag = new HtmlTag("noscript", RenderUtils.getResourceString("javascript.notSupported"));
        if (noscriptTag.getText() == null) {
            noscriptTag.setText("");
        }

        tag.addChild(noscriptTag);
        tag.addChild(scriptTagInit);
        tag.addChild(scriptTagWrite);

        return tag;
    }

}
