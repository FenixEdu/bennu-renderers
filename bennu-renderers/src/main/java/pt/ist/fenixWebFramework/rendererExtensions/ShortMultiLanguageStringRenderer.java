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

import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * This renderer has the same behaviour as the
 * {@link net.sourceforge.fenixedu.presentationTier.renderers.MultiLanguageStringRenderer} but the context is limited to a certain
 * number of characters.
 * 
 * @author cfgi
 */
public class ShortMultiLanguageStringRenderer extends MultiLanguageStringRenderer {

    private Integer length;
    private boolean tooltipShown;

    public ShortMultiLanguageStringRenderer() {
        super();

        this.tooltipShown = true;
    }

    public boolean isTooltipShown() {
        return tooltipShown;
    }

    /**
     * Chooses if a tooltip is added to the text when text is bigger than the
     * value given in {@link #setLength(Integer) length}.
     * 
     * @property
     */
    public void setTooltipShown(boolean tooltipShown) {
        this.tooltipShown = tooltipShown;
    }

    @Override
    public Integer getLength() {
        return this.length;
    }

    /**
     * Choose the amount of characters displayed. If the original text has more
     * than the value given the elipses are added.
     * 
     * @property
     */
    @Override
    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    protected String getRenderedText(LocalizedString mlString) {
        String content = super.getRenderedText(mlString);

        if (content != null && getLength() != null) {
            if (content.length() > getLength()) {
                return content.substring(0, getLength()) + "...";
            }
        }

        return content;
    }

    @Override
    protected HtmlComponent renderComponent(Layout layout, Object object, Class type) {
        HtmlComponent component = super.renderComponent(layout, object, type);

        LocalizedString mlString = getLocalized(object);

        String previous = super.getRenderedText(mlString);
        String current = getRenderedText(mlString);

        if (isTooltipShown()) {
            if (!String.valueOf(previous).equals(String.valueOf(current))) {
                component.setTitle(HtmlText.escape(previous));
            }
        }

        return component;
    }
}
