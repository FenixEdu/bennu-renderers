package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * A basic presentation of an integer number.
 * 
 * @author cfgi
 */
public class IntegerRenderer extends OutputRenderer {

    private int base;
    private String prefix;
    private String suffix;
    private String bundle;

    public int getBase() {
        return this.base;
    }

    /**
     * Indicates the base in wich the number will be presented.
     * 
     * @property
     */
    public void setBase(int base) {
        this.base = base;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                Number number = (Number) object;

                String text;
                if (number != null && number instanceof Integer) {
                    text = Integer.toString(number.intValue(), getBase());
                } else {
                    text = number == null ? "" : number.toString();
                }

                // Apply Prefix&Suffix
                String prefixString = RenderUtils.getResourceString(getBundle(), prefix);
                String suffixString = RenderUtils.getResourceString(getBundle(), suffix);
                if (prefixString != null || suffixString != null) {
                    StringBuilder strBuilder = prefixString == null ? new StringBuilder() : new StringBuilder(prefixString);
                    strBuilder.append(text);
                    strBuilder.append(suffixString);
                    text = strBuilder.toString();
                }

                return new HtmlText(text);
            }

        };
    }
}
