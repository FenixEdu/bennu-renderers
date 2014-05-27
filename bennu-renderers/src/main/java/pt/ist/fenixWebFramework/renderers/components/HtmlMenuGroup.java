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

public class HtmlMenuGroup extends HtmlMenuEntry {

    private List<HtmlMenuOption> options;

    public HtmlMenuGroup(String label) {
        super(label, false);

        this.options = new ArrayList<HtmlMenuOption>();
    }

    public HtmlMenuOption createOption() {
        HtmlMenuOption option = new HtmlMenuOption();

        this.options.add(option);

        return option;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("optgroup");

        for (HtmlMenuOption option : this.options) {
            tag.addChild(option.getOwnTag(context));
        }

        return tag;
    }

    @Override
    public List<HtmlComponent> getChildren() {
        List<HtmlComponent> children = super.getChildren();

        children.addAll(this.options);

        return children;
    }

    @Override
    public void setSelected(String value) {
        for (HtmlMenuOption option : this.options) {
            option.setSelected(value);
        }
    }

    @Override
    public boolean isSelected() {
        for (HtmlMenuOption option : this.options) {
            if (option.isSelected()) {
                return true;
            }
        }

        return false;
    }
}
