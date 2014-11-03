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

public class HtmlMenu extends HtmlSimpleValueComponent {

    private Integer size;
    private Integer tabIndex;
    private String onChange;

    private final List<HtmlMenuEntry> entries;

    public HtmlMenu() {
        super();

        this.entries = new ArrayList<HtmlMenuEntry>();
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public String getOnChange() {
        return onChange;
    }

    public void setOnChange(String onChange) {
        this.onChange = onChange;
    }

    public List<HtmlMenuEntry> getEntries() {
        return this.entries;
    }

    public HtmlMenuGroup createGroup(String label) {
        HtmlMenuGroup group = new HtmlMenuGroup(label);

        this.entries.add(group);

        return group;
    }

    public HtmlMenuOption createDefaultOption(String text) {
        HtmlMenuOption option = new HtmlMenuOption(text, "") {
            @Override
            public void setValue(String value) {
                // Does not allow value to change
            }
        };

        if (this.entries.isEmpty()) {
            this.entries.add(option);
        } else {
            this.entries.set(0, option);
        }

        return option;
    }

    public HtmlMenuOption createOption(String text) {
        HtmlMenuOption option = new HtmlMenuOption(text);

        this.entries.add(option);

        return option;
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);

        for (HtmlMenuEntry entry : this.entries) {
            entry.setSelected(value);
        }
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        addClass("form-control");
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("select");

        tag.setAttribute("size", getSize());
        tag.setAttribute("onchange", getOnChange());

        if (isDisabled()) {
            tag.setAttribute("disabled", true);
        }
        tag.setAttribute("tabindex", getTabIndex());

        for (HtmlMenuEntry entry : this.entries) {
            tag.addChild(entry.getOwnTag(context));
        }

        return tag;
    }

    @Override
    public List<HtmlComponent> getChildren() {
        List<HtmlComponent> children = super.getChildren();

        children.addAll(this.entries);

        return children;
    }
}
