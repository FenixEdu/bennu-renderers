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
package pt.ist.fenixWebFramework.rendererExtensions;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.LocalDate;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class LocalDateInputRendererWithPicker extends DateInputRendererWithPicker {

    public LocalDateInputRendererWithPicker() {
        super();

    }

    @Override
    public HtmlComponent render(Object object, Class type) {
        Date date = object == null ? null : ((LocalDate) object).toDateMidnight().toDate();
        return super.render(date, Date.class);
    }

    @Override
    protected Converter getDateConverter(SimpleDateFormat dateFormat) {
        final Converter dateConverter = super.getDateConverter(dateFormat);

        return new Converter() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object convert(Class type, Object value) {
                Date date = (Date) dateConverter.convert(type, value);

                return date == null ? null : new LocalDate(date);
            }

        };
    }

}
