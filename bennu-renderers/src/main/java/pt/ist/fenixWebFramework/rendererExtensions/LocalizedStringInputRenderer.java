/**
 * Copyright © 2008 Instituto Superior Técnico
 *
 * This file is part of Bennu Renderers Framework.
 *
 * Bennu Renderers Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Renderers Framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Renderers Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixWebFramework.rendererExtensions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixWebFramework.rendererExtensions.validators.LocalizedStringValidator;
import pt.ist.fenixWebFramework.renderers.InputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlActionLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlActionLinkController;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlController;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.validators.ValidatorProperties;

import com.google.common.base.Strings;

/**
 * This renderer provides a generic way of editing slots that contain a {@link MultiLanguageString}. The interface generated
 * allows the user to
 * incrementally add more values in different languages. The user can also
 * remove some of the values already introduced.
 * <p>
 * Example: <div> <div> <input type="text"/> <select> <option selected="selected" value="">-- Choose an option --</option> <option
 * value="eo">Esperanto</option> <option value="xx-klingon">Klingon</option> <option value="xx-piglatin">Pig Latin</option>
 * <option value="xx-elmer">Elmer Fudd</option> </select> <a href="#">Remove</a> </div> <div> <input type="text"/> <select>
 * <option selected="selected" value="">-- Choose an option --</option> <option value="eo">Esperanto</option> <option
 * value="xx-klingon">Klingon</option> <option value="xx-piglatin">Pig Latin</option> <option value="xx-elmer">Elmer Fudd</option>
 * </select> <a href="#">Remove</a> </div> <a href="#">Add</a> </div>
 * 
 * @author cfgi
 */
public class LocalizedStringInputRenderer extends InputRenderer {

    private Integer size;

    private String eachClasses;

    private String inputClasses;

    public Integer getSize() {
        return this.size;
    }

    /**
     * Allows you to configure the size of the input fields for each language.
     * 
     * @property
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    public String getEachClasses() {
        return this.eachClasses;
    }

    /**
     * The classes to apply to the div containing each language line.
     * 
     * @property
     */
    public void setEachClasses(String eachClasses) {
        this.eachClasses = eachClasses;
    }

    public String getInputClasses() {
        return this.inputClasses;
    }

    /**
     * The classes to apply to the input field.
     * 
     * @property
     */
    public void setInputClasses(String inputClasses) {
        this.inputClasses = inputClasses;
    }

    public static class LanguageBean implements Serializable {
        public Locale language;
        public String value;

        public LanguageBean(Locale language, String value) {
            super();

            this.language = language;
            this.value = value;
        }

        public static String exportAsString(LanguageBean languageBean) {
            StringBuilder builder = new StringBuilder();

            if (languageBean != null) {
                builder.append(languageBean.language != null ? languageBean.language.toLanguageTag() : "");
                builder.append(":");
                builder.append(languageBean.value != null ? languageBean.value : "");
            }

            return builder.toString();
        }

        public static String exportAsString(Collection<LanguageBean> languageBeans) {
            StringBuilder builder = new StringBuilder();

            if (languageBeans != null) {
                for (LanguageBean bean : languageBeans) {
                    String beanString = exportAsString(bean);
                    builder.append(beanString.replace("/", "//"));
                    builder.append("/");
                }
            }

            return builder.toString();
        }

        public static LanguageBean importFromString(String value) {
            if (value == null || value.length() == 0) {
                return null;
            }

            int firstIndex = value.indexOf(":");
            if (firstIndex == -1) {
                return null;
            }

            String language = value.substring(0, firstIndex);
            String message = value.substring(firstIndex + 1);

            Locale locale = language.length() == 0 ? null : new Builder().setLanguageTag(language).build();

            return new LanguageBean(locale, message);
        }

        public static Collection<LanguageBean> importAllFromString(String value) {
            Collection<LanguageBean> allLanguageBeans = new ArrayList<LanguageBean>();

            if (value == null || value.length() == 0) {
                return allLanguageBeans;
            }

            int startIndex = 0;
            int lastIndex = 0;
            int index;

            while (lastIndex < value.length()) {
                index = value.indexOf("/", lastIndex);

                if (index == -1) {
                    return allLanguageBeans;
                }

                if (index < value.length() - 1 && value.charAt(index + 1) == '/') {
                    lastIndex = index + 2;
                    continue;
                }

                String part = value.substring(startIndex, index);
                allLanguageBeans.add(importFromString(part.replace("//", "/")));

                lastIndex = index + 1;
                startIndex = lastIndex;
            }

            return allLanguageBeans;
        }
    }

    protected HtmlSimpleValueComponent getInputComponent() {
        HtmlTextInput textInput = new HtmlTextInput();
        textInput.setSize(getSize() == null ? null : String.valueOf(getSize()));

        return textInput;
    }

