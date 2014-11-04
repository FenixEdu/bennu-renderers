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

import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixWebFramework.renderers.components.HtmlForm;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.components.state.ViewStateWrapper;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectKey;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.utils.RenderMode;

public final class InputContext extends PresentationContext {

    private HtmlForm form;
    private boolean toolkitRequired = false;

    public InputContext() {
        super();

        setRenderMode(RenderMode.INPUT);
    }

    protected InputContext(InputContext parent) {
        super(parent);
    }

    @Override
    public IViewState getViewState() {
        IViewState viewState = super.getViewState();

        if (getMetaObject() instanceof MetaSlot) {
            MetaObjectKey key = getMetaObject().getKey();

            if (key != null) {
                String prefix = key.toString();

                if (viewState instanceof ViewStateWrapper) {
                    ViewStateWrapper wrapper = (ViewStateWrapper) viewState;

                    if (prefix.equals(wrapper.getPrefix())) {
                        return wrapper;
                    } else {
                        return new ViewStateWrapper(viewState, prefix);
                    }
                } else {
                    return new ViewStateWrapper(viewState, prefix);
                }
            } else {
                return viewState;
            }
        } else {
            return viewState;
        }
    }

    protected User getUser() {
        return getViewState().getUser();
    }

    public HtmlForm getForm() {
        if (getParentContext() == null || !(getParentContext() instanceof InputContext)) {
            if (this.form == null) {
                this.form = new HtmlForm();
            }

            return this.form;
        } else {
            return ((InputContext) getParentContext()).getForm();
        }
    }

    @Override
    public InputContext createSubContext(MetaObject metaObject) {
        InputContext context = new InputContext(this);

        // TODO: check this and compare with the version in OutputContext
        context.setLayout(getLayout());
        context.setMetaObject(metaObject);
        context.setProperties(metaObject.getProperties());

        context.setRenderMode(getRenderMode());

        return context;
    }

    public void requireToolkit() {
        if (getParentContext() == null || !(getParentContext() instanceof InputContext)) {
            this.toolkitRequired = true;
        } else {
            ((InputContext) getParentContext()).requireToolkit();
        }
    }

    public boolean isToolkitRequired() {
        return toolkitRequired;
    }
}
