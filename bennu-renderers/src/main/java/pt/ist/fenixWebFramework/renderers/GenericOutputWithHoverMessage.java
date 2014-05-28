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
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class GenericOutputWithHoverMessage extends AbstractToolTipRenderer {

    private String format;

    private String hoverMessage;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getHoverMessage() {
        return hoverMessage;
    }

    public void setHoverMessage(String hover) {
        this.hoverMessage = hover;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {

        return new ToolTipLayout() {
            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                PresentationContext context = getContext();

                context.setLayout(getSubLayout());
                context.setProperties(getProperties());

                HtmlComponent component = RenderKit.getInstance().render(context, object, type);
                String hoverMessage = null;

                if (getFormat() != null) {
                    hoverMessage = RenderUtils.getFormattedProperties(getFormat(), getTargetObject(object));
                } else {
                    if (isKey()) {
                        hoverMessage = RenderUtils.getResourceString(getBundle(), getHoverMessage());
                    } else {
                        hoverMessage = getHoverMessage();
                    }
                }

                return wrapUpCompletion(component, new HtmlText(hoverMessage, isEscape()));
            }
        };
    }

}
