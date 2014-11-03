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
package pt.ist.fenixWebFramework.renderers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectCollection;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;

/**
 * This renderer can be used to edit a single object in a tabular view as if
 * you were editing a collection with only the given object.
 * <p>
 * If you edit a collection with this renderer then it will behave exactly as the
 * {@link pt.ist.fenixWebFramework.renderers.TabularInputRenderer}.
 * 
 * @author cfgi
 */
public class SingleObjectTabularInputRenderer extends TabularInputRenderer {

    @Override
    public HtmlComponent render(Object object, Class type) {
        if (object instanceof Collection) {
            return super.render(object, type);
        } else {
            List list = new ArrayList();
            list.add(object);

            MetaObjectCollection multipleMetaObject = new MetaObjectCollection();
            multipleMetaObject.add(getInputContext().getMetaObject());

            getInputContext().setMetaObject(multipleMetaObject);

            return super.render(list, list.getClass());
        }
    }
}
