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

import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.tiles.DefinitionsFactoryConfig;
import org.apache.struts.tiles.TilesPlugin;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
public class FenixTilesPlugin extends TilesPlugin {

    @Override
    protected DefinitionsFactoryConfig readFactoryConfig(ActionServlet servlet, ModuleConfig config) throws ServletException {
        final DefinitionsFactoryConfig factoryConfig = super.readFactoryConfig(servlet, config);
        factoryConfig.setAttribute("defaultTileDefinition",
                this.currentPlugInConfigObject.getProperties().get("defaultTileDefinition"));
        return factoryConfig;
    }

}
