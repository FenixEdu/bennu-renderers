package pt.ist.fenixWebFramework.renderers.components;

import java.util.List;

import javax.servlet.jsp.PageContext;

import org.apache.commons.collections.Predicate;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlRadioButtonList extends HtmlRadioButtonGroup {

    private HtmlList list;

    public HtmlRadioButtonList() {
        super();

        this.list = new HtmlList();
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

    public HtmlList getList() {
        return this.list;
    }

    public HtmlRadioButton addOption(HtmlComponent component, String value) {
        HtmlRadioButton radio = addOption(component);

        radio.setUserValue(value);
        return radio;
    }

    public HtmlRadioButton addOption(HtmlComponent component) {
        HtmlRadioButton radio = createRadioButton();

        HtmlListItem item = this.list.createItem();
        item.addChild(component);

        return radio;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        super.getOwnTag(context);

        for (int i = 0; i < this.list.getItems().size(); i++) {
            HtmlListItem item = this.list.getItems().get(i);
            HtmlInlineContainer container = new HtmlInlineContainer();

            container.addChild(getRadioButtons().get(i));

            for (HtmlComponent component : item.getChildren()) {
                container.addChild(component);
            }

            item.setBody(container);
        }

        return this.list.getOwnTag(context);
    }
}