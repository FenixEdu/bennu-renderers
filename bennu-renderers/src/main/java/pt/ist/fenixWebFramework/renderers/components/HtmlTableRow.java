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

import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell.CellType;
import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlTableRow extends HtmlComponent {

    private List<HtmlTableCell> cells = null;

    private String align;

    public HtmlTableRow() {
        cells = new ArrayList<HtmlTableCell>();
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    protected void addCell(HtmlTableCell cell) {
        this.cells.add(cell);
    }

    public HtmlTableCell createCell() {
        HtmlTableCell cell = new HtmlTableCell();

        addCell(cell);
        return cell;
    }

    public HtmlTableCell createCell(String text) {
        HtmlTableCell cell = createCell();
        cell.setText(text);

        return cell;
    }

    public HtmlTableCell createCell(CellType cellType) {
        HtmlTableCell cell = new HtmlTableCell(cellType);

        addCell(cell);
        return cell;
    }

    public List<HtmlTableCell> getCells() {
        return cells;
    }

    @Override
    public List<HtmlComponent> getChildren() {
        return new ArrayList<HtmlComponent>(cells);
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("tr");
        tag.setAttribute("align", getAlign());

        for (HtmlTableCell cell : this.cells) {
            tag.addChild(cell.getOwnTag(context));
        }

        return tag;
    }
}
