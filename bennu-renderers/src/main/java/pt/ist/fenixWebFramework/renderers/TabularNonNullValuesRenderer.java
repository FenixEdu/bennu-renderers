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

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.layouts.TabularLayout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public class TabularNonNullValuesRenderer extends OutputRenderer {

    private static int numberOfColumns = 2;

    private String label;
    private String schema;
    private String columnClasses;
    private String rowClasses;

    public String getRowClasses() {
        return rowClasses;
    }

    public void setRowClasses(String rowClasses) {
        this.rowClasses = rowClasses;
    }

    public String getColumnClasses() {
        return columnClasses;
    }

    public void setColumnClasses(String columnClasses) {
        this.columnClasses = columnClasses;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new TabularNonNullValuesLayout();
    }

    protected class TabularNonNullValuesLayout extends TabularLayout {

        private final MetaObject metaObject;
        private final List<MetaSlot> slots;
        private int indexSkipped = 0;

        public TabularNonNullValuesLayout() {
            this.metaObject = getContext().getMetaObject();
            this.slots = metaObject.getSlots();
        }

        @Override
        protected boolean isHeader(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        protected HtmlComponent getComponent(int rowIndex, int columnIndex) {
            if (!renderRowIndex(rowIndex + indexSkipped)) {
                indexSkipped++;
                return getComponent(rowIndex, columnIndex);
            } else {
                return (columnIndex == 0) ? new HtmlText(addLabel(slots.get(rowIndex + indexSkipped).getLabel()), false) : renderSlot(this.metaObject
                        .getSlots().get(rowIndex + indexSkipped));
            }

        }

        @Override
        protected HtmlComponent getHeaderComponent(int columnIndex) {
            return new HtmlText();
        }

        @Override
        protected int getNumberOfColumns() {
            return numberOfColumns;
        }

        @Override
        protected int getNumberOfRows() {
            int numberOfRows = 0;
            for (MetaSlot slot : metaObject.getSlots()) {
                if (isValidObject(slot.getObject())) {
                    numberOfRows++;
                }
            }
            return numberOfRows;
        }

        private boolean renderRowIndex(int rowIndex) {
            return isValidObject(this.metaObject.getSlots().get(rowIndex).getObject());
        }

        private String addLabel(String name) {
            return (getLabel() == null) ? name + ":" : name + getLabel();
        }

        private boolean isValidObject(Object object) {
            return !(object == null || (object instanceof String && ((String) object).length() == 0)
                    || (object instanceof Collection && ((Collection) object).size() == 0) || (object instanceof LocalizedString && !validMultiLanguage((LocalizedString) object)));
        }

        private boolean validMultiLanguage(LocalizedString localizedString) {
            for (Locale locale : localizedString.getLocales()) {
                if (localizedString.getContent(locale).trim().length() > 0) {
                    return true;
                }
            }
            return false;
        }

    }

}
