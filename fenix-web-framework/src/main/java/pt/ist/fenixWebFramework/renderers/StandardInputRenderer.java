package pt.ist.fenixWebFramework.renderers;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlImage;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlLabel;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.Validatable;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.layouts.TabularLayout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;

/**
 * This renderer provides a simple way of editing objects. A table is used to
 * organize the presentation. Each slot will have it's corresponding row. Each
 * row has three columns. In the left column the slot's label will be presented.
 * In the middle column the editor for the slot's value will be presented. In
 * the rightmost column validation errors are presented.
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
 * <td><select> <option>-- Please Select --</option> <option>Female</option> <option>Male</option> </select></td>
 * <td>You must select a gender.</td>
 * </tr>
 * </table>
 * 
 * @author cfgi
 */
public class StandardInputRenderer extends InputRenderer {
    private String rowClasses;

    private String columnClasses;

    private String validatorClasses;

    private boolean hideValidators;

    private String labelTerminator;

    private boolean displayLabel = Boolean.TRUE;

    private boolean requiredMarkShown = FenixWebFramework.getConfig().getRequiredMarkShown();

    private boolean requiredMessageShown = true;

    private boolean optionalMarkShown = false;

    private String helpNoJavascriptClasses;

    private String helpClosedClasses;

    private String helpOpenClasses;

    private String helpTextClasses;

    private String helpImageIcon;

    public boolean isDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(boolean displayLabel) {
        this.displayLabel = displayLabel;
    }

    public StandardInputRenderer() {
        super();

        this.hideValidators = false;
    }

    public String getColumnClasses() {
        return columnClasses;
    }

    /**
     * The classes to be used in each column of the generated table. See {@link CollectionRenderer#setColumnClasses(String)} for
     * more details.
     * Remember that the first column contains labels, the second column slot's
     * editors, and the third the validator messages.
     * 
     * @property
     */
    public void setColumnClasses(String columnClasses) {
        this.columnClasses = columnClasses;
    }

    public String getRowClasses() {
        return rowClasses;
    }

    /**
     * The classes to be used in each row of the table. See {@link CollectionRenderer#setRowClasses(String)} for more details.
     * 
     * @property
     */
    public void setRowClasses(String rowClasses) {
        this.rowClasses = rowClasses;
    }

    public boolean isHideValidators() {
        return this.hideValidators;
    }

    /**
     * Allows you to suppress the inclusion of the validator messages in the
     * standard layout. This is specilly usefull if you want to show all
     * messages in one place in the page.
     * 
     * @property
     */
    public void setHideValidators(boolean hideValidators) {
        this.hideValidators = hideValidators;
    }

    public String getValidatorClasses() {
        return this.validatorClasses;
    }

    /**
     * Configure the html classes to apply to the validator messages.
     * 
     * @property
     */
    public void setValidatorClasses(String validatorClasses) {
        this.validatorClasses = validatorClasses;
    }

    public String getLabelTerminator() {
        return this.labelTerminator;
    }

    /**
     * Chooses the suffix to be added to each label. If the label already
     * contains that suffix then nothing will be added. See {@link StandardObjectRenderer#setLabelTerminator(String)}.
     * 
     * @property
     */
    public void setLabelTerminator(String labelTerminator) {
        this.labelTerminator = labelTerminator;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new ObjectInputTabularLayout(getContext().getMetaObject());
    }

    class ObjectInputTabularLayout extends TabularLayout {
        public Logger logger = Logger.getLogger(ObjectInputTabularLayout.class);

        protected Map<Integer, Validatable> inputComponents;

        protected MetaObject object;

        public ObjectInputTabularLayout(MetaObject object) {
            this.object = object;
            this.inputComponents = new HashMap<Integer, Validatable>();
        }

        @Override
        protected int getNumberOfColumns() {
            return 3;
        }

        @Override
        protected int getNumberOfRows() {
            return this.object.getSlots().size();
        }

        @Override
        protected HtmlComponent getHeaderComponent(int columnIndex) {
            return new HtmlText();
        }

