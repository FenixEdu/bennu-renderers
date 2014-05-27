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

import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * This renderer interprets the value of String as a web color (like #ff4455")
 * and presents the value and a demonstration of what the color looks like.
 * 
 * @author cfgi
 */
public class WebcolorRenderer extends OutputRenderer {

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                String value = (String) object;

                if (value == null) {
                    return new HtmlText();
                }

                HtmlInlineContainer container = new HtmlInlineContainer();

                HtmlText color = new HtmlText("&nbsp;", false);
                color.setStyle(String.format("border: 1px solid %1$s; background-color: %1$s; padding: 0px 7px;", value));

                container.addChild(new HtmlText(value));
                container.addChild(color);

                return container;
            }

        };
    }

}
