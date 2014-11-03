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
package pt.ist.fenixWebFramework.renderers;

import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenu;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenuOption;
import pt.ist.fenixWebFramework.renderers.components.converters.BiDirectionalConverter;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderMode;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

import com.google.common.base.Strings;

/**
 * This renderer as a purpose similar to {@link pt.ist.fenixWebFramework.renderers.CheckBoxOptionListRenderer} but is
 * intended to collect only one value. All the possible values for the slot
 * beeing edited are presented in an html menu. The presentation of each object
 * must have in consideration that the object is beeing presented in an option
 * of the menu so it must be short and simple. If possible used the <tt>format</tt> property to format the object. Nevertheless
 * the usual
 * configuration is possible with <tt>eachLayout</tt> and <tt>eachSchema</tt>.
 * 
 * <p>
 * Example: <select> <option>&lt;object A presentation&gt;</option> <option>&lt;object B presentation&gt;</option>
 * <option>&lt;object C presentation&gt;</option> </select>
 * 
 * @author cfgi
 */
public class MenuOptionListRenderer extends SelectionRenderer {
    private String format;

    private String eachSchema;

    private String eachLayout;

    private boolean saveOptions;

    private boolean nullOptionHidden;
    private String defaultText;
    private String bundle;
    private boolean key;

    public String getFormat() {
        return this.format;
    }

    /**
     * This allows to specify a presentation format for each object. For more
     * details about the format syntaxt check the {@see FormatRenderer}.
     * 
     * @property
     */
    public void setFormat(String format) {
        this.format = format;
    }

    public String getEachLayout() {
        return this.eachLayout;
    }

    /**
     * The layout to be used when presenting each object. This property will
     * only be used if {@link #setFormat(String) format} is not specified.
     * 
     * @property
     */
    public void setEachLayout(String eachLayout) {
        this.eachLayout = eachLayout;
    }

    public String getEachSchema() {
        return this.eachSchema;
    }

    /**
     * The schema to be used when presenting each object.
     * 
     * @property
     */
    public void setEachSchema(String eachSchema) {
        this.eachSchema = eachSchema;
    }

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

    public boolean isSaveOptions() {
        return saveOptions;
    }

    /**
     * Allows the possible object list to be persisted between requests, meaning
     * that the provider is invoked only once.
     * 
     * @property
     */
    public void setSaveOptions(boolean saveOptions) {
        this.saveOptions = saveOptions;
    }

    public boolean isNullOptionHidden() {
        return this.nullOptionHidden;
    }

    /**
     * Don't show the default option, that is, the options meaning no value
     * selected.
     * 
     * @property
     */
    public void setNullOptionHidden(boolean nullOptionHidden) {
        this.nullOptionHidden = nullOptionHidden;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new MenuOptionLayout();
    }

    class MenuOptionLayout extends Layout {

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            HtmlMenu menu = new HtmlMenu();

            if (!isNullOptionHidden()) {
                String defaultOptionTitle = getDefaultTitle();
                menu.createDefaultOption(defaultOptionTitle).setSelected(object == null);
            }

            RenderKit kit = RenderKit.getInstance();
            Schema schema = kit.findSchema(getEachSchema());

            List<MetaObject> possibleMetaObjects;

            if (hasSavedPossibleMetaObjects()) {
                possibleMetaObjects = getPossibleMetaObjects();
            } else {
                possibleMetaObjects = new ArrayList<MetaObject>();
                for (Object possibility : getPossibleObjects()) {
                    possibleMetaObjects.add(MetaObjectFactory.createObject(possibility, schema));
                }
            }

            for (MetaObject metaObject : possibleMetaObjects) {
                Object obj = metaObject.getObject();
                HtmlMenuOption option = menu.createOption(null);

                if (getConverter() instanceof BiDirectionalConverter) {
                    option.setValue(((BiDirectionalConverter) getConverter()).deserialize(obj));
                } else {
                    option.setValue(metaObject.getKey().toString());
                }

                if (Strings.isNullOrEmpty(getEachLayout())) {
                    if (Enum.class.isAssignableFrom(obj.getClass()) && Strings.isNullOrEmpty(getFormat())) {
                        fillBodyWithRenderKit(kit, metaObject, obj, option);
                    } else {
                        option.setText(getObjectLabel(obj));
                    }
                } else {
                    fillBodyWithRenderKit(kit, metaObject, obj, option);
                }

                if (obj.equals(object)) {
                    option.setSelected(true);
                }
            }

            if (isSaveOptions()) {
                savePossibleMetaObjects(possibleMetaObjects);
            }

            menu.setConverter(new SingleSelectOptionConverter(possibleMetaObjects, getConverter()));

            menu.setTargetSlot((MetaSlotKey) getInputContext().getMetaObject().getKey());
            return menu;
        }

        private void fillBodyWithRenderKit(RenderKit kit, MetaObject metaObject, Object obj, HtmlMenuOption option) {
            PresentationContext newContext = getContext().createSubContext(metaObject);
            newContext.setLayout(getEachLayout());
            newContext.setRenderMode(RenderMode.OUTPUT);

            HtmlComponent component = kit.render(newContext, obj);
            option.setBody(component);
        }

        private boolean hasSavedPossibleMetaObjects() {
            return getInputContext().getViewState().getLocalAttribute("options") != null;
        }

        private List<MetaObject> getPossibleMetaObjects() {
            return (List<MetaObject>) getInputContext().getViewState().getLocalAttribute("options");
        }

        private void savePossibleMetaObjects(List<MetaObject> possibleMetaObjects) {
            getInputContext().getViewState().setLocalAttribute("options", possibleMetaObjects);
        }

        // TODO: duplicate code, id=menu.getDefaultTitle
        private String getDefaultTitle() {
            if (getDefaultText() == null) {
                return RenderUtils.getResourceString("renderers.menu.default.title");
            }
            if (isKey()) {
                return RenderUtils.getResourceString(getBundle(), getDefaultText());
            }
            return getDefaultText();
        }

        protected String getObjectLabel(Object object) {
            if (getFormat() != null) {
                return RenderUtils.getFormattedProperties(getFormat(), object);
            }
            return String.valueOf(object);
        }
    }
}
