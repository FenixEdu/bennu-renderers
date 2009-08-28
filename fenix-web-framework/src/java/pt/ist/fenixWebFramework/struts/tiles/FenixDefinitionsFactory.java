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

    private static class PartialTileDefinition {
	private final String forward;

	private final String superTile;

	public PartialTileDefinition(String forward, String superTile) {
	    this.forward = forward;
	    this.superTile = superTile;
	}

	public String getForward() {
	    return forward;
	}

	public String getSuperTile() {
	    return superTile;
	}

    }

    private static final String DEFAULT_MODULE = "defaultModule";

    private ComponentDefinition defaultModuleDefinition;

    private String defaultModuleDefinitionName;

    private final Map<String, ComponentDefinition> definitionsCache = new HashMap<String, ComponentDefinition>();

    static private Map<String, String> forwardsUsingDefaultModule = new HashMap<String, String>();

    static private Map<String, PartialTileDefinition> forwardsUsingCustomTile = new HashMap<String, PartialTileDefinition>();

    static public String registerDefinition(String forward) {
	String defaultTileName = forward + "-" + DEFAULT_MODULE;
	if (!forwardsUsingDefaultModule.containsKey(defaultTileName)) {
	    forwardsUsingDefaultModule.put(defaultTileName, forward);
	}
	return defaultTileName;
    }

    static public String registerDefinition(String forward, String superTile) {
	if ((superTile == null) || (superTile.isEmpty())) {
	    return registerDefinition(forward);
	}
	String customTileName = forward + "+" + superTile;
	if (!forwardsUsingCustomTile.containsKey(customTileName)) {
	    PartialTileDefinition customTile = new PartialTileDefinition(forward, superTile);
	    forwardsUsingCustomTile.put(customTileName, customTile);
	}
	return customTileName;
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

	if (forwardsUsingDefaultModule.containsKey(tileName)) {
	    if (defaultModuleDefinition == null) {
		if (defaultModuleDefinitionName != null) {
		    defaultModuleDefinition = super.getDefinition(defaultModuleDefinitionName, request, servletContext);
		} else {
		    return null;
		}
	    }

	    return createComponentDefinition(tileName, forwardsUsingDefaultModule.get(tileName), defaultModuleDefinition);
	}

	if (forwardsUsingCustomTile.containsKey(tileName)) {
	    PartialTileDefinition customTile = forwardsUsingCustomTile.get(tileName);
	    ComponentDefinition superComponent = super.getDefinition(customTile.getSuperTile(), request, servletContext);
	    if (superComponent == null) {
		throw new NoSuchDefinitionException();
	    }

	    return createComponentDefinition(tileName, customTile.getForward(), superComponent);
	}

	return super.getDefinition(tileName, request, servletContext);
    }

    private ComponentDefinition createComponentDefinition(String tileName, String body, ComponentDefinition superComponent) {
	ComponentDefinition componentDefinition = new ComponentDefinition(superComponent);
	componentDefinition.putAttribute("body", body);
	definitionsCache.put(tileName, componentDefinition);
	return componentDefinition;
    }
}
