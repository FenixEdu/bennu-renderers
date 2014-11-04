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

import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

/**
 * This renderer has the same behaviour than
 * {@link LocalizedTextInputRenderer.sourceforge.fenixedu.presentationTier.renderers.MultiLanguageTextInputRenderer} but uses a
 * rich-text editor in place
 * of a textarea for browsers that support
 * it.
 * 
 * @author cfgi
 */
public class LocalizedRichTextInputRenderer extends LocalizedTextInputRenderer {

    private boolean safe;

    public boolean isSafe() {
        return safe;
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
    protected HtmlSimpleValueComponent getInputComponent() {
        HtmlSimpleValueComponent textArea = super.getInputComponent();

        textArea.setAttribute("bennu-html-editor", "true");
        return textArea;
    }

    @Override
    protected Converter getConverter() {
        if (isSafe()) {
            return new LocalizedStringSafeHtmlConverter();
        } else {
            return super.getConverter();
        }
    }

}
