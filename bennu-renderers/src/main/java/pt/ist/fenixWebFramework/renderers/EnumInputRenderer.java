package pt.ist.fenixWebFramework.renderers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.Predicate;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenu;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenuOption;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.contexts.InputContext;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.converters.EnumConverter;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.utl.ist.fenix.tools.util.Pair;

/**
 * This renderer presents an html menu with one option for each possible enum
 * value. Each option's label is searched in the bundle <tt>ENUMERATION_RESOURCES</tt> using the enum's name.
 * 
 * <p>
 * Example:<br/>
 * Choose a {@link java.lang.annotation.ElementType element type}: <select> <option>Type</option> <option>Field</option>
 * <option>Parameter</option> <option>Constructor</option> <option>Local Variable</option> <option>Annotation</option>
 * <option>Package</option> </select>
 * 
 * @author cfgi
 */
public class EnumInputRenderer extends InputRenderer {

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

    public String getExcludedValues() {
        return excludedValues;
    }

    /**
     * Excluded Values.
     * 
     * @property
     */
    public void setExcludedValues(String excludedValues) {
        this.excludedValues = excludedValues;
    }

    public String getIncludedValues() {
        return includedValues;
    }

    /**
     * Excluded Values.
     * 
     * @property
     */
    public void setIncludedValues(String includedValues) {
        this.includedValues = includedValues;
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

    @Override
    protected Layout getLayout(final Object object, Class type) {
        if (getReadOnly()) {
            return new Layout() {
                @Override
                public HtmlComponent createComponent(Object targetObject, Class type) {
                    Enum enumerate = (Enum) targetObject;

                    HtmlInlineContainer container = new HtmlInlineContainer();
                    HtmlHiddenField value = new HtmlHiddenField();
                    value.setValue(enumerate.name());
                    value.setConverter(new EnumConverter());
                    container.addChild(value);
                    InputContext context = getInputContext();
                    ((HtmlFormComponent) value).setTargetSlot((MetaSlotKey) context.getMetaObject().getKey());

                    HtmlTextInput component = new HtmlTextInput();
                    component.setValue(RenderUtils.getEnumString(enumerate));
                    container.addChild(component);

                    return container;
                }

                @Override
                public void applyStyle(HtmlComponent component) {
                    HtmlInlineContainer block = (HtmlInlineContainer) component;
                    HtmlTextInput textInput = (HtmlTextInput) block.getChild(new Predicate() {
                        @Override
                        public boolean evaluate(Object elem) {
                            return !(elem instanceof HtmlHiddenField);
                        }
                    });

                    super.applyStyle(textInput);
                    textInput.setReadOnly(true);
                    textInput.setDisabled(getDisabled());
                }
            };
        }
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object targetObject, Class type) {
                Enum enumerate = (Enum) targetObject;

                if (!type.isEnum() && Enum.class.isAssignableFrom(type)) {
                    type = type.getEnclosingClass();
                }

                final Object bean = getRenderedObject();
                Collection<Object> constants = getIncludedEnumValues(type, bean);
                Collection<Object> excludedValues = getExcludedEnumValues(type, bean);
                List<Pair<Enum, String>> pairList = new ArrayList<Pair<Enum, String>>();

                for (Object object : constants) {
                    Enum oneEnum = (Enum) object;
                    pairList.add(new Pair<Enum, String>(oneEnum, RenderUtils.getEnumString(oneEnum, getBundle())));
                }

                if (isSort()) {
                    Collections.sort(pairList, new Comparator<Pair<Enum, String>>() {
                        @Override
                        public int compare(Pair<Enum, String> o1, Pair<Enum, String> o2) {
                            return o1.getValue().compareTo(o2.getValue());
                        }
                    });
                }

                HtmlSimpleValueComponent holderComponent = createInputContainerComponent(enumerate);

                for (Pair<Enum, String> pair : pairList) {

                    Enum oneEnum = pair.getKey();
                    String description = pair.getValue();

                    if (excludedValues.contains(oneEnum)) {
                        continue;
                    }

                    addEnumElement(enumerate, holderComponent, oneEnum, description);
                }

                holderComponent.setConverter(new EnumConverter());
                holderComponent.setTargetSlot((MetaSlotKey) getInputContext().getMetaObject().getKey());

                return holderComponent;
            }

            private Object getRenderedObject() {
                final PresentationContext context = getContext();
                if (context != null) {
                    final PresentationContext parentContext = context.getParentContext();
                    if (parentContext != null) {
                        final MetaObject metaObject = parentContext.getMetaObject();
                        return metaObject == null ? null : metaObject.getObject();
                    }
                }
                return null;
            }

            @Override
            public void applyStyle(HtmlComponent component) {
                super.applyStyle(component);

                HtmlSimpleValueComponent holderComponent = (HtmlSimpleValueComponent) component;
                holderComponent.setDisabled(getDisabled());
            }

        };
    }

    protected void addEnumElement(Enum enumerate, HtmlSimpleValueComponent holder, Enum oneEnum, String description) {
        HtmlMenu menu = (HtmlMenu) holder;

        HtmlMenuOption option = menu.createOption(description);
        option.setValue(oneEnum.toString());

        if (enumerate != null && oneEnum.equals(enumerate)) {
            option.setSelected(true);
        }
    }

    protected HtmlSimpleValueComponent createInputContainerComponent(Enum enumerate) {
        HtmlMenu menu = new HtmlMenu();

        if (!isDefaultOptionHidden()) {
            String defaultOptionTitle = getDefaultTitle();
            menu.createDefaultOption(defaultOptionTitle).setSelected(enumerate == null);
        }

        if (getOnChange() != null && !getOnChange().trim().isEmpty()) {
            menu.setOnChange(getOnChange());
        }

        return menu;
    }

    // TODO: refactor this, probably mode to HtmlMenu, duplicate
    // id=menu.getDefaultTitle
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

    private Collection<Object> getIncludedEnumValues(Class type, final Object object) {
        final String valuesString = getIncludedValues();

        if (valuesString == null || valuesString.length() == 0) {
            Object[] constants = type.getEnumConstants();
            if (constants == null) {
                constants = type.getDeclaringClass().getEnumConstants();
            }

            return Arrays.asList(constants);
        } else {
            String formatedValues = object == null ? valuesString : RenderUtils.getFormattedProperties(valuesString, object);
            return getEnumValues(type, formatedValues);
        }
    }

    private Collection<Object> getExcludedEnumValues(Class type, final Object object) {
        final String valuesString = getExcludedValues();

        if (valuesString == null || valuesString.length() == 0) {
            return Collections.emptyList();
        } else {
            String formatedValues = object == null ? valuesString : RenderUtils.getFormattedProperties(valuesString, object);
            return getEnumValues(type, formatedValues);
        }
    }

    private Collection<Object> getEnumValues(Class type, String valuesString) {
        ArrayList<Object> result = new ArrayList<Object>();
        for (String part : valuesString.split(",")) {
            final String trimmedValue = part.trim();
            if (trimmedValue.length() > 0) {
                result.add(Enum.valueOf(type, trimmedValue));
            }
        }

        return result;
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
}