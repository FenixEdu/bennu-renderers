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

public class HtmlTable extends HtmlComponent {

    private final List<HtmlTableRow> rows;

    private HtmlTableHeader header;

    private String summary;
    private String width; // can be "100%"
    private String border;
    private String cellSpacing;
    private String cellPadding;
    private String caption;

    // frame and rules were ignored

    public HtmlTable() {
        rows = new ArrayList<HtmlTableRow>();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getWidth() {
        return width;
    }

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public String getCellPadding() {
        return cellPadding;
    }

    public void setCellPadding(String cellPadding) {
        this.cellPadding = cellPadding;
    }

    public String getCellSpacing() {
        return cellSpacing;
    }

    public void setCellSpacing(String cellSpacing) {
        this.cellSpacing = cellSpacing;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public HtmlTableRow createRow() {
        HtmlTableRow row = new HtmlTableRow();

        this.rows.add(row);

        return row;
    }

    public void removeRow(HtmlTableRow row) {
        this.rows.remove(row);
    }

    public List<HtmlTableRow> getRows() {
        return rows;
    }

    public HtmlTableHeader createHeader() {
        this.header = new HtmlTableHeader();

        return this.header;
    }

    public HtmlTableHeader getHeader() {
        return header;
    }

    @Override
    public List<HtmlComponent> getChildren() {
        List<HtmlComponent> children = new ArrayList<HtmlComponent>(super.getChildren());

        if (this.header != null) {
            children.add(this.header);
        }

        children.addAll(this.rows);

        return children;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {

        HtmlTag tag = super.getOwnTag(context);

        tag.setName("table");

        tag.setAttribute("class", getClasses() == null ? "table" : getClasses() + " table");
        tag.setAttribute("summary", summary);
        tag.setAttribute("width", width);
        tag.setAttribute("border", border);
        tag.setAttribute("cellSpacing", cellSpacing);
        tag.setAttribute("cellPadding", cellPadding);

        if (this.caption != null) {
            tag.addChild(new HtmlTag("caption", this.caption));
        }

        if (this.header != null) {
            HtmlTag headerTag = header.getOwnTag(context);
            tag.addChild(headerTag);
        }

        if (this.rows.size() >= 0) {
            HtmlTag innerTag = new HtmlTag("tbody");
            for (HtmlTableRow row : this.rows) {
                innerTag.addChild(row.getOwnTag(context));
            }
            tag.addChild(innerTag);
        } else {
            HtmlTag innerTag = new HtmlTag("tbody");
            tag.addChild(innerTag);
        }

        // Always generate end tag
        if (tag.getChildren().isEmpty()) {
            tag.addChild(new HtmlTag(null));
        }

        return tag;
    }
}
