package pt.ist.fenixWebFramework.renderers;

import java.util.Collection;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.layouts.TabularLayout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

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

        private MetaObject metaObject;
        private List<MetaSlot> slots;
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
                    || (object instanceof Collection && ((Collection) object).size() == 0) || (object instanceof MultiLanguageString && !validMultiLanguage((MultiLanguageString) object)));
        }

        private boolean validMultiLanguage(MultiLanguageString multiLanguageString) {
            for (String content : multiLanguageString.getAllContents()) {
                if (content.trim().length() > 0) {
                    return true;
                }
            }
            return false;
        }

    }

}
