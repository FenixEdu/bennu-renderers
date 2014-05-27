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
/**
 * 
 */
package pt.ist.fenixWebFramework.renderers.converters;

import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class EnumArrayConverter extends Converter {

    private EnumConverter concreteConverter;

    public EnumArrayConverter() {
        this.concreteConverter = new EnumConverter();
    }

    public EnumArrayConverter(Class enumClass) {
        this.concreteConverter = new EnumConverter(enumClass);
    }

    @Override
    public Object convert(Class type, Object value) {
        List enumValues = new ArrayList();

        String[] values = (String[]) value;
        for (String enumString : values) {
            enumValues.add(this.concreteConverter.convert(type, enumString));
        }

        return enumValues;
    }

}