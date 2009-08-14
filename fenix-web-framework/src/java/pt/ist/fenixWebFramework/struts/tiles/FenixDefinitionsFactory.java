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

    private static final String DEFAULT_MODULE = "-defaultModule";

    private ComponentDefinition defaultModuleDefinition;

    private String defaultModuleDefinitionName;

    private final Map<String, ComponentDefinition> definitionsCache = new HashMap<String, ComponentDefinition>();

    static private Map<String, String> forwardsUsingDefaultModule = new HashMap<String, String>();

    static public String createDefinition(String forward) {
	String defaultTileName = forward + DEFAULT_MODULE;
	if (!forwardsUsingDefaultModule.containsKey(defaultTileName)) {
	    forwardsUsingDefaultModule.put(defaultTileName, forward);
	}
	return defaultTileName;
    }

    @Override
    public void initFactory(ServletContext servletContext, Map properties) throws DefinitionsFactoryException {
	this.defaultModuleDefinitionName = (String) properties.get("defaultTileDefinition");
	super.initFactory(servletContext, properties);

    }

    @Override
    public ComponentDefinition getDefinition(String tileName, ServletRequest request, ServletContext servletContext)
	    throws NoSuchDefinitionException, DefinitionsFactoryException {

	if (forwardsUsingDefaultModule.containsKey(tileName)) {

	    // init default definition
	    if (defaultModuleDefinition == null) {
		if (defaultModuleDefinitionName != null) {
		    defaultModuleDefinition = super.getDefinition(defaultModuleDefinitionName, request, servletContext);
		} else {
		    return null;
		}
	    }

	    Set<String> processedTiles = (Set<String>) request.getAttribute("__processedTiles");
	    if (processedTiles == null) {
		processedTiles = new HashSet<String>();
		request.setAttribute("__processedTiles", processedTiles);
	    } else if (processedTiles.contains(tileName)) {
		return null;
	    }
	    processedTiles.add(tileName);

	    if (definitionsCache.containsKey(tileName)) {
		return definitionsCache.get(tileName);
	    }

	    ComponentDefinition componentDefinition = new ComponentDefinition(defaultModuleDefinition);
	    componentDefinition.putAttribute("body", forwardsUsingDefaultModule.get(tileName));
	    definitionsCache.put(tileName, componentDefinition);
	    return componentDefinition;
	}

	return super.getDefinition(tileName, request, servletContext);
    }
}
