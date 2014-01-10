package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * The <code>EnumRenderer</code> provides a simple presentation for enumeration
 * values. An enum value will be displayed in one of two forms. First a bundle
 * named <code>ENUMERATION_RESOURCES</code> is used. Using as key the enum name
 * a localized message is searched. If the bundle is not defined or the key does
 * not exist in the bundle then the programmatic name of the enum is presented.
 * 
 * @author cfgi
 */
public class EnumRenderer extends OutputRenderer {

    private String bundle;
    private boolean allowHTML = false;
    private String nullLabel;

    public String getNullLabel() {
        return nullLabel;
    }

    public void setNullLabel(String nullLabel) {
        this.nullLabel = nullLabel;
    }

    public boolean getAllowHTML() {
        return allowHTML;
    }

    public void setAllowHTML(boolean allowHTML) {
        this.allowHTML = allowHTML;
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
                Enum enumerate = (Enum) object;

                if (enumerate == null) {
                    return new HtmlText(getNullLabel());
                }

                String description = RenderUtils.getEnumString(enumerate, getBundle());

                return new HtmlText(description, !getAllowHTML());
            }

        };
    }
}
