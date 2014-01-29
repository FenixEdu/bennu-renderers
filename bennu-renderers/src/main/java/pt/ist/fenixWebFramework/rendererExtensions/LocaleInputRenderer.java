package pt.ist.fenixWebFramework.rendererExtensions;

import java.util.Locale;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.I18N;

import pt.ist.fenixWebFramework.renderers.InputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenu;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenuOption;
import pt.ist.fenixWebFramework.renderers.converters.LocaleConverter;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class LocaleInputRenderer extends InputRenderer {

    private String defaultText;

    private String defaultTextBundle;

    private boolean defaultOptionHidden = false;

    private String bundle;

    private boolean key;

    private String excludedValues;

    private String includedValues;

    private boolean sort;

    private boolean readOnly;

    private boolean disabled;

    private String onChange;

    public String getBundle() {
        return this.bundle;
    }

    /**
     * The bundle used if <code>key</code> is <code>true</code>
     * 
     * @property
     */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getDefaultText() {
        return this.defaultText;
    }

    /**
     * The text or key of the default menu title.
     * 
     * @property
     */
    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public boolean isDefaultOptionHidden() {
        return defaultOptionHidden;
    }

    /**
     * Don't show the default option, that is, the options meaning no value
     * selected.
     * 
     * @property
     */
    public void setDefaultOptionHidden(boolean defaultOptionHidden) {
        this.defaultOptionHidden = defaultOptionHidden;
    }

    public boolean isKey() {
        return this.key;
    }

    /**
     * Indicates the the default text is a key to a resource bundle.
     * 
     * @property
     */
    public void setKey(boolean key) {
        this.key = key;
    }

    /**
     * 
     * @return Javascript code to be executed when the value is changed.
     */
    public String getOnChange() {
        return onChange;
    }

    public void setOnChange(String onChange) {
        this.onChange = onChange;
    }

    private String getDefaultTitle() {
        if (getDefaultText() == null) {
            return RenderUtils.getResourceString("renderers.menu.default.title");
        } else {
            if (isKey()) {
                return RenderUtils.getResourceString(getDefaultTextBundle(), getDefaultText());
            } else {
                return getDefaultText();
            }
        }
    }

    public boolean isSort() {
        return sort;
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getDefaultTextBundle() {
        return defaultTextBundle;
    }

    public void setDefaultTextBundle(String defaultTextBundle) {
        this.defaultTextBundle = defaultTextBundle;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {
            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                Locale selected = (Locale) object;
                HtmlMenu menu = new HtmlMenu();

                if (!isDefaultOptionHidden()) {
                    String defaultOptionTitle = getDefaultTitle();
                    menu.createDefaultOption(defaultOptionTitle).setSelected(selected == null);
                }

                if (getOnChange() != null && !getOnChange().trim().isEmpty()) {
                    menu.setOnChange(getOnChange());
                }

                for (Locale locale : CoreConfiguration.supportedLocales()) {
                    HtmlMenuOption option = menu.createOption(locale.getDisplayLanguage(I18N.getLocale()));
                    option.setValue(locale.toLanguageTag());

                    if (selected != null && locale.equals(selected)) {
                        option.setSelected(true);
                    }
                }

                menu.setConverter(new LocaleConverter());
                menu.setTargetSlot((MetaSlotKey) getInputContext().getMetaObject().getKey());

                return menu;
            }

            @Override
            public void applyStyle(HtmlComponent component) {
                super.applyStyle(component);

                HtmlFormComponent holderComponent = (HtmlFormComponent) component;
                holderComponent.setDisabled(getDisabled());
            }
        };
    }
}
