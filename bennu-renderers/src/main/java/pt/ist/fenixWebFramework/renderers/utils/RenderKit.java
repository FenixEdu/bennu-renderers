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

import pt.ist.fenixWebFramework.renderers.Renderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.exceptions.NoRendererException;
import pt.ist.fenixWebFramework.renderers.exceptions.NoSuchSchemaException;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;

/**
 * Entry point for the rendering mechanism. The render kit allows several renderers and schemas to
 * be registered. The application can retrieve schemas to create suitable meta objects and prepare
 * a presentation context and then start the rendering mechanism with, for example, an invokation
 * to {@link #render(PresentationContext, Object, Class)}.
 * 
 * @author cfgi
 */
public class RenderKit {

    private static RenderKit instance = new RenderKit();

    private final RendererRegistry inputRenderers = new RendererRegistry();
    private final RendererRegistry outputRenderers = new RendererRegistry();

    private final Map<String, Schema> schemaRegistry = new HashMap<>();

    //
    // construct
    //

    private RenderKit() {
    }

    /**
     * Used to allow reloading of the configuration in runtime All registered schemas
     * and renderers are lost.
     */
    public static void reset() {
        RenderKit.instance = new RenderKit();
    }

    /**
     * Retrieves the default instance of the render kit.
     */
    public static RenderKit getInstance() {
        return instance;
    }

    //
    // register renderer and schema
    //

    private final RendererRegistry registry(RenderMode mode) {
        switch (mode) {
        case INPUT:
            return inputRenderers;
        case OUTPUT:
            return outputRenderers;
        default:
            throw new IllegalArgumentException("Could not find registry for mode: " + mode);
        }
    }

    /**
     * Registers a new renderer for the mode type and layout specified. Every instance of this renderer will be pre-configured
     * with
     * the properties given so the work as the default properties for the renderer.
     * <p>
     * An instace of the renderer can be retrieved with {@link #getRenderer(RenderMode, Class, String)}.
     */
    public void registerRenderer(RenderMode mode, Class<?> type, String layout, Class<? extends Renderer> renderer,
            Properties defaultProperties) {
        registry(mode).registerRenderer(type, layout, renderer, defaultProperties);
    }

    /**
     * Registers a new schema. The scheme can be searched by name with {@link #findSchema(String)}.
     */
    public void registerSchema(Schema schema) {
        schemaRegistry.put(schema.getName(), schema);
    }

    //
    // find
    // 

    /**
     * Finds a registered schema by name. If the name is null then this method returns null. In all
     * other cases returns the schema with the given name or throws an exception if the schema
     * is not registered.
     * 
     * @exception NoSuchSchemaException if the schema named <tt>schemaName</tt> could not be found
     */
    public Schema findSchema(String schemaName) {
        if (schemaName == null) {
            return null;
        }

        if (!schemaRegistry.containsKey(schemaName)) {
            throw new NoSuchSchemaException(schemaName);
        }

        return schemaRegistry.get(schemaName);
    }

    public boolean hasSchema(String schemaName) {
        return schemaRegistry.containsKey(schemaName);
    }

    /**
     * @exception NoRendererException if no renderer description could be found
     */
    public RendererDescription getExactRendererDescription(RenderMode mode, Class type, String layout) {
        return registry(mode).getExactRenderDescription(type, layout);
    }

    /**
     * @exception NoRendererException if no renderer description could be found
     */
    public RendererDescription getRendererDescription(RenderMode mode, Class type, String layout) {
        return registry(mode).getRenderDescription(type, layout);
    }

    /**
     * @exception NoRendererException if no specific renderer description could be found
     */
    private RendererDescription getSpecificRendererDescription(RenderMode mode, Class type, String layout) {
        try {
            return getRendererDescription(mode, type, layout);
        } catch (NoRendererException e) {
            return getRendererDescription(mode, type, null);
        }
    }

    /**
     * Allows to retrieve a new instance of a renderer previously registered for the given mode, type, and
     * layout.
     * 
     * @return a new instance of the renderer pre-configured with the default properties
     */
    public Renderer getRenderer(RenderMode mode, Class type, String layout) {
        RendererDescription rendererDescription = getSpecificRendererDescription(mode, type, layout);
        return rendererDescription.createRenderer();
    }

    /**
     * This method is a convenience method. It works in a similar way as {@link #getRenderer(RenderMode, Class, String)} but the
     * render mode is retrieved from the presentation context.
     */
    public Renderer getRenderer(PresentationContext context, Class type, String layout) {
        return getRenderer(context.getRenderMode(), type, layout);
    }

    //
    // render
    //

    private void prepareRenderer(Renderer renderer, PresentationContext context) {
        renderer.setContext(context);

        Properties properties = context.getProperties();

        if (properties != null) {
            RenderUtils.setProperties(renderer, properties);
        }
    }

    /**
     * Renders the object given as <code>targetObject</code>.
     * 
     * @exception NullPointerException if the targetObject is <code>null</code>
     * @exception NoRendererException if no suitable renderer could be found to present the <code>object</code> with the given
     *                context
     */
    public HtmlComponent render(PresentationContext context, Object object) {
        return render(context, object, object.getClass());
    }

    /**
     * Usefull for presenting the <code>null</code> value of a specific type or to present an
     * object as one of it's superclasses.
     */
    public HtmlComponent render(PresentationContext context, Object object, Class type) {
        Renderer renderer = getRenderer(context, type, context.getLayout());

        return renderUsing(renderer, context, object, type);
    }

    /**
     * Uses the given renderer to render the object. That is setups the renderer and invokes
     * {@link Renderer#render(Object, Class)}.
     */
    public HtmlComponent renderUsing(Renderer renderer, PresentationContext context, Object object, Class type) {
        prepareRenderer(renderer, context);

        return renderer.render(object, type);
    }
}
