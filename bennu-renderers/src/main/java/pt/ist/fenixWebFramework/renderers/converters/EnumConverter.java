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

import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class EnumConverter extends Converter {

    private Class enumClass;

    public EnumConverter() {
        super();
    }

    public EnumConverter(Class enumClass) {
        super();

        this.enumClass = enumClass;
    }

    @Override
    public Object convert(Class type, Object value) {
        Object[] enums;

        if (this.enumClass == null) {
            enums = type.getEnumConstants();
        } else {
            enums = this.enumClass.getEnumConstants();
            if (enums == null) {
                enums = this.enumClass.getEnclosingClass().getEnumConstants();
            }
        }

        for (Object enum1 : enums) {
            if (enum1.toString().equals(value)) {
                return enum1;
            }
        }

        return null;
    }

}