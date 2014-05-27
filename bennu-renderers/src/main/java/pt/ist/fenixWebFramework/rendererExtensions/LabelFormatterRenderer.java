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

import java.util.Properties;

import pt.ist.fenixWebFramework.rendererExtensions.util.RendererMessageResourceProvider;
import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.utl.ist.fenix.tools.resources.LabelFormatter;

public class LabelFormatterRenderer extends OutputRenderer {

    private final Properties bundleMappings;

    public LabelFormatterRenderer() {
        super();

        this.bundleMappings = new Properties();

    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {

                return new HtmlText(((LabelFormatter) object).toString(new RendererMessageResourceProvider(
                        LabelFormatterRenderer.this.bundleMappings)));
            }

        };
    }

    /**
     * 
     * 
     * @property
     */
    public void setBundleName(String bundle, String name) {
        this.bundleMappings.put(bundle, name);
    }

    public String getBundleName(String bundle) {
        return this.bundleMappings.getProperty(bundle);
    }

}
