package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * This renderer allows you to produce a short textual description of 
 * an object by using a simple format string.
 * 
 * @author cfgi
 */
public class FormatRenderer extends OutputRenderer {

    private String format;
    
    private Boolean escaped;

    public String getFormat() {
        return this.format;
    }

    /**
     * The format is simple string with a few rules to indicate where slot values
     * will be placed. An example string is
     * <pre>
     *  "${name} is ${age} years old"
     * </pre>
     * Assuming that we are taking about an object that represents a person and
     * has a <code>getName()</code> and <code>getAge()</code> methods this format
     * would produce something like: <code>"Jane Doe is 20 years old"</code>
     * <p>
     * Any literal text will be present in the output. Slot values can be including
     * by surrounding the slot name in <code>${&lt;slot.name&gt;}</code>. Additionally
     * you can indicate how to format each value. This is specially usefull if you 
     * want to present dates.
     * <pre>
     * "${name} was born in a ${birthdate,%ta}
     * </pre>
     * Could present something like <code>"Jane Doe was born in a Sunday"</code>. 
     * You can use use the same syntax that is accepted by 
     * #{@link String#format(java.lang.String, java.lang.Object[])}. 
     * 
     * @property
     */
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new FormatLayout();
    }

    public Boolean getEscaped() {
	return escaped;
    }

    public void setEscaped(Boolean escaped) {
	this.escaped = escaped;
    }

    private class FormatLayout extends Layout {

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            if (object == null) {
                return new HtmlText();
            }
            
            String formatedObject = RenderUtils.getFormattedProperties(getFormat(), object);
	    return getEscaped() != null ? new HtmlText(formatedObject, getEscaped()) : new HtmlText(formatedObject);
        }

    }
}
