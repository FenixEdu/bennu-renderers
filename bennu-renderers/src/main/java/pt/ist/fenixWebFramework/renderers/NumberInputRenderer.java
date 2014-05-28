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
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

/**
 * This renderer provides a simple way of doing the input of numbers. The
 * value is read with an text input field and converted to the appropriate
 * type.
 * 
 * <p>
 * Example: <input type="text" value="10"/>
 * 
 * @author cfgi
 */
public abstract class NumberInputRenderer extends StringInputRenderer {
    @Override
    public HtmlComponent render(Object targetObject, Class type) {
        Number number = (Number) targetObject;

        String text;
        if (number == null) {
            text = "";
        } else {
            text = number.toString();
        }

        return super.render(text, type);
    }

    @Override
    protected HtmlComponent createTextField(Object object, Class type) {
        HtmlContainer fieldComponent = (HtmlContainer) super.createTextField(object, type);

        HtmlFormComponent formComponent = (HtmlFormComponent) fieldComponent.getChildren().get(0);
        formComponent.setConverter(getConverter());

        HtmlContainer container = new HtmlInlineContainer();
        container.addChild(formComponent);
        container.addChild(new HtmlText(getFormatLabel()));
        return container;
    }

    protected abstract Converter getConverter();
}
