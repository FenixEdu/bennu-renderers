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
package pt.ist.fenixWebFramework.renderers.utils;

import java.util.Properties;

import pt.ist.fenixWebFramework.renderers.Renderer;

/**
 * RendererDescription is used to mantain the renderer's class and the default
 * properties associated with that particular renderer.
 * 
 * @author cfgi
 */
public class RendererDescription {
    private Class<? extends Renderer> renderer;

    private Properties properties;

    public RendererDescription(Class<Renderer> renderer, Properties defaultProperties) {
        this.renderer = renderer;
        this.properties = defaultProperties;
    }

    public Properties getProperties() {
        return properties;
    }

    public Class<? extends Renderer> getRenderer() {
        return renderer;
    }

    public Renderer createRenderer() {
        Renderer renderer = null;

        try {
            renderer = getRenderer().newInstance();

            if (properties != null) {
                RenderUtils.setProperties(renderer, properties);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return renderer;
    }
}