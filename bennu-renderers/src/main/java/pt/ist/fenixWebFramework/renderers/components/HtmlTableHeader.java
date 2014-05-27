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

public class HtmlTableHeader extends HtmlComponent {

    private List<HtmlTableRow> rows;

    public HtmlTableHeader() {
        rows = new ArrayList<HtmlTableRow>();
    }

    public HtmlTableRow createRow() {
        HtmlTableRow row = new HtmlTableHeaderRow();

        this.rows.add(row);
        return row;
    }

    @Override
    public List<HtmlComponent> getChildren() {
        return new ArrayList<HtmlComponent>(rows);
    }

    public List<HtmlTableRow> getRows() {
        return this.rows;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("thead");

        for (HtmlTableRow row : this.rows) {
            tag.addChild(row.getOwnTag(context));
        }

        return tag;
    }
}
