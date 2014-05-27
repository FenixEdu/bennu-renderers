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
package pt.ist.fenixWebFramework.renderers.components.state;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;

public class ActionViewState extends ViewState {

    private HtmlComponent component;

    public ActionViewState() {
        super();
    }

    public ActionViewState(String id) {
        super(id);
    }

    public ActionViewState(String id, Object object) {
        this(id, object, (Schema) null);
    }

    public ActionViewState(String id, Object object, Schema schema) {
        this(id);

        setMetaObject(MetaObjectFactory.createObject(object, schema));
    }

    public ActionViewState(String id, Object object, String schemaName) {
        this(id, object, RenderKit.getInstance().findSchema(schemaName));
    }

    @Override
    public HtmlComponent getComponent() {
        return this.component;
    }

    @Override
    public void setComponent(HtmlComponent component) {
        this.component = component;
    }

}
