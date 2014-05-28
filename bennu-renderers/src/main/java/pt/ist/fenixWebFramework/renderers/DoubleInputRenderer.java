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
 * {@inheritDoc}
 * 
 * This renderer converts the value to a float with {@link Double#parseDouble(java.lang.String)}.
 * 
 * @author cfgi
 */
public class DoubleInputRenderer extends NumberInputRenderer {

    @Override
    protected Converter getConverter() {
        return new DoubleNumberConverter();
    }

    private class DoubleNumberConverter extends Converter {

        @Override
        public Object convert(Class type, Object value) {
            String numberText = ((String) value).trim();

            if (numberText.length() == 0) {
                return null;
            }

            try {
                return Double.parseDouble(numberText);
            } catch (NumberFormatException e) {
                throw new ConversionException("renderers.converter.double", e, true, value);
            }
        }
    }
}
