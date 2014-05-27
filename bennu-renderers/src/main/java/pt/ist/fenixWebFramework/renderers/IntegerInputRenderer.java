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

import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.converters.IntegerNumberConverter;

/**
 * This renderer provides a simple way of doing the input of a integer number.
 * The number is read form a text input field and parsed with {@link Integer#parseInt(java.lang.String, int)} were the
 * second argument is the value given in the {@linkplain #setBase(int) base} property.
 * 
 * <p>
 * Example: <input type="text" value="12345"/>
 * 
 * @author cfgi
 */
public class IntegerInputRenderer extends NumberInputRenderer {

    private int base = 10;

    /**
     * The base in which the number should be interpreted. For instance,
     * if <tt>base</tt> is 16 then an input like <tt>CAFE</tt> will be
     * interpreted as 51966.
     * 
     * @property
     */
    public void setBase(int base) {
        this.base = base;
    }

    public int getBase() {
        return this.base;
    }

    @Override
    protected Converter getConverter() {
        return new IntegerNumberConverter(getBase());
    }

}
