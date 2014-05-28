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
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.contexts.InputContext;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;

public class EditViewStateTag extends EditObjectTag {

    @Override
    public int doEndTag() throws JspException {
        IViewState viewState = (IViewState) getTargetObject();

        if (viewState.getId() != null) {
            setId(viewState.getId());
        } else {
            setId(null);
        }

        return super.doEndTag();
    }

    @Override
    protected Object getTargetObject() throws JspException {
        if (!isPostBack()) {
            return super.getTargetObject();
        } else {
            return getViewState();
        }
    }

    @Override
    protected PresentationContext createPresentationContext(Object object, String layout, Schema schema, Properties properties) {
        IViewState viewState = (IViewState) object;

        viewState.setRequest((HttpServletRequest) pageContext.getRequest());
        setViewStateDestinations(viewState);

        InputContext inputContext = new InputContext();

        inputContext.setLayout(layout);
        inputContext.setSchema(schema);
        inputContext.setProperties(properties);

        inputContext.setViewState(viewState);

        return inputContext;
    }

    @Override
    protected HtmlComponent renderObject(PresentationContext context, Object object) throws JspException {
        return ((IViewState) object).getComponent();
    }

}
