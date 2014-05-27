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

import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

/**
 * This renderer provides a simple way of doing the input of a short number.
 * The number is read form a text input field and parsed with {@link Short#parseShort(java.lang.String, int)} were the
 * second argument is the value given in the {@linkplain IntegerInputRenderer#setBase(int) base} property.
 * 
 * <p>
 * Example: <input type="text" value="12345"/>
 * 
 * @author cfgi
 */
public class ShortInputRenderer extends IntegerInputRenderer {
    @Override
    protected Converter getConverter() {
        return new ShortNumberConverter(getBase());
    }

    private class ShortNumberConverter extends Converter {

        private int base;

        public ShortNumberConverter(int base) {
            this.base = base;
        }

        public int getBase() {
            return this.base;
        }

        public void setBase(int base) {
            this.base = base;
        }

        @Override
        public Object convert(Class type, Object value) {
            String numberText = ((String) value).trim();

            if (numberText.length() == 0) {
                return null;
            }

            try {
                return Short.parseShort(numberText.trim(), getBase());
            } catch (NumberFormatException e) {
                throw new ConversionException("renderers.converter.short", e, true, value);
            }
        }

    }
}
