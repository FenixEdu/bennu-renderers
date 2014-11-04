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
package pt.ist.fenixWebFramework.rendererExtensions.htmlEditor;

import pt.ist.fenixWebFramework.renderers.InputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextArea;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * Allows you to use the <a href="http://tinymce.moxiecode.com/">TinyMCE</a>
 * Javascript HTML WYSIWYG editor.
 * 
 * @author cfgi
 */
public class RichTextInputRenderer extends InputRenderer {

    private Integer columns;
    private Integer rows;

    private boolean safe;

    public RichTextInputRenderer() {
        super();

        setColumns(50);
        setRows(10);
    }

    public Integer getColumns() {
        return this.columns;
    }

    /**
     * The number of column of the textarea.
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
     * The number of rows of the textarea.
     * 
     * @property
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public boolean isSafe() {
        return this.safe;
    }

    /**
     * If this property is set to <tt>true</tt> then the input will be filtered
     * and any unsupported HTML will be removed or escaped to the corresponding
     * entities.
     * 
     * @property
     */
    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                HtmlTextArea textArea = new HtmlTextArea();
                textArea.setValue((String) object);

                textArea.setColumns(getColumns());
                textArea.setRows(getRows());

                textArea.addClass("form-control");
                textArea.setAttribute("bennu-html-editor", "true");

                if (isSafe()) {
                    textArea.setConverter(new JsoupSafeHtmlConverter());
                }

                getInputContext().requireToolkit();

                return textArea;
            }
        };
    }

}
