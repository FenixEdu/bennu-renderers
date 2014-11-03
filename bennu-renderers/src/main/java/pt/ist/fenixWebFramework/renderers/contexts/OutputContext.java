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

import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.utils.RenderMode;

public final class OutputContext extends PresentationContext {

    public OutputContext() {
        super();

        setRenderMode(RenderMode.OUTPUT);
    }

    protected OutputContext(OutputContext parent) {
        super(parent);
    }

    @Override
    public OutputContext createSubContext(MetaObject metaObject) {
        OutputContext context = new OutputContext(this);

        //context.setLayout(getLayout());
        context.setMetaObject(metaObject);
        //context.setProperties(metaObject.getProperties());

        context.setRenderMode(getRenderMode());

        return context;
    }

}
