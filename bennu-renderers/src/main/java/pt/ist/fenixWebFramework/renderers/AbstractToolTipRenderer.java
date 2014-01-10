package pt.ist.fenixWebFramework.renderers;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

public abstract class AbstractToolTipRenderer extends OutputRenderer {

    private String bundle;

    private boolean key;

    private String subLayout;

    private boolean useParent;

    private boolean escape = true;

    private String openClasses;

    private String closedClasses;

    private String textClasses;

    private Map<String, String> properties = new HashMap<String, String>();

    public boolean isUseParent() {
        return useParent;
    }

    public void setUseParent(boolean useParent) {
        this.useParent = useParent;
    }

    public String getSubLayout() {
        return subLayout;
    }

    public void setSubLayout(String subLayout) {
        this.subLayout = subLayout;
    }

    private Map<String, String> getPropertiesMap() {
        return properties;
    }

    public void setSubProperty(String property, String value) {
        properties.put(property, value);
    }

    public String getSubProperty(String property) {
        return properties.get(property);
    }

    public boolean isEscape() {
        return escape;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }

    public String getOpenClasses() {
        return openClasses;
    }

    public void setOpenClasses(String openClasses) {
        this.openClasses = openClasses;
    }

    public String getClosedClasses() {
        return closedClasses;
    }

    public void setClosedClasses(String closedClasses) {
        this.closedClasses = closedClasses;
    }

    public String getTextClasses() {
        return textClasses;
    }

    public void setTextClasses(String textClasses) {
        this.textClasses = textClasses;
    }

    public abstract class ToolTipLayout extends Layout {

        protected Properties getProperties() {
            Properties properties = new Properties();
            Map<String, String> map = getPropertiesMap();
            for (String property : map.keySet()) {
                properties.put(property, map.get(property));
            }
            return properties;
        }

        protected HtmlContainer wrapUpCompletion(HtmlComponent renderedComponent, HtmlComponent hoverComponent) {

            HtmlContainer container = new HtmlBlockContainer();
            HtmlInlineContainer span = new HtmlInlineContainer();
            span.addChild(renderedComponent);
            container.addChild(span);
            String id =
                    HtmlComponent.getValidIdOrName(String.valueOf(hoverComponent.hashCode())) + ":" + System.currentTimeMillis();

            HtmlBlockContainer toolTipContainer = new HtmlBlockContainer();
            container.setId(id);
            container.setOnMouseOver(getScript(id, getOpenClasses()));
            container.setOnMouseOut(getScript(id, getClosedClasses()));
            container.setClasses(getClosedClasses());

            toolTipContainer.setClasses(getTextClasses());
            toolTipContainer.addChild(hoverComponent);

            container.addChild(toolTipContainer);
            HtmlScript script = new HtmlScript();
            script.setContentType("text/javascript");
            script.setScript(getScript(id, getClosedClasses()));
            container.addChild(script);

            return container;
        }

        private String getScript(String id, String classes) {
            return String.format("document.getElementById('%s').className='%s';", id, classes);
        }

        protected Object getTargetObject(Object object) {
            if (isUseParent()) {
                if (getContext().getParentContext() != null) {
                    return getContext().getParentContext().getMetaObject().getObject();
                } else {
                    return null;
                }
            } else {
                return object;
            }
        }
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }
}
