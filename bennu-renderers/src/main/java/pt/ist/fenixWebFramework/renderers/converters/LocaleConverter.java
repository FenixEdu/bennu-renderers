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
package pt.ist.fenixWebFramework.renderers.converters;

import java.util.Locale;
import java.util.Locale.Builder;

import pt.ist.fenixWebFramework.renderers.components.converters.BiDirectionalConverter;

public class LocaleConverter extends BiDirectionalConverter {
    @Override
    public String deserialize(Object object) {
        return ((Locale) object).toLanguageTag();
    }

    @Override
    public Object convert(Class type, Object value) {
        return new Builder().setLanguageTag((String) value).build();
    }
}
