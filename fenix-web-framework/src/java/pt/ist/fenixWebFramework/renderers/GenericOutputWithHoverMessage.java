package pt.ist.fenixWebFramework.renderers;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.contexts.OutputContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class GenericOutputWithHoverMessage extends OutputRenderer {

    private String format;

    private String bundle;

    private String hoverMessage;

    private boolean key;

    private String openClasses;

    private String closedClasses;

    private String textClasses;

    private String noJavascriptClasses;

    private String subLayout;

    private boolean useParent;

    public boolean isUseParent() {
	return useParent;
    }

    public void setUseParent(boolean useParent) {
	this.useParent = useParent;
    }

    private Map<String, String> properties = new HashMap<String, String>();

    public String getFormat() {
	return format;
    }

    public void setFormat(String format) {
	this.format = format;
    }

    public String getBundle() {
	return bundle;
    }

    public void setBundle(String bundle) {
	this.bundle = bundle;
    }

    public String getHoverMessage() {
	return hoverMessage;
    }

    public void setHoverMessage(String hover) {
	this.hoverMessage = hover;
    }

    public boolean isKey() {
	return key;
    }

    public void setKey(boolean key) {
	this.key = key;
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

    public String getNoJavascriptClasses() {
	return noJavascriptClasses;
    }

    public void setNoJavascriptClasses(String noJavascriptClasses) {
	this.noJavascriptClasses = noJavascriptClasses;
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

    @Override
    protected Layout getLayout(Object object, Class type) {
	return new Layout() {
	    private Properties getProperties() {
		Properties properties = new Properties();
		Map<String, String> map = getPropertiesMap();
		for (String property : map.keySet()) {
		    properties.put(property, map.get(property));
		}
		return properties;
	    }

	    @Override
	    public HtmlComponent createComponent(Object object, Class type) {
		OutputContext context = getOutputContext();

		context.setLayout(getSubLayout());
		context.setProperties(getProperties());

		HtmlComponent component = RenderKit.getInstance().render(context, object, type);
		String hoverMessage = null;

		if (getFormat() != null) {
		    hoverMessage = RenderUtils.getFormattedProperties(getFormat(), getTargetObject(object));
		} else {
		    if (isKey()) {
			hoverMessage = RenderUtils.getResourceString(getBundle(), getHoverMessage());
		    } else {
			hoverMessage = getHoverMessage();
		    }
		}

		return wrapUpCompletion(component, HtmlText.escape(hoverMessage));
	    }

	    private HtmlContainer wrapUpCompletion(HtmlComponent component, String escape) {

		HtmlContainer container = new HtmlBlockContainer();
		container.addChild(component);
		String id = HtmlComponent.getValidIdOrName(String.valueOf(escape.hashCode())) + ":" + System.currentTimeMillis();

		HtmlBlockContainer helpContainer = new HtmlBlockContainer();
		helpContainer.setClasses(getNoJavascriptClasses());
		helpContainer.setId(id);
		component.setOnMouseOver(getScript(id, getOpenClasses()));
		component.setOnMouseOut(getScript(id, getClosedClasses()));

		HtmlBlockContainer textContainer = new HtmlBlockContainer();
		textContainer.setClasses(getTextClasses());
		textContainer.addChild(new HtmlText(escape));
		helpContainer.addChild(textContainer);

		container.addChild(helpContainer);
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
	};
    }
}
