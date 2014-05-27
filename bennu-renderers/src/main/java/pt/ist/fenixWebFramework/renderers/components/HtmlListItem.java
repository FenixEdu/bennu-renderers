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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlListItem extends HtmlComponent {

    private List<HtmlComponent> body;

    public HtmlListItem() {
        super();

        this.body = new ArrayList<HtmlComponent>();
    }

    public void setBody(HtmlComponent body) {
        this.body = new ArrayList<HtmlComponent>();
        this.body.add(body);
    }

    public HtmlComponent getBody() {
        if (this.body.isEmpty()) {
            return null;
        } else {
            return this.body.get(0);
        }
    }

    public void addChild(HtmlComponent component) {
        this.body.add(component);
    }

    @Override
    public List<HtmlComponent> getChildren() {
        List<HtmlComponent> children = super.getChildren();

        if (this.body != null) {
            children.addAll(this.body);
        }

        return children;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("li");

        for (HtmlComponent child : this.body) {
            if (child != null) {
                tag.addChild(child.getOwnTag(context));
            }
        }

        return tag;
    }
}
