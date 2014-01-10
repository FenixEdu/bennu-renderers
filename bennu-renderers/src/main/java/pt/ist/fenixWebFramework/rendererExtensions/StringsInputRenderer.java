package pt.ist.fenixWebFramework.rendererExtensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

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
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.utl.ist.fenix.tools.util.Strings;

/**
 * This renderer provides a generic way of editing slots that contain a {@link Strings}. The interface generated allows the user
 * to incrementally add
 * more string lines. The user can also remove some of the lines already
 * introduced.
 * <p>
 * Example: <div> <div> <input type="text"/> <a href="#">Remove</a> </div> <div> <input type="text"/> <a href="#">Remove</a>
 * </div> <a href="#">Add</a> </div>
 */
public class StringsInputRenderer extends InputRenderer {

    private Integer size;

    private String eachClasses;

    private String inputClasses;

    public Integer getSize() {
        return this.size;
    }

    /**
     * Allows you to configure the size of the input fields for each line.
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

    protected HtmlSimpleValueComponent getInputComponent() {
        HtmlTextInput textInput = new HtmlTextInput();
        textInput.setSize(getSize() == null ? null : String.valueOf(getSize()));

        return textInput;
    }

    protected void configureStringsContainer(HtmlContainer stringsContainer, HtmlSimpleValueComponent input,
            HtmlActionLink removeLink) {
        stringsContainer.addChild(input);
        stringsContainer.addChild(removeLink);
    }

    protected Converter getConverter() {
        return new StringsConverter();
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new StringsInputLayout();
    }

    protected HtmlBlockContainer getTopContainer() {
        return new HtmlBlockContainer();
    }

    protected class StringsInputLayout extends Layout {
        private static final String STATE_MAP_NAME = "stringsMap";
        private static final String STATE_INDEX = "lastIndex";

        protected Map<Integer, String> getStringsMap(boolean create) {
            Map<Integer, String> map = (Map<Integer, String>) getInputContext().getViewState().getLocalAttribute(STATE_MAP_NAME);

            if (map == null && create) {
                map = new TreeMap<Integer, String>();
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
            Strings strings = (Strings) object;

            MetaSlotKey key = ((MetaSlot) getInputContext().getMetaObject()).getKey();
            HtmlBlockContainer container = getTopContainer();

            // hidden field with real value
            HtmlHiddenField hiddenField = new HtmlHiddenField();
            hiddenField.setTargetSlot(key);
            hiddenField.setController(new StringsController());
            hiddenField.setConverter(getConverter());
            container.addChild(hiddenField);

            // add link
            HtmlActionLink addLink = new HtmlActionLink(RenderUtils.getResourceString("renderers.strings.add"));
            addLink.setName(getLocalName("add"));
            container.addChild(addLink);

            Map<Integer, String> map = getStringsMap(false);
            if ((map == null || map.isEmpty()) && (strings != null && !strings.isEmpty())) {
                map = getStringsMap(true);

                int index = 0;
                for (String string : strings.getUnmodifiableList()) {
                    map.put(index++, string);
                }
            }

            HtmlActionLink firstRemoveLink = null;
            HtmlActionLink secondRemoveLink = null;

            if (map != null) {
                List<Map.Entry<Integer, String>> list = new ArrayList<Map.Entry<Integer, String>>(map.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<Integer, String>>() {
                    @Override
                    public int compare(Entry<Integer, String> entry1, Entry<Integer, String> entry2) {
                        return entry1.getKey().compareTo(entry2.getKey());
                    }
                });

                for (Map.Entry<Integer, String> entry : list) {
                    HtmlActionLink link = addStringInput(container, entry.getKey(), entry.getValue(), list.size() > 1);

                    if (firstRemoveLink == null) {
                        firstRemoveLink = link;
                    } else if (secondRemoveLink == null) {
                        secondRemoveLink = link;
                    }
                }
            } else {
                // default: present one entry without allowing to remove
                addStringInput(container, 0, "", false);
            }

            // setup controllers to avoid displaying the remove link when only
            // one line is present
            addLink.setController(new AddNewStringController(container, firstRemoveLink));
            if (map != null && map.size() == 2) {
                ((RemoveStringController) firstRemoveLink.getController()).setLink(secondRemoveLink);
                ((RemoveStringController) secondRemoveLink.getController()).setLink(firstRemoveLink);
            }

            return container;
        }

        private HtmlActionLink addStringInput(HtmlContainer container, Integer index, String value, boolean allowRemove) {
            // insert empty entry if not present
            Map<Integer, String> map = getStringsMap(true);
            if (!map.containsKey(index)) {
                map.put(index, "");
            }

            updateLastIndex(index);

            // create component line
            HtmlContainer inputContainer = new HtmlBlockContainer();
            inputContainer.setClasses(getEachClasses());

            HtmlSimpleValueComponent textInput = getInputComponent();
            textInput.setClasses(getInputClasses());

            textInput.setName(getLocalName("text/" + index));
            textInput.setValue(value);
            textInput.setController(new UpdateStringController(index));

            PresentationContext context = getInputContext().createSubContext(getInputContext().getMetaObject());
            context.setProperties(new Properties());

            HtmlActionLink removeLink = new HtmlActionLink(RenderUtils.getResourceString("renderers.language.remove"));
            removeLink.setVisible(allowRemove);
            removeLink.setName(getLocalName("remove/" + index));
            removeLink.setController(new RemoveStringController(container, inputContainer, index));

            configureStringsContainer(inputContainer, textInput, removeLink);

            container.getChildren().add(container.getChildren().size() - 2, inputContainer);

            return removeLink;
        }

        private class StringsController extends HtmlController {
            @Override
            public void execute(IViewState viewState) {
                String value = null;
                Map<Integer, String> map = getStringsMap(false);

                if (map != null && map.size() > 0) {
                    Strings strings = new Strings(map.values());
                    value = strings.exportAsString();
                }

                HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getControlledComponent();
                component.setValue(value);
            }
        }

        private class UpdateStringController extends HtmlController {
            private final Integer index;

            private UpdateStringController(Integer index) {
                super();
                this.index = index;
            }

            @Override
            public void execute(IViewState viewState) {
                Map<Integer, String> map = getStringsMap(true);

                HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) getControlledComponent();
                map.put(this.index, component.getValue());
            }
        }

        private class AddNewStringController extends HtmlActionLinkController {

            private final HtmlBlockContainer container;
            private final HtmlActionLink link;

            public AddNewStringController(HtmlBlockContainer container, HtmlActionLink link) {
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

                addStringInput(this.container, index, "", true);
            }

        }

        private class RemoveStringController extends HtmlActionLinkController {

            private final HtmlContainer container;
            private final HtmlContainer inputContainer;
            private HtmlActionLink link;
            private final Integer index;

            public RemoveStringController(HtmlContainer container, HtmlContainer inputContainer, Integer index) {
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

                Map<Integer, String> map = getStringsMap(true);
                map.remove(this.index);
            }
        }
    }

    public static class StringsConverter extends Converter {
        @Override
        public Object convert(Class type, Object value) {
            return Strings.importFromString((String) value);
        }
    }
}
