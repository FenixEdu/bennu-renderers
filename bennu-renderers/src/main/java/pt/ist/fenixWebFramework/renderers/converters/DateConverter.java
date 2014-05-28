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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class DateConverter extends Converter {
    public static final String DEFAULT_FORMAT = "dd/MM/yyyy";

    private DateFormat format;

    public DateConverter() {
        this.format = new SimpleDateFormat(DEFAULT_FORMAT);
    }

    public DateConverter(DateFormat format) {
        this.format = format;
    }

    @Override
    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        }

        String text = ((String) value).trim();

        if (text.length() == 0) {
            return null;
        }

        try {
            return format.parse(text);
        } catch (ParseException e) {
            throw new ConversionException("renderers.converter.date", e, true, value);
        }
    }
}