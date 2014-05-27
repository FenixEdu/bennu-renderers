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

import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

public class DomainObjectKeyConverter extends Converter {

    @Override
    public Object convert(Class type, Object value) {

        if (value == null || value.equals("")) {
            return null;
        }
        int index = ((String) value).indexOf(":");

        final String key = index > 0 ? ((String) value).substring(index + 1) : (String) value;
        try {
            return FenixFramework.getDomainObject(key);
        } catch (NumberFormatException e) {
            throw new ConversionException("invalid oid in key: " + key, e);
        }
    }

    public static String code(final DomainObject object) {
        return object.getExternalId();
    }

}
