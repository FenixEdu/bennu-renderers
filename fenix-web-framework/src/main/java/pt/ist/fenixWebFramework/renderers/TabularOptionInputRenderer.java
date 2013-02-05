package pt.ist.fenixWebFramework.renderers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBox;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlMultipleHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlController;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.layouts.TabularLayout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectCollection;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * This renderer allows you choose several object from a list of choices. The
 * list of choices is presented in a table but each row has a checkbox that
 * allows you to select the object in that row.
 * 
 * <p>
 * The list of options is given by a {@link pt.ist.fenixWebFramework.renderers.DataProvider data provider}.
 * 
 * <p>
 * Example:
 * <table border="1">
 * <thead>
 * <th></th>
 * <th>Name</th>
 * <th>Age</th>
 * <th>Gender</th>
 * </thead>
 * <tr>
 * <td><input type="checkbox"/></td>
 * <td>Name A</td>
 * <td>20</td>
 * <td>Female</td>
 * </tr>
 * <tr>
 * <td><input type="checkbox" checked="checked"/></td>
 * <td>Name B</td>
 * <td>22</td>
 * <td>Male</td>
 * </tr>
 * <tr>
 * <td><input type="checkbox" checked="checked"/></td>
 * <td>Name C</td>
 * <td>21</td>
 * <td>Female</td>
 * </tr>
 * </table>
 * 
 * @author pcma
 */
public class TabularOptionInputRenderer extends SelectionRenderer {
    private String classes;

    private String emptyMessageKey;

    private String emptyMessageBundle;

    private String columnClasses;

    /**
     * Selects column classes
     * 
     * @property
     * 
     */
    public String getColumnClasses() {
        return columnClasses;
    }

    public void setColumnClasses(String columnClasses) {
        this.columnClasses = columnClasses;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {

        List<MetaObject> metaObjects = getMetaObjects(getPossibleObjects());
        Collection objectsReceived = (Collection) object;

        HtmlMultipleHiddenField hiddenField = new HtmlMultipleHiddenField();
        hiddenField.bind((MetaSlot) getContext().getMetaObject());
        hiddenField.setConverter(getConverter());

        return new CheckableTabularLayout(metaObjects, objectsReceived, hiddenField);
    }

    private List<MetaObject> getMetaObjects(Collection collection) {
        List<MetaObject> metaObjects = new ArrayList<MetaObject>();

        MetaObject contextMetaObject = getContext().getMetaObject();
        if (contextMetaObject instanceof MetaObjectCollection) {
            // reuse meta objects
            MetaObjectCollection multipleMetaObject = (MetaObjectCollection) getContext().getMetaObject();

            for (Object object : collection) {
                for (MetaObject metaObject : multipleMetaObject.getAllMetaObjects()) {
                    if (object.equals(metaObject.getObject())) {
                        metaObjects.add(metaObject);
                        break;
                    }
                }
            }
        } else {
            Schema schema = getContext().getSchema();
            for (Object object : collection) {
                metaObjects.add(MetaObjectFactory.createObject(object, schema));
            }
        }

        return metaObjects;
    }

    @Override
    public String getClasses() {
        return classes;
    }

    @Override
    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getEmptyMessageBundle() {
        return emptyMessageBundle;
    }

    public void setEmptyMessageBundle(String emptyMessageBundle) {
        this.emptyMessageBundle = emptyMessageBundle;
    }

    public String getEmptyMessageKey() {
        return emptyMessageKey;
    }

    public void setEmptyMessageKey(String emptyMessageKey) {
        this.emptyMessageKey = emptyMessageKey;
    }

    public class CheckableTabularLayout extends TabularLayout {

        protected List<MetaObject> metaObjects;
        protected Collection<?> objectsReceived;
        protected HtmlMultipleHiddenField hiddenField;

        protected List<HtmlCheckBox> checkboxes = new ArrayList<HtmlCheckBox>();

        public CheckableTabularLayout(List<MetaObject> metaObjects, Collection<?> collection, HtmlMultipleHiddenField hiddenField) {
            this.metaObjects = metaObjects;
            this.objectsReceived = collection;
            this.hiddenField = hiddenField;
        }

        public HtmlMultipleHiddenField getHiddenField() {
            return hiddenField;
        }

        public List<HtmlCheckBox> getCheckBoxes() {
            return checkboxes;
        }

        @Override
        public HtmlComponent createLayout(Object object, Class type) {
            HtmlContainer container = new HtmlInlineContainer();
            HtmlComponent component = super.createLayout(object, type);
            container.addChild(hiddenField);
            container.addChild(component);
            if (metaObjects.isEmpty() && !StringUtils.isEmpty(getEmptyMessageKey())) {
                HtmlText emptyMessage =
                        new HtmlText(RenderUtils.getResourceString(getEmptyMessageBundle(), getEmptyMessageKey()), false);
                container.addChild(emptyMessage);
            }

            hiddenField.setController(new HtmlController() {

                @Override
                public void execute(IViewState viewState) {
                    List<String> values = new ArrayList<String>();

                    for (HtmlCheckBox checkBox : checkboxes) {
                        if (checkBox.isChecked()) {
                            values.add(checkBox.getValue());
                        }
                    }

                    hiddenField.setValues(values.toArray(new String[0]));
                }
            });

            return container;
        }

        @Override
        protected boolean hasHeader() {
            return metaObjects.size() > 0;
        }

        @Override
        protected HtmlComponent getHeaderComponent(int columnIndex) {
            String text = "";
            if (columnIndex != 0) {
                text = metaObjects.get(0).getSlots().get(columnIndex - 1).getLabel();
            }
            return new HtmlText(text, false);
        }

        @Override
        protected int getNumberOfColumns() {
            if (metaObjects.size() > 0) {
                MetaObject metaObject = metaObjects.get(0);
                return metaObject.getSlots().size() + 1; // +1 due to the
                // checkbox
            }
            return 0;
        }

        @Override
        protected int getNumberOfRows() {
            return metaObjects.size();
        }

        @Override
        protected HtmlComponent getComponent(int rowIndex, int columnIndex) {

            if (columnIndex == 0) {
                HtmlCheckBox checkBox = new HtmlCheckBox();
                this.checkboxes.add(checkBox);

                MetaObject metaObject = metaObjects.get(rowIndex);
                checkBox.setUserValue(metaObject.getKey().toString());

                checkBox.setName(hiddenField.getName() + "/" + metaObject.getKey().toString());

                if (objectsReceived != null && objectsReceived.contains(metaObject.getObject())) {
                    checkBox.setChecked(true);
                }
                return checkBox;
            }
            MetaSlot slot = getSlotUsingName(metaObjects.get(rowIndex), columnIndex - 1);
            slot.setReadOnly(true);
            return renderSlot(slot);
        }

        protected MetaSlot getSlotUsingName(MetaObject object, int columnIndex) {
            MetaObject referenceObject = metaObjects.get(0);
            MetaSlot referenceSlot = referenceObject.getSlots().get(columnIndex);

            MetaSlot directSlot = object.getSlots().get(columnIndex); // common
            // case
            if (directSlot.getName().equals(referenceSlot.getName())) {
                return directSlot;
            }

            for (MetaSlot slot : object.getSlots()) {
                if (slot.getName().equals(referenceSlot.getName())) {
                    return slot;
                }
            }

            return null;
        }

    };

}