        @Override
        protected boolean isHeader(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        protected HtmlComponent getComponent(int rowIndex, int columnIndex) {
            HtmlComponent component;

            switch (columnIndex) {
            case 0:
                MetaSlot slot = this.object.getSlots().get(rowIndex);
                if (displayLabel) {

                    if (slot.isReadOnly()) {
                        component = new HtmlText(addLabelTerminator(slot.getLabel()), false);
                    } else {
                        HtmlLabel label = new HtmlLabel();
                        label.setFor(slot.getKey().toString());
                        StringBuilder buffer = new StringBuilder();

                        if (slot.isRequired()) {
                            if (isRequiredMarkShown()) {
                                buffer.append(RenderUtils.getResourceString("RENDERER_RESOURCES",
                                        "renderers.validator.required.mark"));
                                buffer.append(" ");
                            }
                        }

                        buffer.append(slot.getLabel());

                        if (!slot.isRequired() && isOptionalMarkShown()) {
                            buffer.append(" ");
                            buffer.append(RenderUtils
                                    .getResourceString("RENDERER_RESOURCES", "renderers.validator.optional.mark"));
                        }

                        label.setText(addLabelTerminator(buffer.toString()));
                        label.setTitle(slot.getTitle());

                        component = label;
                    }
                } else {
                    component = null;
                }
                break;
            case 1:
                slot = this.object.getSlots().get(rowIndex);

                HtmlComponent renderedSlot = renderSlot(slot);

                if (!slot.isReadOnly()) {
                    Validatable validatable = findValidatableComponent(renderedSlot);

                    if (validatable != null) {
                        HtmlFormComponent formComponent = (HtmlFormComponent) validatable;
                        if (formComponent.getId() == null) {
                            formComponent.setId(slot.getKey().toString());
                        }
                        if (FenixWebFramework.getConfig().isJavascriptValidationEnabled() && !isHideValidators()) {
                            HtmlChainValidator chainValidator = getChainValidator(formComponent, slot);
                            for (HtmlValidator validator : chainValidator.getSupportedJavascriptValidators()) {
                                HtmlInlineContainer container = new HtmlInlineContainer();
                                container.addChild(renderedSlot);
                                container.addChild(validator.bindJavascript(formComponent));
                                renderedSlot = container;
                            }
                        }
                        inputComponents.put(rowIndex, validatable);
                    }
                }

                component =
                        slot.hasHelp() ? renderHelpOnComponent(renderedSlot, slot.getBundle(), slot.getHelpLabel(),
                                slot.getName()) : renderedSlot;

                break;
            case 2:
                if (isHideValidators()) {
                    component = new HtmlText();
                } else {
                    Validatable inputComponent = inputComponents.get(rowIndex);

                    if (inputComponent != null) {
                        HtmlChainValidator chainValidator =
                                getChainValidator(inputComponent, this.object.getSlots().get(rowIndex));

                        if (chainValidator != null && !chainValidator.isEmpty()) {
                            chainValidator.setClasses(getValidatorClasses());
                            component = chainValidator;
                        } else {
                            component = new HtmlText();
                        }
                    } else {
                        component = new HtmlText();
                    }
                }

                break;
            default:
                component = new HtmlText();
                break;
            }

            return component;
        }

        @Override
        protected void costumizeCell(HtmlTableCell cell, int rowIndex, int columnIndex) {
            super.costumizeCell(cell, rowIndex, columnIndex);

            if (columnIndex == 0) {
                cell.setScope("row");
            }
        }

        // duplicated code id=standard-renderer.label.addTerminator
        protected String addLabelTerminator(String label) {
            if (getLabelTerminator() == null) {
                return label;
            }

            if (label == null) {
                return null;
            }

            if (label.endsWith(getLabelTerminator())) {
                return label;
            }

            return label + getLabelTerminator();
        }

        protected HtmlComponent renderHelpOnComponent(HtmlComponent renderedSlot, String bundle, String helpLabel, String slotName) {

            String id = slotName + ":" + System.currentTimeMillis();

            HtmlBlockContainer container = new HtmlBlockContainer();

            HtmlBlockContainer helpContainer = new HtmlBlockContainer();
            helpContainer.setId(id);
            helpContainer.setClasses(getHelpNoJavascriptClasses());
            helpContainer.setOnMouseOver(getScript(id, getHelpOpenClasses()));
            helpContainer.setOnMouseOut(getScript(id, getHelpClosedClasses()));

            HtmlImage htmlImage = new HtmlImage();
            htmlImage.setSource(getHelpImageIcon());
            htmlImage.setDescription("help icon");

            helpContainer.addChild(htmlImage);

            HtmlBlockContainer textContainer = new HtmlBlockContainer();
            textContainer.setClasses(getHelpTextClasses());
            textContainer.addChild(new HtmlText(RenderUtils.getResourceString(bundle, helpLabel), false));
            helpContainer.addChild(textContainer);

            container.addChild(helpContainer);
            HtmlScript script = new HtmlScript();
            script.setContentType("text/javascript");
            script.setScript(getScript(id, getHelpClosedClasses()));
            container.addChild(script);

            HtmlTable table = new HtmlTable();
            HtmlTableRow row = table.createRow();
            HtmlTableCell cell = row.createCell();
            cell.setBody(renderedSlot);
            HtmlTableCell anotherCell = row.createCell();
            anotherCell.setBody(container);
            return table;
        }

        protected String getScript(String id, String classes) {
            return String.format("document.getElementById('%s').className='%s';", id, classes);
        }
    }

    public boolean isOptionalMarkShown() {
        return optionalMarkShown;
    }

    public void setOptionalMarkShown(boolean optionalMarkShown) {
        this.optionalMarkShown = optionalMarkShown;
    }

    public boolean isRequiredMarkShown() {
        return requiredMarkShown;
    }

    public void setRequiredMarkShown(boolean requiredMarkShown) {
        this.requiredMarkShown = requiredMarkShown;
    }

    public boolean isRequiredMessageShown() {
        return requiredMessageShown;
    }

    public void setRequiredMessageShown(boolean requiredMessageShown) {
        this.requiredMessageShown = requiredMessageShown;
    }

    public String getHelpImageIcon() {
        return helpImageIcon;
    }

    public void setHelpImageIcon(String helpImageIcon) {
        this.helpImageIcon = helpImageIcon;
    }

    public String getHelpNoJavascriptClasses() {
        return helpNoJavascriptClasses;
    }

    public void setHelpNoJavascriptClasses(String helpNoJavascript) {
        this.helpNoJavascriptClasses = helpNoJavascript;
    }

    public String getHelpClosedClasses() {
        return helpClosedClasses;
    }

    public void setHelpClosedClasses(String helpClosed) {
        this.helpClosedClasses = helpClosed;
    }

    public String getHelpOpenClasses() {
        return helpOpenClasses;
    }

    public void setHelpOpenClasses(String helpOpen) {
        this.helpOpenClasses = helpOpen;
    }

    public String getHelpTextClasses() {
        return helpTextClasses;
    }

    public void setHelpTextClasses(String helpTextClasses) {
        this.helpTextClasses = helpTextClasses;
    }

}
