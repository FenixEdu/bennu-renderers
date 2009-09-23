package pt.ist.fenixWebFramework.renderers.layouts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.tagext.BodyContent;

import pt.ist.fenixWebFramework._development.LogLevel;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.html.HTMLBodyElement;

public abstract class Layout {
    private static Logger logger = Logger.getLogger(Layout.class);

    private String classes;

    private String style;

    private String title;

    private boolean requiredMarkShown;

    private boolean optionalMarkShown;

    private static final String REQUIRED_EXPLANATION_CLASS = "requiredMessage";

    public boolean isRequiredMarkShown() {
	return requiredMarkShown;
    }

    public void setRequiredMarkShown(boolean requiredMarkShown) {
	this.requiredMarkShown = requiredMarkShown;
    }

    public boolean isOptionalMarkShown() {
	return optionalMarkShown;
    }

    public void setOptionalMarkShown(boolean optionalMarkShown) {
	this.optionalMarkShown = optionalMarkShown;
    }

    public void setClasses(String classes) {
	this.classes = classes;
    }

    public void setStyle(String style) {
	this.style = style;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getClasses() {
	return classes;
    }

    public String getStyle() {
	return style;
    }

    public String getTitle() {
	return title;
    }

    public String[] getAndVerifyPropertyNames() {
	String[] names = getPropertyNames();
	List<String> finalNames = new ArrayList<String>();

	for (int i = 0; i < names.length; i++) {
	    String name = names[i];

	    if (!PropertyUtils.isWriteable(this, name)) {
		if (LogLevel.WARN) {
		    logger.warn("Layout " + this + " specified a non-writeable property: " + name);
		}
	    } else {
		finalNames.add(name);
	    }
	}

	return finalNames.toArray(new String[0]);
    }

    public String[] getPropertyNames() {
	return new String[] { "classes", "style", "title", "requiredMarkShown", "optionalMarkShown" };
    }

    protected String[] mergePropertyNames(String[] parentNames, String[] ownNames) {
	String[] allNames = new String[parentNames.length + ownNames.length];

	for (int i = 0; i < allNames.length; i++) {
	    allNames[i] = i < parentNames.length ? parentNames[i] : ownNames[i - parentNames.length];
	}

	return allNames;
    }

    public HtmlComponent createLayout(Object object, Class type) {
	HtmlComponent component = createComponent(object, type);
	applyStyle(component);

	if (isRequiredMarkShown()) {
	    HtmlBlockContainer container = new HtmlBlockContainer();
	    container.addChild(component);
	    HtmlText requiredMessage = new HtmlText(RenderUtils.getResourceString("RENDERER_RESOURCES",
		    "renderers.validator.required.mark.explanation"));
	    requiredMessage.setClasses(REQUIRED_EXPLANATION_CLASS);
	    container.addChild(requiredMessage);
	    component = container;
	}
	return component;
    }

    public abstract HtmlComponent createComponent(Object object, Class type);

    public void applyStyle(HtmlComponent component) {
	component.setClasses(getClasses());
	component.setStyle(getStyle());
	component.setTitle(getTitle());
    }
}
