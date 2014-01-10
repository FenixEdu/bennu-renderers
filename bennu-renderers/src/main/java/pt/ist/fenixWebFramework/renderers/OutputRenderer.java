package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.contexts.OutputContext;

/**
 * The base renderer for every output renderer.
 * 
 * @author cfgi
 */
public abstract class OutputRenderer extends Renderer {

    public OutputContext getOutputContext() {
        return (OutputContext) getContext();
    }
}
