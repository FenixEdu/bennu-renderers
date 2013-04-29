package pt.ist.fenixWebFramework.renderers.layouts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixWebFramework._development.LogLevel;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableHeader;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;

public abstract class TabularLayout extends Layout {
    protected static Logger logger = LoggerFactory.getLogger(TabularLayout.class);

    private String caption;
    private String summary;
    private String rowClasses;
    private String columnClasses;
    private String headerClasses;

    private HtmlTable table;

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getColumnClasses() {
        return this.columnClasses;
    }

    public void setColumnClasses(String columnClasses) {
        this.columnClasses = columnClasses;
    }

    public String getHeaderClasses() {
        return this.headerClasses;
    }

    public void setHeaderClasses(String headerClasses) {
        this.headerClasses = headerClasses;
    }

    public String getRowClasses() {
        return this.rowClasses;
    }

    public void setRowClasses(String rowClasses) {
        this.rowClasses = rowClasses;
    }

    public HtmlTable getTable() {
        return this.table;
    }

    public void setTable(HtmlTable table) {
        this.table = table;
    }

    @Override
    public String[] getPropertyNames() {
        return mergePropertyNames(super.getPropertyNames(), new String[] { "caption", "summary", "rowClasses", "columnClasses",
                "headerClasses" });
    }

    @Override
    public HtmlComponent createComponent(Object object, Class type) {
        HtmlTable table = new HtmlTable();
        setTable(table);

        int rowNumber = getNumberOfRows();
        int columnNumber = getNumberOfColumns();

        if (hasHeader()) {
            HtmlTableHeader header = table.createHeader();

            HtmlTableRow firstRow = header.createRow();
            HtmlTableRow secondRow = null;

            if (hasHeaderGroups()) {
                secondRow = header.createRow();
            }

            String lastGroup = null;
            HtmlTableCell lastGroupCell = null;

            for (int columnIndex = 0; columnIndex < columnNumber; columnIndex++) {
                String group = getHeaderGroup(columnIndex);

                if (hasHeaderGroups() && group != null) {
                    if (lastGroup != null && lastGroup.equals(group)) {
                        if (lastGroupCell.getColspan() == null) {
                            lastGroupCell.setColspan(2);
                        } else {
                            lastGroupCell.setColspan(lastGroupCell.getColspan() + 1);
                        }
                    } else {
                        HtmlTableCell cell = firstRow.createCell();
                        cell.setBody(new HtmlText(group));

                        lastGroup = group;
                        lastGroupCell = cell;
                    }

                    HtmlTableCell cell = secondRow.createCell();
                    cell.setBody(getHeaderComponent(columnIndex));
                } else {
                    lastGroup = null;
                    lastGroupCell = null;

                    HtmlTableCell cell = firstRow.createCell();
                    cell.setBody(getHeaderComponent(columnIndex));

                    if (hasHeaderGroups()) {
                        cell.setRowspan(2);
                    }
                }
            }
        }

        for (int rowIndex = 0; rowIndex < rowNumber; rowIndex++) {
            HtmlTableRow row = table.createRow();

            try {
                for (int columnIndex = 0; columnIndex < columnNumber; columnIndex++) {
                    HtmlTableCell cell = row.createCell();

                    if (isHeader(rowIndex, columnIndex)) {
                        cell.setType(HtmlTableCell.CellType.HEADER);
                    }

                    costumizeCell(cell, rowIndex, columnIndex);
                    if (cell.getColspan() != null) {
                        columnIndex += cell.getColspan() - 1;
                    }
                    if (getCellClasses(rowIndex, columnIndex) != null) {
                        cell.addClass(getCellClasses(rowIndex, columnIndex));
                    }

                    cell.setBody(getComponent(rowIndex, columnIndex));
                }
            } catch (Exception e) {
                if (LogLevel.WARN) {
                    logger.warn("while generating table row " + rowIndex + " catched exception " + e);
                }
                e.printStackTrace();
                table.removeRow(row);
            }
        }

        return table;
    }

    protected boolean hasHeader() {
        return false;
    }

    protected boolean hasHeaderGroups() {
        return false;
    }

    protected abstract HtmlComponent getHeaderComponent(int columnIndex);

    protected String getHeaderGroup(int columnIndex) {
        return null;
    }

    protected void costumizeCell(HtmlTableCell cell, int rowIndex, int columnIndex) {

    }

    protected abstract int getNumberOfColumns();

    protected abstract int getNumberOfRows();

    protected abstract HtmlComponent getComponent(int rowIndex, int columnIndex);

    protected String getCellClasses(int rowIndex, int columnIndex) {
        return null;
    }

    protected boolean isHeader(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void applyStyle(HtmlComponent component) {
        super.applyStyle(component);

        HtmlTable table = (HtmlTable) component;

        table.setCaption(getCaption());
        table.setSummary(getSummary());

        // header
        if (getHeaderClasses() != null) {
            // decompose header cell classes
            String[] headerClasses = null;
            if (getHeaderClasses() != null) {
                headerClasses = getHeaderClasses().split(",", -1);
            }

            HtmlTableHeader header = table.getHeader();
            if (header != null) {
                for (HtmlTableRow row : header.getRows()) {
                    int cellIndex = 0;
                    for (HtmlTableCell cell : row.getCells()) {
                        String choosenCellClass = headerClasses[cellIndex % headerClasses.length];
                        cell.setClasses(choosenCellClass);

                        cellIndex++;
                    }
                }
            }
        }

        // decompose row and cell classes
        String[] rowClasses = null;
        if (getRowClasses() != null) {
            rowClasses = getRowClasses().split(",", -1);
        }

        String[] cellClasses = null;
        if (getColumnClasses() != null) {
            cellClasses = getColumnClasses().split(",", -1);
        }

        // check if additional styling is needed
        if (rowClasses == null && cellClasses == null) {
            return;
        }

        // apply style by rows and columns
        int rowIndex = 0;
        for (HtmlTableRow row : table.getRows()) {
            if (rowClasses != null) {
                String chooseRowClass = rowClasses[rowIndex % rowClasses.length];
                if (!chooseRowClass.equals("")) {
                    row.setClasses(chooseRowClass);
                }
            }

            if (cellClasses != null) {
                int cellIndex = 0;
                for (HtmlTableCell cell : row.getCells()) {
                    String chooseCellClass = cellClasses[cellIndex % cellClasses.length];
                    if (!chooseCellClass.equals("")) {
                        cell.addClass(chooseCellClass);
                    }

                    cellIndex++;
                }
            }

            rowIndex++;
        }
    }

}