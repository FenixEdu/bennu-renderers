package pt.ist.fenixWebFramework.renderers;

import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlList;
import pt.ist.fenixWebFramework.renderers.components.HtmlListItem;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.contexts.OutputContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class GenericOutputWithHoverList extends AbstractToolTipRenderer {

    private String list;

    private String listFormat;

    private String prefix;

    private String prefixClasses;

    private boolean counterEnabled;

    public boolean isCounterEnabled() {
        return counterEnabled;
    }

    public void setCounterEnabled(boolean counter) {
        this.counterEnabled = counter;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefixClasses() {
        return prefixClasses;
    }

    public void setPrefixClasses(String prefixClasses) {
        this.prefixClasses = prefixClasses;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getListFormat() {
        return listFormat;
    }

    public void setListFormat(String listFormat) {
        this.listFormat = listFormat;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new ToolTipLayout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                OutputContext context = getOutputContext();

                context.setLayout(getSubLayout());
                context.setProperties(getProperties());

                HtmlComponent component = RenderKit.getInstance().render(context, object, type);
                Collection list = null;

                try {
                    list = (Collection) PropertyUtils.getProperty(getTargetObject(object), getList());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (list.isEmpty()) {
                    return component;
                }

                HtmlList htmlList = new HtmlList();

                int i = 1;
                for (Object objectInList : list) {
                    HtmlListItem item = htmlList.createItem();
                    item.addChild(getTextToDisplay(objectInList, i++));
                }

                return wrapUpCompletion(component, htmlList);
            }

            private HtmlComponent getTextToDisplay(Object objectInList, int index) {
                HtmlInlineContainer inlineContainer = new HtmlInlineContainer();
                inlineContainer.setIndented(false);
                if (getPrefix() != null) {
                    HtmlInlineContainer prefixContainer = new HtmlInlineContainer();
                    prefixContainer.addChild(new HtmlText(
                            isKey() ? RenderUtils.getResourceString(getBundle(), getPrefix()) : getPrefix()));
                    prefixContainer.setClasses(getPrefixClasses());
                    inlineContainer.addChild(prefixContainer);
                }

                if (isCounterEnabled()) {
                    HtmlInlineContainer counterContainer = new HtmlInlineContainer();
                    counterContainer.addChild(new HtmlText(String.valueOf(index)));
                    counterContainer.setClasses(getPrefixClasses());
                    inlineContainer.addChild(counterContainer);
                }

                if (getPrefix() != null | isCounterEnabled()) {
                    inlineContainer.addChild(new HtmlText(": "));
                }

                inlineContainer.addChild(new HtmlText(RenderUtils.getFormattedProperties(getListFormat(), objectInList),
                        isEscape()));

                return inlineContainer;
            }
        };
    }
}
