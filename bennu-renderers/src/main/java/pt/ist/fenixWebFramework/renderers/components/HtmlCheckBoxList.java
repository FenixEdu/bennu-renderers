package pt.ist.fenixWebFramework.renderers.components;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.apache.commons.collections.Predicate;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class HtmlCheckBoxList extends HtmlMultipleValueComponent {

    private HtmlList list;

    private List<HtmlCheckBox> checkBoxes;

    private List<HtmlHiddenField> hiddenFields;

    private boolean selectAllShown;

    public HtmlCheckBoxList() {
        super();

        this.list = new HtmlList();
        this.checkBoxes = new ArrayList<HtmlCheckBox>();
        this.hiddenFields = new ArrayList<HtmlHiddenField>();
    }

    @Override
    public void addClass(String newClass) {
        this.list.addClass(newClass);
    }

    public HtmlListItem createItem() {
        return this.list.createItem();
    }

    @Override
    public HtmlComponent getChild(Predicate predicate) {
        return this.list.getChild(predicate);
    }

    @Override
    public List<HtmlComponent> getChildren() {
        return this.list.getChildren();
    }

    @Override
    public List<HtmlComponent> getChildren(Predicate predicate) {
        return this.list.getChildren(predicate);
    }

    @Override
    public HtmlComponent getChildWithId(String id) {
        return this.list.getChildWithId(id);
    }

    @Override
    public String getClasses() {
        return this.list.getClasses();
    }

    @Override
    public String getId() {
        return this.list.getId();
    }

    @Override
    public String getOnClick() {
        return this.list.getOnClick();
    }

    @Override
    public String getOnDblClick() {
        return this.list.getOnDblClick();
    }

    @Override
    public String getOnKeyDown() {
        return this.list.getOnKeyDown();
    }

    @Override
    public String getOnKeyPress() {
        return this.list.getOnKeyPress();
    }

    @Override
    public String getOnKeyUp() {
        return this.list.getOnKeyUp();
    }

    @Override
    public String getOnMouseDown() {
        return this.list.getOnMouseDown();
    }

    @Override
    public String getOnMouseMove() {
        return this.list.getOnMouseMove();
    }

    @Override
    public String getOnMouseOut() {
        return this.list.getOnMouseOut();
    }

    @Override
    public String getOnMouseOver() {
        return this.list.getOnMouseOver();
    }

    @Override
    public String getOnMouseUp() {
        return this.list.getOnMouseUp();
    }

    @Override
    public String getStyle() {
        return this.list.getStyle();
    }

    @Override
    public String getTitle() {
        return this.list.getTitle();
    }

    @Override
    public boolean isVisible() {
        return this.list.isVisible();
    }

    @Override
    public void setClasses(String classes) {
        this.list.setClasses(classes);
    }

    @Override
    public void setId(String id) {
        this.list.setId(id);
    }

    @Override
    public void setOnClick(String onclick) {
        this.list.setOnClick(onclick);
    }

    @Override
    public void setOnDblClick(String ondblclick) {
        this.list.setOnDblClick(ondblclick);
    }

    @Override
    public void setOnKeyDown(String onkeydown) {
        this.list.setOnKeyDown(onkeydown);
    }

    @Override
    public void setOnKeyPress(String onkeypress) {
        this.list.setOnKeyPress(onkeypress);
    }

    @Override
    public void setOnKeyUp(String onkeyup) {
        this.list.setOnKeyUp(onkeyup);
    }

    @Override
    public void setOnMouseDown(String onmousedown) {
        this.list.setOnMouseDown(onmousedown);
    }

    @Override
    public void setOnMouseMove(String onmousemove) {
        this.list.setOnMouseMove(onmousemove);
    }

    @Override
    public void setOnMouseOut(String onmouseout) {
        this.list.setOnMouseOut(onmouseout);
    }

    @Override
    public void setOnMouseOver(String onmouseover) {
        this.list.setOnMouseOver(onmouseover);
    }

    @Override
    public void setOnMouseUp(String onmouseup) {
        this.list.setOnMouseUp(onmouseup);
    }

    @Override
    public void setStyle(String style) {
        this.list.setStyle(style);
    }

    @Override
    public void setTitle(String title) {
        this.list.setTitle(title);
    }

    @Override
    public void setVisible(boolean visible) {
        this.list.setVisible(visible);
    }

    public boolean isSelectAllShown() {
        return this.selectAllShown;
    }

    public void setSelectAllShown(boolean selectAllShown) {
        this.selectAllShown = selectAllShown;
    }

    public List<HtmlCheckBox> getCheckBoxes() {
        return this.checkBoxes;
    }

    protected List<HtmlHiddenField> getHiddenFields() {
        return this.hiddenFields;
    }

    public HtmlList getList() {
        return this.list;
    }

    @Override
    public void setValues(String... values) {
        super.setValues(values);

        outter: for (HtmlCheckBox checkBox : getCheckBoxes()) {
            for (String value : values) {
                if (value.equals(checkBox.getValue())) {
                    checkBox.setChecked(true);
                    continue outter;
                }
            }

            checkBox.setChecked(false);
        }
    }

    public HtmlCheckBox addOption(HtmlComponent component, String value) {
        HtmlCheckBox checkBox = addOption(component);

        checkBox.setUserValue(value);
        return checkBox;
    }

    public HtmlHiddenField addHiddenOption(String value) {
        HtmlHiddenField hiddenField = new HtmlHiddenField();

        getHiddenFields().add(hiddenField);

        hiddenField.setValue(value);
        return hiddenField;
    }

    public HtmlCheckBox addOption(HtmlComponent component) {
        HtmlCheckBox checkBox = new HtmlCheckBox();
        getCheckBoxes().add(checkBox);

        HtmlListItem item = this.list.createItem();
        item.setBody(component);

        return checkBox;
    }

    protected HtmlCheckBox addOption(HtmlComponent component, int index) {
        HtmlCheckBox checkBox = new HtmlCheckBox();
        getCheckBoxes().add(index, checkBox);

        HtmlListItem item = this.list.createItem(index);
        item.setBody(component);

        return checkBox;
    }

    protected HtmlCheckBox addOption(HtmlCheckBox checkBox, int index) {
        getCheckBoxes().add(index, checkBox);

        HtmlListItem item = this.list.createItem(index);
        item.setBody(new HtmlLabel(checkBox.getText()));
        checkBox.setText("");

        return checkBox;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        int index = 0;
        for (HtmlCheckBox checkBox : getCheckBoxes()) {
            checkBox.setName(getName());

            if (getTargetSlot() != null) {
                checkBox.setTargetSlot(getTargetSlot());
            }

            checkBox.setId(checkBox.getName() + "/" + index++);
        }

        for (HtmlHiddenField hiddenField : getHiddenFields()) {
            hiddenField.setName(getName());

            if (getTargetSlot() != null) {
                hiddenField.setTargetSlot(getTargetSlot());
            }
        }

        if (isSelectAllShown()) {
            StringBuilder selectAllScript = new StringBuilder();
            boolean allChecked = true;

            String checkAllId = getValidIdOrName(getName() + "/all");

            for (HtmlCheckBox checkBox : getCheckBoxes()) {
                if (!checkBox.isChecked()) {
                    allChecked = false;
                }

                selectAllScript.append("var checkbox = document.getElementById('" + checkBox.getId()
                        + "'); if (!checkbox.disabled) checkbox.checked = this.checked; ");

                String eachScript = "if (! this.checked) document.getElementById('" + checkAllId + "').checked = false;";
                checkBox.setOnClick(eachScript);
                checkBox.setOnDblClick(eachScript);
            }

            HtmlCheckBox checkAllBox =
                    addOption(new HtmlLabel(RenderUtils.getResourceString("renderers.checkboxlist.selectAll")), 0);
            checkAllBox.setId(checkAllId);
            checkAllBox.setChecked(allChecked);
            checkAllBox.setOnClick(selectAllScript.toString());
            checkAllBox.setOnDblClick(selectAllScript.toString());
        }

        for (int i = 0; i < this.list.getItems().size(); i++) {
            HtmlListItem item = this.list.getItems().get(i);
            HtmlInlineContainer container = new HtmlInlineContainer();

            container.addChild(getCheckBoxes().get(i));
            container.addChild(item.getBody());

            item.setBody(container);
        }

        if (getHiddenFields().size() > 0) {
            HtmlContainer container = new HtmlInlineContainer();

            container.addChild(list);
            for (HtmlHiddenField hiddenField : getHiddenFields()) {
                container.addChild(hiddenField);
            }

            return container.getOwnTag(context);
        } else {
            return this.list.getOwnTag(context);
        }
    }
}
