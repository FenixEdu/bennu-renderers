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
package pt.ist.fenixWebFramework.renderers.contexts;

import java.util.Properties;

import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.utils.RenderMode;

public abstract class PresentationContext {

    private PresentationContext parentContext;

    private RenderMode renderMode;

    private Schema schema;

    private String layout;

    private Properties properties;

    private IViewState viewState;

    private MetaObject metaObject;

    public PresentationContext() {
        super();

        this.parentContext = null;
    }

    protected PresentationContext(PresentationContext parent) {
        super();

        this.parentContext = parent;
    }

    public RenderMode getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(RenderMode renderMode) {
        this.renderMode = renderMode;
    }

    public PresentationContext getParentContext() {
        return parentContext;
    }

    public void setParentContext(PresentationContext parentContext) {
        this.parentContext = parentContext;
    }

    public Schema getSchema() {
        return this.schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public boolean hasViewState() {
        return getViewState() != null;
    }

    public IViewState getViewState() {
        if (viewState != null) {
            return viewState;
        }

        if (this.parentContext != null) {
            return this.parentContext.getViewState();
        }

        return null;
    }

    public void setViewState(IViewState viewState) {
        this.viewState = viewState;

        if (viewState != null) {
            setMetaObject(viewState.getMetaObject());
        }
    }

    public MetaObject getMetaObject() {
        return this.metaObject;
    }

    public void setMetaObject(MetaObject metaObject) {
        this.metaObject = metaObject;
    }

    public abstract PresentationContext createSubContext(MetaObject metaObject);
}
