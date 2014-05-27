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

import java.util.Collection;

import pt.ist.fenixWebFramework.renderers.MenuOptionListRenderer;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

/**
 * Fenix extension to the {@link pt.ist.fenixWebFramework.renderers.MenuOptionListRenderer}.
 * 
 * {@inheritDoc}
 * 
 * @author cfgi
 */
public class InputMenuOptionListRenderer extends MenuOptionListRenderer {
    private String filterClass;

    public String getFilterClass() {
        return this.filterClass;
    }

    /**
     * This property allows you to indicate a {@linkplain DataFilter data
     * filter} that will remove values, from the collection returned by data
     * provider, not valid in a specific context.
     * 
     * @property
     */
    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    // HACK: duplicated code, id=inputChoices.selectPossibilitiesAndConverter
    @Override
    protected Converter getConverter() {
        return super.getConverter();
    }

    // HACK: duplicated code, id=inputChoices.selectPossibilitiesAndConverter
    @Override
    protected Collection getPossibleObjects() {
        return super.getPossibleObjects();
    }

}
