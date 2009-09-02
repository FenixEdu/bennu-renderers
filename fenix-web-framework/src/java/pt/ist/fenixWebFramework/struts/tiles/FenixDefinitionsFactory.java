/**
 * 
 */
package pt.ist.fenixWebFramework.struts.tiles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.apache.struts.tiles.ComponentDefinition;
import org.apache.struts.tiles.DefinitionsFactoryException;
import org.apache.struts.tiles.NoSuchDefinitionException;
import org.apache.struts.tiles.xmlDefinition.I18nFactorySet;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
public class FenixDefinitionsFactory extends I18nFactorySet {

    private ComponentDefinition defaultModuleDefinition;

    private String defaultModuleDefinitionName;

    private final Map<String, ComponentDefinition> definitionsCache = new HashMap<String, ComponentDefinition>();

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

//	Set<String> processedTiles = (Set<String>) request.getAttribute("__processedTiles");
//	if (processedTiles == null) {
//	    processedTiles = new HashSet<String>();
//	    request.setAttribute("__processedTiles", processedTiles);
//	} else if (processedTiles.contains(tileName)) {
//	    return null;
//	}
//	processedTiles.add(tileName);

	if (definitionsCache.containsKey(tileName)) {
	    return definitionsCache.get(tileName);
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
	partialTile.updateComponentDefinition(componentDefinition);
	definitionsCache.put(tileName, componentDefinition);
	return componentDefinition;
    }
}