    protected void configureInputComponent(HtmlSimpleValueComponent textInput) {
    }

    protected void configureLanguageContainer(HtmlContainer languageContainer, HtmlSimpleValueComponent input,
            HtmlSimpleValueComponent languageComponent, HtmlActionLink removeLink) {
        languageContainer.addChild(input);
        languageContainer.addChild(languageComponent);
        languageContainer.addChild(removeLink);
    }

    protected Converter getConverter() {
        return new LocalizedStringConverter();
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        MetaObject metaObject = getInputContext().getMetaObject();

        if (metaObject != null && metaObject instanceof MetaSlot) {
            MetaSlot metaSlot = (MetaSlot) metaObject;

            if (!metaSlot.hasValidator()) {
                Class defaultValidator = LocalizedStringValidator.class;
                metaSlot.setValidators(Collections.singletonList(new ValidatorProperties(defaultValidator, new Properties())));
            }
        }

        return new MultiLanguageStringInputLayout();
    }

    protected HtmlBlockContainer getTopContainer() {
        return new HtmlBlockContainer();
    }

    protected class MultiLanguageStringInputLayout extends Layout {
        private static final String STATE_MAP_NAME = "mlsMap";
        private static final String STATE_INDEX = "lastIndex";

        protected Map<Integer, LanguageBean> getLanguageMap(boolean create) {
            Map<Integer, LanguageBean> map =
                    (Map<Integer, LanguageBean>) getInputContext().getViewState().getLocalAttribute(STATE_MAP_NAME);

            if (map == null && create) {
                map = new Hashtable<Integer, LanguageBean>();
                getInputContext().getViewState().setLocalAttribute(STATE_MAP_NAME, map);
            }

            return map;
        }

        protected String getLocalName(String part) {
            return getInputContext().getMetaObject().getKey() + "/" + part;
        }

        protected Integer updateLastIndex(Integer index) {
            Integer lastIndex = (Integer) getInputContext().getViewState().getLocalAttribute(STATE_INDEX);

            if (lastIndex == null || lastIndex < index) {
                lastIndex = index;
                getInputContext().getViewState().setLocalAttribute(STATE_INDEX, index);
            }

            return lastIndex;
        }

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            LocalizedString mls = getLocalized(object);

            MetaSlotKey key = ((MetaSlot) getInputContext().getMetaObject()).getKey();
            HtmlBlockContainer container = getTopContainer();

            // hidden field with real value
            HtmlHiddenField hiddenField = new HtmlHiddenField();
            hiddenField.setTargetSlot(key);
            hiddenField.setController(new MultiLanguageStringController());
            hiddenField.setConverter(getConverter());
            container.addChild(hiddenField);

            // add link
            HtmlActionLink addLink = new HtmlActionLink(RenderUtils.getResourceString("renderers.language.add"));
            addLink.setName(getLocalName("add"));
            container.addChild(addLink);

            Map<Integer, LanguageBean> map = getLanguageMap(false);
            if ((map == null || map.isEmpty()) && (mls != null && !mls.isEmpty())) {
                map = getLanguageMap(true);

                int index = 0;
                for (Locale locale : mls.getLocales()) {
                    map.put(index++, new LanguageBean(locale, mls.getContent(locale)));
                }
            }

            HtmlActionLink firstRemoveLink = null;
            HtmlActionLink secondRemoveLink = null;

            if (map != null) {
                List<Map.Entry<Integer, LanguageBean>> list = new ArrayList<Map.Entry<Integer, LanguageBean>>(map.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<Integer, LanguageBean>>() {

                    @Override
                    public int compare(Entry<Integer, LanguageBean> o1, Entry<Integer, LanguageBean> o2) {
                        return o1.getKey().compareTo(o2.getKey());
                    }

                });

                for (Map.Entry<Integer, LanguageBean> entry : list) {
                    HtmlActionLink link =
                            addLanguageInput(container, entry.getKey(), entry.getValue().value, entry.getValue().language,
                                    list.size() > 1);

                    if (firstRemoveLink == null) {
                        firstRemoveLink = link;
                    } else if (secondRemoveLink == null) {
                        secondRemoveLink = link;
                    }
                }
            } else {
                // default: present one entry without allowing to remove
                addLanguageInput(container, 0, "", null, false);
            }

            // setup controllers to avoid displaying the remove link when only
            // one line is present
            addLink.setController(new AddNewLanguageController(container, firstRemoveLink));
            if (map != null && map.size() == 2) {
                ((RemoveLanguageController) firstRemoveLink.getController()).setLink(secondRemoveLink);
                ((RemoveLanguageController) secondRemoveLink.getController()).setLink(firstRemoveLink);
            }

            return container;
        }

