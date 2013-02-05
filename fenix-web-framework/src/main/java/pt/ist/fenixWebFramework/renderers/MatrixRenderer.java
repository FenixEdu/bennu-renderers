package pt.ist.fenixWebFramework.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell.CellType;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public class MatrixRenderer extends InputRenderer {

    private final List<CellConfiguration> cellConfigurations;

    public MatrixRenderer() {
        this.cellConfigurations = new ArrayList<CellConfiguration>();
    }

    private CellConfiguration getCellConfiguration(String slotId) {
        for (CellConfiguration cellConf : this.cellConfigurations) {
            if (cellConf.getId().equals(slotId)) {
                return cellConf;
            }
        }

        CellConfiguration cellConf = new CellConfiguration(slotId);
        this.cellConfigurations.add(cellConf);
        return cellConf;
    }

    public void setSlot(String id, String value) {
        getCellConfiguration(id).setSlotName(value);
    }

    public String getSlot(String id) {
        return getCellConfiguration(id).getSlotName();
    }

    public void setLabelHidden(String id, String value) {
        getCellConfiguration(id).setLabelHidden(Boolean.valueOf(value));
    }

    public String getLabelHidden(String id) {
        return getCellConfiguration(id).isLabelHidden().toString();
    }

    public void setColumn(String id, String value) {
        getCellConfiguration(id).setColumn(Integer.valueOf(value));
    }

    public String getColumn(String id) {
        return getCellConfiguration(id).getColumn().toString();
    }

    public void setRow(String id, String value) {
        getCellConfiguration(id).setRow(Integer.valueOf(value));
    }

    public String getRow(String id) {
        return getCellConfiguration(id).getRow().toString();
    }

    public void setColumnSpan(String id, String value) {
        getCellConfiguration(id).setColumnSpan(Integer.valueOf(value));
    }

    public String getColumnSpan(String id) {
        return getCellConfiguration(id).getColumnSpan().toString();
    }

    public void setRowSpan(String id, String value) {
        getCellConfiguration(id).setRowSpan(Integer.valueOf(value));
    }

    public String getRowSpan(String id) {
        return getCellConfiguration(id).getRowSpan().toString();
    }

    public void setLabelColumnSpan(String id, String value) {
        getCellConfiguration(id).setLabelColumnSpan(Integer.valueOf(value));
    }

    public String getLabelColumnSpan(String id) {
        return getCellConfiguration(id).getLabelColumnSpan().toString();
    }

    public void setLabelRowSpan(String id, String value) {
        getCellConfiguration(id).setLabelRowSpan(Integer.valueOf(value));
    }

    public String getLabelRowSpan(String id) {
        return getCellConfiguration(id).getLabelRowSpan().toString();
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new MatrixLayout(getContext().getMetaObject());
    }

    private static class CellConfiguration {
        private String id;
        private String slotName;
        private Boolean labelHidden;
        private Integer labelRowSpan;
        private Integer labelColumnSpan;
        private Integer rowSpan;
        private Integer columnSpan;
        private Integer column;
        private Integer row;

        public CellConfiguration(String id) {
            this.id = id;
            labelHidden = false;
            labelRowSpan = 1;
            labelColumnSpan = 1;
            rowSpan = 1;
            columnSpan = 1;
        }

        public Boolean isLabelHidden() {
            return labelHidden;
        }

        public void setLabelHidden(Boolean labelHidden) {
            this.labelHidden = labelHidden;
        }

        public Integer getLabelRowSpan() {
            return labelRowSpan;
        }

        public void setLabelRowSpan(Integer labelRowSpan) {
            this.labelRowSpan = labelRowSpan;
        }

        public Integer getLabelColumnSpan() {
            return labelColumnSpan;
        }

        public void setLabelColumnSpan(Integer labelColumnSpan) {
            this.labelColumnSpan = labelColumnSpan;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSlotName() {
            return slotName;
        }

        public void setSlotName(String slotName) {
            this.slotName = slotName;
        }

        public Integer getRowSpan() {
            return rowSpan;
        }

        public void setRowSpan(Integer rowSpan) {
            this.rowSpan = rowSpan;
        }

        public Integer getColumnSpan() {
            return columnSpan;
        }

        public void setColumnSpan(Integer columnSpan) {
            this.columnSpan = columnSpan;
        }

        public Integer getColumn() {
            return column;
        }

        public void setColumn(Integer column) {
            this.column = column;
        }

        public Integer getRow() {
            return row;
        }

        public void setRow(Integer row) {
            this.row = row;
        }

    }

    // TODO: This should inherit from TabularLayout
    public class MatrixLayout extends Layout {

        private final MetaObject metaObject;
        private Map<Integer, List<CellConfiguration>> matrix;

        private int maxColumns;
        private int maxRows;

        public MatrixLayout(Object object) {
            this.metaObject = (MetaObject) object;
            this.matrix = new HashMap<Integer, List<CellConfiguration>>();
            this.maxColumns = -1;
            this.maxRows = -1;
        }

        public Map<Integer, List<CellConfiguration>> getMatrix() {
            if (matrix == null) {
                createMatrix();
            }
            return this.matrix;
        }

        private void createMatrix() {
            this.matrix = new HashMap<Integer, List<CellConfiguration>>();
            for (CellConfiguration configuration : MatrixRenderer.this.cellConfigurations) {
                Integer row = configuration.getRow();
                List<CellConfiguration> cells = this.matrix.get(row);
                if (cells == null) {
                    cells = new ArrayList<CellConfiguration>();
                    this.matrix.put(row, cells);
                }
                cells.add(configuration);
                setMaxLinesAndRows(configuration);
            }
        }

        private void setMaxLinesAndRows(CellConfiguration configuration) {
            maxColumns = Math.max(maxColumns, configuration.getColumn());
            maxRows = Math.max(maxRows, configuration.getRow());
        }

        protected CellConfiguration getCell(int rowIndex, int columnIndex) {
            for (CellConfiguration configuration : getMatrix().get(rowIndex)) {
                if (configuration.getColumn().equals(columnIndex)) {
                    return configuration;
                }
            }
            return null;
        }

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            HtmlTable table = new HtmlTable();
            createMatrix();
            for (int row = 0; row <= this.maxRows; row++) {
                HtmlTableRow tableRow = table.createRow();
                for (int column = 0; column <= this.maxColumns; column++) {
                    CellConfiguration configuration = getCell(row, column);
                    if (configuration != null) {
                        MetaSlot slot = this.metaObject.getSlot(configuration.getSlotName());
                        if (!configuration.isLabelHidden()) {
                            HtmlTableCell headerCell = tableRow.createCell(CellType.HEADER);
                            headerCell.setRowspan(configuration.getLabelRowSpan());
                            headerCell.setColspan(configuration.getLabelColumnSpan());
                            headerCell.setBody(new HtmlText(slot.getLabel()));
                        }
                        HtmlTableCell componentCell = tableRow.createCell(CellType.DATA);
                        componentCell.setBody(renderSlot(slot));
                        componentCell.setRowspan(configuration.getRowSpan());
                        componentCell.setColspan(configuration.getColumnSpan());
                    }
                }

            }
            return table;
        }

    }
}
