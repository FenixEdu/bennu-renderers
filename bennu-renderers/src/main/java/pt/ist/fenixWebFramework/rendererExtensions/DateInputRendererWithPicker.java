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

import pt.ist.fenixWebFramework.renderers.DateInputRenderer;

/**
 * This renderer provides a more fashionable way of doing the input of a date
 * than the plain DateInputRenderer. The date is accepted from a text input
 * field using a certain format, but there is an auxiliary javascript calendar
 * that pops out on the click of a small image butoon The format beeing accepted
 * is shown to the right of the textfield and before the calendar trigger
 * button.
 * 
 * <p>
 * Example: <input type="text" value="01/02/3456"/> dd/MM/yyyy <input type="button" value="Cal..." align="absmiddle" size="10"/>
 * 
 * @author José Pedro Pereira - Linkare TI
 * @author Paulo Abrantes
 */
public class DateInputRendererWithPicker extends DateInputRenderer {

}