        private HtmlActionLink addLanguageInput(HtmlContainer container, Integer index, String value, Locale locale,
                boolean allowRemove) {
            // insert empty entry if not present
            Map<Integer, LanguageBean> map = getLanguageMap(true);
            if (!map.containsKey(index)) {
                map.put(index, new LanguageBean(null, null));
            }

            updateLastIndex(index);

            // create component line
            HtmlContainer inputContainer = new HtmlBlockContainer();
            inputContainer.setClasses(getEachClasses());

            HtmlSimpleValueComponent textInput = getInputComponent();
            textInput.setClasses(getInputClasses());

            textInput.setName(getLocalName("text/" + index));
            textInput.setValue(value);

            configureInputComponent(textInput);

            PresentationContext context = getInputContext().createSubContext(getInputContext().getMetaObject());
            context.setProperties(new Properties());

            Locale usedLocale = locale == null ? I18N.getLocale() : locale;

            HtmlSimpleValueComponent languageComponent =
                    (HtmlSimpleValueComponent) RenderKit.getInstance().render(context, usedLocale, Locale.class);
            languageComponent.setController(new UpdateLanguageController(textInput, index));
            languageComponent.setTargetSlot(null);
            languageComponent.setName(getLocalName("language/" + index));

            HtmlActionLink removeLink = new HtmlActionLink(RenderUtils.getResourceString("renderers.language.remove"));
            removeLink.setVisible(allowRemove);
            removeLink.setName(getLocalName("remove/" + index));
            removeLink.setController(new RemoveLanguageController(container, inputContainer, index));

            configureLanguageContainer(inputContainer, textInput, languageComponent, removeLink);

            container.getChildren().add(container.getChildren().size() - 2, inputContainer);

            return removeLink;
        }

        private class MultiLanguageStringController extends HtmlController {
            @Override
            public void execute(IViewState viewState) {
                String value = null;
                Map<Integer, LanguageBean> map = getLanguageMap(false);

                if (map != null && map.size() > 0) {
                    value = LanguageBean.exportAsString(map.values());
                }

                HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getControlledComponent();
                component.setValue(value == null ? null : value.toString());
            }
        }

        private class UpdateLanguageController extends HtmlController {
            private final HtmlSimpleValueComponent input;
            private final Integer index;

            private UpdateLanguageController(HtmlSimpleValueComponent textInput, Integer index) {
                super();

                this.input = textInput;
                this.index = index;
            }

            @Override
            public void execute(IViewState viewState) {
                Map<Integer, LanguageBean> map = getLanguageMap(true);

                HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getControlledComponent();

                String value = this.input.getValue();
                Locale locale =
                        Strings.isNullOrEmpty(component.getValue()) ? null : new Builder().setLanguageTag(component.getValue())
                                .build();

                map.put(this.index, new LanguageBean(locale, value));
            }
        }

        private class AddNewLanguageController extends HtmlActionLinkController {

            private final HtmlBlockContainer container;
            private final HtmlActionLink link;

            public AddNewLanguageController(HtmlBlockContainer container, HtmlActionLink link) {
                this.container = container;
                this.link = link;
            }

            @Override
            public void linkPressed(IViewState viewState, HtmlActionLink link) {
                viewState.setSkipValidation(true);

                Integer index = updateLastIndex(0);
                updateLastIndex(index++);

                if (this.link != null) {
                    this.link.setVisible(true);
                }

                addLanguageInput(this.container, index, "", null, true);
            }

        }

        private class RemoveLanguageController extends HtmlActionLinkController {

            private final HtmlContainer container;
            private final HtmlContainer inputContainer;
            private HtmlActionLink link;
            private final Integer index;

            public RemoveLanguageController(HtmlContainer container, HtmlContainer inputContainer, Integer index) {
                this.container = container;
                this.inputContainer = inputContainer;
                this.index = index;
            }

            public void setLink(HtmlActionLink link) {
                this.link = link;
            }

            @Override
            public void linkPressed(IViewState viewState, HtmlActionLink link) {
                viewState.setSkipValidation(true);

                this.container.removeChild(this.inputContainer);
                if (this.link != null) {
                    this.link.setVisible(false);
                }

                Map<Integer, LanguageBean> map = getLanguageMap(true);
                map.remove(this.index);
            }

        }

    }

    protected LocalizedString getLocalized(Object object) {
        if (object instanceof LocalizedString) {
            return (LocalizedString) object;
        } else {
            return null;
        }
    }

    public static class LocalizedStringConverter extends Converter {

        @Override
        public Object convert(Class type, Object value) {
            String text = (String) value;

            LocalizedString mls = new LocalizedString();

            Collection<LanguageBean> allLanguageBean = LanguageBean.importAllFromString(text);
            for (LanguageBean bean : allLanguageBean) {
                if (bean.value != null && bean.value.trim().length() != 0) {
                    mls = mls.with(bean.language, bean.value);
                }
            }

            return processLocalized(mls);
        }

        protected Object processLocalized(LocalizedString mls) {
            return mls;
        }

    }

}
