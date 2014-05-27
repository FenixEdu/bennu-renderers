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
/**
 * 
 */
package pt.ist.fenixWebFramework.struts.tiles;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.apache.struts.tiles.ComponentDefinition;
import org.apache.struts.tiles.DefinitionsFactoryException;
import org.apache.struts.tiles.NoSuchDefinitionException;
import org.apache.struts.tiles.xmlDefinition.I18nFactorySet;
import org.fenixedu.commons.i18n.I18N;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
public class FenixDefinitionsFactory extends I18nFactorySet {
    private static final long serialVersionUID = 3791313145787770679L;

    private class DefinitionsCache extends HashMap<String, ComponentDefinition> {
        private static final long serialVersionUID = -6628486073076074205L;

        public DefinitionsCache(String tileName, ComponentDefinition componentDefinition) {
            put(tileName, componentDefinition);
        }
    }

    private ComponentDefinition defaultModuleDefinition;

    private String defaultModuleDefinitionName;

    private final Map<Locale, DefinitionsCache> caches = new HashMap<Locale, DefinitionsCache>();

    static private Map<String, PartialTileDefinition> partialDefinitions = new HashMap<String, PartialTileDefinition>();

    static public void registerDefinition(PartialTileDefinition tileDefinition) {
        if (!partialDefinitions.containsKey(tileDefinition.getName())) {
            partialDefinitions.put(tileDefinition.getName(), tileDefinition);
        }
    }

    @Override
    public void initFactory(ServletContext servletContext, Map properties) throws DefinitionsFactoryException {
        this.defaultModuleDefinitionName = (String) properties.get("defaultTileDefinition");
        super.initFactory(servletContext, properties);
    }

    @Override
    public ComponentDefinition getDefinition(String tileName, ServletRequest request, ServletContext servletContext)
            throws NoSuchDefinitionException, DefinitionsFactoryException {

        Locale locale = I18N.getLocale();
        if (caches.containsKey(locale)) {
            DefinitionsCache cache = caches.get(locale);
            if (cache.containsKey(tileName)) {
                return cache.get(tileName);
            }
        }

        PartialTileDefinition partialTile = partialDefinitions.get(tileName);
        if (partialTile == null) {
            return super.getDefinition(tileName, request, servletContext);
        }

        ComponentDefinition superComponent;
        if (partialTile.hasExtend()) {
            superComponent = super.getDefinition(partialTile.getExtend(), request, servletContext);
        } else {
            if (defaultModuleDefinition == null) {
                defaultModuleDefinition = super.getDefinition(defaultModuleDefinitionName, request, servletContext);
            }
            superComponent = defaultModuleDefinition;
        }
        if (superComponent == null) {
            throw new NoSuchDefinitionException();
        }

        ComponentDefinition componentDefinition = new ComponentDefinition(superComponent);
        partialTile.updateComponentDefinition(componentDefinition, locale);

        if (caches.containsKey(locale)) {
            caches.get(locale).put(tileName, componentDefinition);
        } else {
            caches.put(locale, new DefinitionsCache(tileName, componentDefinition));
        }

        return componentDefinition;
    }
}
