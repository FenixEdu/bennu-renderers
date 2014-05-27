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

public class HtmlSelectList extends HtmlMultipleValueComponent {

    private Integer size;
    private Integer tabIndex;

    private List<HtmlMenuOption> options;

    public HtmlSelectList() {
        super();

        this.options = new ArrayList<HtmlMenuOption>();
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

    public HtmlMenuOption createOption(String text) {
        HtmlMenuOption option = new HtmlMenuOption(text);

        this.options.add(option);

        return option;
    }

    @Override
    public void setValues(String... values) {
        super.setValues(values);

        for (String value : values) {
            for (HtmlMenuOption option : getOptions()) {
                if (option.getValue().equals(value)) {
                    option.setSelected(true);
                }
            }
        }
    }

    public List<HtmlMenuOption> getOptions() {
        return this.options;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("select");

        tag.setAttribute("size", getSize());
        tag.setAttribute("multiple", true);
        tag.setAttribute("tabindex", getTabIndex());

        if (isDisabled()) {
            tag.setAttribute("disabled", true);
        }

        for (HtmlMenuEntry entry : getOptions()) {
            tag.addChild(entry.getOwnTag(context));
        }

        return tag;
    }

    @Override
    public List<HtmlComponent> getChildren() {
        List<HtmlComponent> children = super.getChildren();

        children.addAll(getOptions());

        return children;
    }

}
