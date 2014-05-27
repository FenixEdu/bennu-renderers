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

public class HtmlList extends HtmlComponent {

    private List<HtmlListItem> items;
    private boolean ordered;

    public HtmlList() {
        super();

        this.items = new ArrayList<HtmlListItem>();
        this.ordered = false;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public boolean isOrdered() {
        return this.ordered;
    }

    public HtmlListItem createItem() {
        HtmlListItem newItem = new HtmlListItem();

        this.items.add(newItem);

        return newItem;
    }

    public HtmlListItem createItem(int index) {
        HtmlListItem newItem = new HtmlListItem();

        this.items.add(index, newItem);

        return newItem;
    }

    @Override
    public List<HtmlComponent> getChildren() {
        List<HtmlComponent> children = new ArrayList<HtmlComponent>(super.getChildren());

        children.addAll(this.items);

        return children;
    }

    public List<HtmlListItem> getItems() {
        return this.items;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName(isOrdered() ? "ol" : "ul");

        for (HtmlListItem item : this.items) {
            tag.addChild(item.getOwnTag(context));
        }

        return tag;
    }
}
