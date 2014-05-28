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
package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextArea;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;

/**
 * This renderer provides an input mechanism for a string that allows long
 * texts to be inserted or span over multiple lines.
 * 
 * <p>
 * Example: <textarea>&lt;the text&gt;</textarea>
 * 
 * @author cfgi
 */
public class TextAreaInputRenderer extends InputRenderer {

    private Integer columns;
    private Integer rows;

    public Integer getColumns() {
        return this.columns;
    }

    /**
     * The number of columns of the generated text area.
     * 
     * @property
     */
    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Integer getRows() {
        return this.rows;
    }

    /**
     * The number of rows of the generated text area.
     * 
     * @property
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                HtmlTextArea textArea = new HtmlTextArea();
                textArea.setTargetSlot((MetaSlotKey) getInputContext().getMetaObject().getKey());

                textArea.setValue((String) object);

                return textArea;
            }

            @Override
            public void applyStyle(HtmlComponent component) {
                super.applyStyle(component);

                HtmlTextArea textArea = (HtmlTextArea) component;

                textArea.setColumns(getColumns());
                textArea.setRows(getRows());
            }
        };
    }

}
