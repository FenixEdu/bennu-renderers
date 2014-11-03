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

/**
 * This renderer provides a simple way of editing objects. A table
 * is used to organize the presentation. Each slot will have it's
 * corresponding row. Each row has three columns. In the left column
 * the slot's label will be presented. In the middle column the
 * editor for the slot's value will be presented. In the rightmost
 * column validation errors are presented.
 * 
 * <p>
 * Example:
 * <table border="1">
 * <tr>
 * <th>Name</th>
 * <td><input type="text"/></td>
 * <td>An empty name is not valid.</td>
 * </tr>
 * <tr>
 * <th>Age</th>
 * <td><input type="text" value="20"/></td>
 * <td></td>
 * </tr>
 * <tr>
 * <th>Gender</th>
 * <td>
 * <select> <option>-- Please Select --</option> <option>Female</option> <option>Male</option> </select></td>
 * <td>You must select a gender.</td>
 * </tr>
 * </table>
 * 
 */
public class StandardInputBreakRenderer extends StandardInputRenderer {

}
