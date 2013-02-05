package pt.ist.fenixWebFramework.renderers.layouts;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlList;
import pt.ist.fenixWebFramework.renderers.components.HtmlListItem;

public abstract class ListLayout extends FlowLayout {

    public ListLayout() {
        super();
    }

    @Override
    protected HtmlComponent getContainer() {
        return new HtmlList();
    }

    @Override
    protected void addComponent(HtmlComponent container, HtmlComponent component) {
        HtmlList list = (HtmlList) container;

        HtmlListItem item = list.createItem();
        item.setBody(component);
    }
}
