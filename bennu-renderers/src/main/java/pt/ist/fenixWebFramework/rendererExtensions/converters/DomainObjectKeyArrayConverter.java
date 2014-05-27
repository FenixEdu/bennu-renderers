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
package pt.ist.fenixWebFramework.rendererExtensions.converters;

import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixframework.DomainObject;

public class DomainObjectKeyArrayConverter extends Converter {

    @Override
    public Object convert(Class type, Object value) {
        DomainObjectKeyConverter converter = new DomainObjectKeyConverter();
        List<DomainObject> result = new ArrayList<DomainObject>();

        String[] values = (String[]) value;
        for (String key : values) {
            result.add((DomainObject) converter.convert(type, key));
        }

        return result;
    }

}
