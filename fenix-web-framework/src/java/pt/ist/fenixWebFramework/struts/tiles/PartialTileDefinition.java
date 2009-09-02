package pt.ist.fenixWebFramework.struts.tiles;

import org.apache.struts.tiles.ComponentDefinition;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;

public class PartialTileDefinition {

    private static final String DEFAULT_EXTEND = "defaultExtend";

    private final String path;
    private final String extend;

    public PartialTileDefinition(Forward forward, Forwards forwards) {
	this.path = forward.path();
	this.extend = (forward.extend().isEmpty()) ? forwards.extend() : forward.extend();
    }

    public PartialTileDefinition(String path) {
	this.path = path;
	this.extend = "";
    }

    public String getPath() {
	return path;
    }

    public String getExtend() {
	return extend;
    }

    public boolean hasExtend() {
	return !((extend == null) || (extend.isEmpty()));
    }

    public String getName() {
	if (hasExtend()) {
	    return path + "+" + extend;
	} else {
	    return path + "-" + DEFAULT_EXTEND;
	}

    }

    public void updateComponentDefinition(ComponentDefinition componentDefinition) {
	componentDefinition.putAttribute("body", path);
    }
}
