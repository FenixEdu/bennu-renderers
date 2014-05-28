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
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;

import com.google.common.base.Predicate;

/**
 * This renderer provides a standard way of doing the input of a string. The
 * string is read with a text input field.
 * 
 * <p>
 * Example: <input type="text" value="the string"/>
 * 
 * @author cfgi
 */
public class StringInputRenderer extends TextFieldRenderer {

    @Override
    protected HtmlComponent createTextField(Object object, Class type) {
        String string = (String) object;

        HtmlTextInput input = new HtmlTextInput();
        input.setValue(string);

        HtmlContainer container = new HtmlInlineContainer();
        container.addChild(input);
        container.addChild(new HtmlText(getFormatLabel()));

        return container;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new StringInputFieldLayout();
    }

    class StringInputFieldLayout extends TextFieldLayout {

        @Override
        protected void setContextSlot(HtmlComponent component, MetaSlotKey slotKey) {
            HtmlComponent actualComponent =
                    component instanceof HtmlTextInput ? component : component.getChild(new Predicate<HtmlComponent>() {
                        @Override
                        public boolean apply(HtmlComponent component) {
                            return component instanceof HtmlTextInput;
                        }
                    });
            super.setContextSlot(actualComponent, slotKey);
        }

        @Override
        public void applyStyle(HtmlComponent component) {
            HtmlComponent actualComponent =
                    component instanceof HtmlTextInput ? component : component.getChild(new Predicate<HtmlComponent>() {
                        @Override
                        public boolean apply(HtmlComponent component) {
                            return component instanceof HtmlTextInput;
                        }
                    });
            super.applyStyle(actualComponent);
        }

    }
}
