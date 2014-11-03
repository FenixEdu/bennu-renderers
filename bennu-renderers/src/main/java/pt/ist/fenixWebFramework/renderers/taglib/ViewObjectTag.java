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
package pt.ist.fenixWebFramework.renderers.taglib;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.state.ViewState;
import pt.ist.fenixWebFramework.renderers.contexts.OutputContext;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;

public class ViewObjectTag extends BaseRenderObjectTag {

    @Override
    protected HtmlComponent renderObject(PresentationContext context, Object object) throws JspException {
        if (getType() == null) {
            return RenderKit.getInstance().render(context, object);
        } else {
            try {
                Class type = Class.forName(getType());

                return RenderKit.getInstance().render(context, object, type);
            } catch (ClassNotFoundException e) {
                throw new JspException("could not get class named " + getType(), e);
            }
        }
    }

    @Override
    protected PresentationContext createPresentationContext(Object object, String layout, Schema schema, Properties properties) {
        OutputContext context = new OutputContext();

        context.setSchema(schema);
        context.setLayout(layout);
        context.setProperties(properties);

        MetaObject metaObject = MetaObjectFactory.createObject(object, schema);

        ViewState viewState = new ViewState(null);
        viewState.setMetaObject(metaObject);

        viewState.setInputDestination(getInputDestination());

        viewState.setLayout(getLayout());
        viewState.setProperties(getRenderProperties());
        viewState.setRequest((HttpServletRequest) pageContext.getRequest());

        setViewStateDestinations(viewState);

        context.setViewState(viewState);
        context.setMetaObject(metaObject);

        return context;
    }
}
