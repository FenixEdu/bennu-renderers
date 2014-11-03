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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import pt.ist.fenixWebFramework.renderers.exceptions.NoRendererException;

public class RendererRegistry {

    private final ClassHierarchyTable<Map<String, RendererDescription>> renderersTable;

    public RendererRegistry() {
        super();

        this.renderersTable = new ClassHierarchyTable<Map<String, RendererDescription>>();
    }

    public void registerRenderer(Class type, String layout, Class renderer, Properties defaultProperties) {
        Map<String, RendererDescription> layoutsTable = this.renderersTable.getUnspecific(type);

        if (layoutsTable == null) {
            this.renderersTable.put(type, new HashMap<String, RendererDescription>());
            layoutsTable = this.renderersTable.getUnspecific(type);
        }

        layoutsTable.put(layout, new RendererDescription(renderer, defaultProperties));
    }

    public RendererDescription getRenderDescription(Class objectType, final String layout) {
        Map<String, RendererDescription> layoutsTable = renderersTable.get(objectType, table -> table.get(layout) != null);

        if (layoutsTable == null) {
            throw new NoRendererException(objectType, layout);
        }

        return layoutsTable.get(layout);
    }

    public RendererDescription getExactRenderDescription(Class objectType, String layout) {
        Map<String, RendererDescription> layoutsTable = renderersTable.getUnspecific(objectType);

        if (layoutsTable == null) {
            throw new NoRendererException(objectType, layout);
        }

        return layoutsTable.get(layout);
    }
}
