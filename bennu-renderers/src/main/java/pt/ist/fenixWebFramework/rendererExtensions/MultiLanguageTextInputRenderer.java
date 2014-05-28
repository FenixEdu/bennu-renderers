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
package pt.ist.fenixWebFramework.rendererExtensions;

import pt.ist.fenixWebFramework.renderers.components.HtmlActionLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextArea;

/**
 * This renderer extends the {@link MultiLanguageStringInputRenderer}. The only difference
 * is that the input is done in an text area instead of a text field. Because of that, this
 * renderer ignores the {@link #setSize(Integer) size} property and provides additional
 * properties to configure the {@link #setColumns(Integer) columns} and {@link #setRows(Integer) rows} of each text area.
 * 
 * @author cfgi
 */
public class MultiLanguageTextInputRenderer extends MultiLanguageStringInputRenderer {

    public Integer rows;
    public Integer columns;

    /**
     * Allows you to configure the columns if the text area used for the input in each language.
     * 
     * @property
     */
    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Integer getColumns() {
        return this.columns;
    }

    /**
     * Allows you to configure the rows if the text area used for the input in each language.
     * 
     * @property
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getRows() {
        return this.rows;
    }

    @Override
    protected void configureLanguageContainer(HtmlContainer languageContainer, HtmlSimpleValueComponent input,
            HtmlSimpleValueComponent languageComponent, HtmlActionLink removeLink) {
        languageComponent.setStyle("display: block;");

        languageContainer.addChild(languageComponent);
        languageContainer.addChild(input);
        languageContainer.addChild(removeLink);
    }

    @Override
    protected HtmlSimpleValueComponent getInputComponent() {
        HtmlTextArea textArea = new HtmlTextArea();

        textArea.setColumns(getColumns());
        textArea.setRows(getRows());

        return textArea;
    }

}
