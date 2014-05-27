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
package org.fenixedu.bennu.core.presentationTier.component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class OrganizationChart<T> extends ArrayList<OrganizationChartRow<T>> {

    final int elementRowIndex;

    final int unitsPerPart;

    public OrganizationChart(final Collection<T> elements, final int unitsPerPart) {
        this.unitsPerPart = unitsPerPart;
        addAllElements(elements);
        elementRowIndex = -1;
    }

    public OrganizationChart(final T element, final Collection<T> parents, final Collection<T> children, final int unitsPerPart) {
        this.unitsPerPart = unitsPerPart;
        if (parents != null && !parents.isEmpty()) {
            addAllElements(parents);
        }
        elementRowIndex = size();
        add(new OrganizationChartRow<T>(element, unitsPerPart));
        addAllElements(children);
    }

    public void addAllElements(final Collection<T> elements) {
        if (elements != null && !elements.isEmpty()) {
            OrganizationChartRow<T> row = null;
            for (final T t : elements) {
                if (row == null || row.isFull()) {
                    row = new OrganizationChartRow<T>(t, unitsPerPart);
                    add(row);
                } else {
                    row.add(t);
                }
            }
        }
    }

    public int getElementRowIndex() {
        return elementRowIndex;
    }

    public T getElement() {
        return elementRowIndex == -1 ? null : get(elementRowIndex).iterator().next();
    }

    public int getUnitsPerPart() {
        return unitsPerPart;
    }

}
