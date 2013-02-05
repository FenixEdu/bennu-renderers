package pt.ist.fenixWebFramework.renderers;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.contexts.InputContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * This renderer serves as a base for all input renderers that are
 * based in a text input field.
 * 
 * @author cfgi
 */
public abstract class TextFieldRenderer extends InputRenderer {

    private boolean disabled;

    private boolean readOnly;

    private String size;

    private Integer maxLength;

    private String formatText;
    private String bundle;
    private boolean key;

    public boolean getDisabled() {
        return disabled;
    }

    /**
     * Indicates that the field is to be disabled, that is, the user
     * won't be able to change it's value and it wont be submited.
     * 
     * @property
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * The max length of the field's input.
     * 
     * @property
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    /**
     * Indicates that the field is read only. The user cannot change
     * the field's value but the field is submited.
     * 
     * @property
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getSize() {
        return size;
    }

    /**
     * The size of the field.
     * 
     * @property
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * When the value of the <code>formatText</code> is a key this property indicates
     * the name of the bundle where the key will be looked for.
     * 
     * @property
     */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public boolean isKey() {
        return this.key;
    }

    /**
     * Indicates the the value of the <code>formatText</code> property is
     * a key and not the text itself.
     * 
     * @property
     */
    public void setKey(boolean key) {
        this.key = key;
    }

    public String getFormatText() {
        return this.formatText;
    }

    /**
     * The value that will be appended next to the input text box.
     * 
     * @property
     */
    public void setFormatText(String formatText) {
        this.formatText = formatText;
    }

    public String getBundle() {
        return this.bundle;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new TextFieldLayout();
    }

    protected String getFormatLabel() {
        if (isKey()) {
            return RenderUtils.getResourceString(getBundle(), getFormatText());
        } else if (getFormatText() != null) {
            return getFormatText();
        }
        return StringUtils.EMPTY;
    }

    protected abstract HtmlComponent createTextField(Object object, Class type);

    protected class TextFieldLayout extends Layout {

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            HtmlComponent component = createTextField(object, type);

            InputContext context = getInputContext();
            setContextSlot(component, (MetaSlotKey) context.getMetaObject().getKey());

            return component;
        }

        protected void setContextSlot(HtmlComponent component, MetaSlotKey slotKey) {
            ((HtmlFormComponent) component).setTargetSlot(slotKey);
        }

        @Override
        public void applyStyle(HtmlComponent component) {
            super.applyStyle(component);

            HtmlTextInput textInput = (HtmlTextInput) component;

            textInput.setMaxLength(getMaxLength());
            textInput.setSize(getSize());
            textInput.setReadOnly(getReadOnly());
            textInput.setDisabled(getDisabled());
        }
    }
}
