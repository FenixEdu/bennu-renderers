package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBox;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.contexts.InputContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * This renderer allows you to do the input of a boolean value. A checkbox is
 * presented and is checked accordingly with the slot's value.
 * 
 * <p>
 * Example: <input type="checkbox"/>
 * 
 * @author cfgi
 */
public class BooleanInputRenderer extends InputRenderer {
    private boolean disabled;

    private String bundle;

    private String bodyText;

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                HtmlCheckBox checkBox = new HtmlCheckBox();
                checkBox.setChecked(object == null ? false : (Boolean) object);
                checkBox.setText(RenderUtils.getResourceString(getBundle(), getBodyText()));

                InputContext context = getInputContext();
                checkBox.setTargetSlot((MetaSlotKey) context.getMetaObject().getKey());

                return checkBox;
            }

            @Override
            public void applyStyle(HtmlComponent component) {
                super.applyStyle(component);

                HtmlCheckBox checkBox = (HtmlCheckBox) component;
                checkBox.setDisabled(getDisabled());
            }

        };
    }

    public boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }
}
