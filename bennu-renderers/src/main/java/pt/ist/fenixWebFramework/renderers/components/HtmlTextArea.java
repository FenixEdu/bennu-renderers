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

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlTextArea extends HtmlSimpleValueComponent {

    private Integer rows;
    private Integer columns;
    private boolean readOnly;

    private String onFocus;
    private String onBlur;
    private String onSelect;
    private String onChange;

    public Integer getColumns() {
        return this.columns;
    }

    public void setColumns(Integer colums) {
        this.columns = colums;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Integer getRows() {
        return this.rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getOnBlur() {
        return this.onBlur;
    }

    public void setOnBlur(String onBlur) {
        this.onBlur = onBlur;
    }

    public String getOnFocus() {
        return this.onFocus;
    }

    public void setOnFocus(String onFocus) {
        this.onFocus = onFocus;
    }

    public String getOnSelect() {
        return this.onSelect;
    }

    public void setOnSelect(String onSelect) {
        this.onSelect = onSelect;
    }

    public String getOnChange() {
        return onChange;
    }

    public void setOnChange(String onChange) {
        this.onChange = onChange;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        addClass("form-control");
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("textarea");
        tag.removeAttribute("value");

        tag.setAttribute("rows", getRows());
        tag.setAttribute("cols", getColumns());
        tag.setAttribute("onfocus", getOnFocus());
        tag.setAttribute("onblur", getOnBlur());
        tag.setAttribute("onselect", getOnSelect());
        tag.setAttribute("onchange", getOnChange());

        if (isReadOnly()) {
            tag.setAttribute("readonly", "readonly");
        }

        if (isDisabled()) {
            tag.setAttribute("disabled", "disabled");
        }

        if (getValue() != null) {
            tag.setText(HtmlText.escape(getValue(), false));
        } else {
            tag.setText("");
        }

        return tag;
    }

}
