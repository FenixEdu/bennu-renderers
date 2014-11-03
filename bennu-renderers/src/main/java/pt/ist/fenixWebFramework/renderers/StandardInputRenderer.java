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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import pt.ist.fenixWebFramework.RenderersConfigurationManager;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlImage;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.Validatable;
import pt.ist.fenixWebFramework.renderers.layouts.FormLayout;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;

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

    private boolean requiredMarkShown = RenderersConfigurationManager.getConfiguration().requiredMarkShown();

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
        return new ObjectInputFormLayout(getContext().getMetaObject());
    }

    class ObjectInputFormLayout extends FormLayout {
        private final MetaObject object;
        private final Map<Integer, Validatable> inputComponents;

        public ObjectInputFormLayout(MetaObject object) {
            this.object = object;
            this.inputComponents = new HashMap<Integer, Validatable>();
        }

        @Override
        public int getNumberOfRows() {
            return object.getSlots().size();
        }

        @Override
        public String getLabelText(int rowIndex) {
            MetaSlot slot = this.object.getSlots().get(rowIndex);
            return slot.getLabel();
        }

        @Override
        public HtmlComponent getRenderedSlot(int rowIndex) {
            MetaSlot slot = this.object.getSlots().get(rowIndex);
            HtmlComponent renderedSlot = renderSlot(slot);

            if (!slot.isReadOnly()) {
                Validatable validatable = findValidatableComponent(renderedSlot);
                if (validatable != null) {
                    HtmlFormComponent formComponent = (HtmlFormComponent) validatable;
                    if (formComponent.getId() == null) {
                        formComponent.setId(slot.getKey().toString());
                    }
                    inputComponents.put(rowIndex, validatable);
                }
            }

            return renderedSlot;
        }

        @Override
        public Supplier<Optional<String>> getValidationError(int rowIndex) {
            if (isHideValidators()) {
                return Optional::empty;
            }

            Validatable inputComponent = inputComponents.get(rowIndex);

            if (inputComponent != null) {
                HtmlChainValidator chainValidator = getChainValidator(inputComponent, this.object.getSlots().get(rowIndex));
                if (chainValidator != null && !chainValidator.isEmpty()) {
                    return () -> {
                        if (chainValidator.isValid()) {
                            return Optional.empty();
                        } else {
                            return Optional.of(chainValidator.getErrorMessage());
                        }
                    };
                }
            }
            return Optional::empty;
        }

        @Override
        public Optional<String> getHelpLabel(int rowIndex) {
            MetaSlot slot = this.object.getSlots().get(rowIndex);
            if (!slot.hasHelp()) {
                return Optional.empty();
            } else {
                String value = RenderUtils.getResourceString(slot.getBundle(), slot.getHelpLabel());
                return value == null ? Optional.of("!" + slot.getHelpLabel() + "!") : Optional.of(value);
            }
        }
    }

    class ObjectInputTabularLayout {

        public ObjectInputTabularLayout(MetaObject object) {
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
