package pt.ist.fenixWebFramework.renderers;

import org.apache.log4j.Logger;

import pt.ist.fenixWebFramework._development.LogLevel;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlLabel;
import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableHeader;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.Validatable;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

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

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new ObjectInputTabularBreakLayout(getContext().getMetaObject());
    }

    class ObjectInputTabularBreakLayout extends ObjectInputTabularLayout {

        public Logger logger = Logger.getLogger(ObjectInputTabularLayout.class);

        public ObjectInputTabularBreakLayout(MetaObject object) {
            super(object);
        }

        @Override
        protected int getNumberOfColumns() {
            return 2;
        }

        @Override
        protected int getNumberOfRows() {
            return this.object.getSlots().size() * 2;
        }

        @Override
        public HtmlComponent createComponent(Object object, Class type) {

            int rowNumber = getNumberOfRows();
            int columnNumber = getNumberOfColumns();

            HtmlTable table = new HtmlTable();
            setTable(table);
            if (hasHeader()) {
                HtmlTableHeader header = table.createHeader();

                HtmlTableRow firstRow = header.createRow();
                HtmlTableRow secondRow = (hasHeaderGroups() ? header.createRow() : null);

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
                    if (rowIndex % 2 == 0) {
                        HtmlTableCell cell = row.createCell();
                        cell.setType(HtmlTableCell.CellType.HEADER);
                        costumizeCell(cell, rowIndex, 0);
                        cell.setBody(getComponent(rowIndex, 0));
                    } else {
                        for (int columnIndex = 0; columnIndex < columnNumber; columnIndex++) {
                            HtmlTableCell cell = row.createCell();
                            costumizeCell(cell, rowIndex, columnIndex);
                            cell.setBody(getComponent(rowIndex, columnIndex));
                        }
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

        @Override
        protected HtmlComponent getComponent(int rowIndex, int columnIndex) {
            HtmlComponent component = null;
            int valueIndex = (rowIndex - 1) / 2;

            switch (rowIndex % 2) {
            case 0: // even value: label
                if (isDisplayLabel()) {
                    MetaSlot slot = this.object.getSlots().get(rowIndex / 2);
                    if (slot.isReadOnly()) {
                        component = new HtmlText(addLabelTerminator(slot.getLabel()), false);
                    } else {
                        HtmlLabel label = new HtmlLabel();
                        label.setFor(slot.getKey().toString());
                        label.setText(addLabelTerminator(slot.getLabel()));
                        component = label;
                    }
                }
                break;

            case 1: // odd value: slot
                if (columnIndex == 0) {
                    MetaSlot slot = this.object.getSlots().get(valueIndex);
                    component = renderSlot(slot);
                    if (!slot.isReadOnly()) {
                        Validatable validatable = findValidatableComponent(component);
                        if (validatable != null) {
                            HtmlFormComponent formComponent = (HtmlFormComponent) validatable;
                            if (formComponent.getId() == null) {
                                formComponent.setId(slot.getKey().toString());
                            }

                            inputComponents.put(valueIndex, validatable);
                        }
                    }
                } else {
                    if (isHideValidators()) {
                        component = new HtmlText();
                    } else {
                        Validatable inputComponent = inputComponents.get(valueIndex);

                        if (inputComponent != null) {
                            component = getChainValidator(inputComponent, this.object.getSlots().get(valueIndex));

                            if (component != null) {
                                component.setClasses(getValidatorClasses());
                            } else {
                                component = new HtmlText();
                            }
                        } else {
                            component = new HtmlText();
                        }
                    }
                }
                break;

            default:
                component = new HtmlText();
                break;
            }

            return component;
        }

    }

}
