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
package pt.ist.fenixWebFramework.renderers.layouts;

import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;

public abstract class FlowLayout extends Layout {

    private String eachClasses;
    private String eachStyle;

    private boolean eachInline = true;

    public void setEachClasses(String classes) {
        this.eachClasses = classes;
    }

    public String getEachClasses() {
        return this.eachClasses;
    }

    public void setEachStyle(String style) {
        this.eachStyle = style;
    }

    public String getEachStyle() {
        return this.eachStyle;
    }

    public boolean isEachInline() {
        return eachInline;
    }

    public void setEachInline(boolean eachInline) {
        this.eachInline = eachInline;
    }

    @Override
    public String[] getPropertyNames() {
        return mergePropertyNames(super.getPropertyNames(), new String[] { "eachClasses", "eachStyle", "eachInline" });
    }

    @Override
    public HtmlComponent createComponent(Object object, Class type) {
        HtmlComponent container = getContainer();

        while (hasMoreComponents()) {
            HtmlComponent component = getNextComponent();

            addComponent(container, component);
        }

        return container;
    }

    protected abstract boolean hasMoreComponents();

    protected abstract HtmlComponent getNextComponent();

    protected void addComponent(HtmlComponent component, HtmlComponent child) {
        HtmlContainer eachContainer = getEachContainer();
        eachContainer.addChild(child);

        ((HtmlContainer) component).addChild(eachContainer);
    }

    protected HtmlComponent getContainer() {
        return new HtmlInlineContainer();
    }

    protected HtmlContainer getEachContainer() {
        if (isEachInline()) {
            return new HtmlInlineContainer();
        } else {
            return new HtmlBlockContainer();
        }
    }

    @Override
    public void applyStyle(HtmlComponent component) {
        super.applyStyle(component);

        String[] eachClasses = new String[] { null };
        if (this.eachClasses != null) {
            eachClasses = this.eachClasses.split(",", -1);
        }

        int index = 0;
        for (HtmlComponent child : component.getChildren()) {
            child.setClasses(eachClasses[index % eachClasses.length]);
            child.setStyle(this.eachStyle);

            index++;
        }
    }
}
