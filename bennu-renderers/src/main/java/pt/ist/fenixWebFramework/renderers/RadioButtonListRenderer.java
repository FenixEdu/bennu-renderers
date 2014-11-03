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
import pt.ist.fenixWebFramework.renderers.components.HtmlLabel;
import pt.ist.fenixWebFramework.renderers.components.HtmlRadioButton;
import pt.ist.fenixWebFramework.renderers.components.HtmlRadioButtonList;
import pt.ist.fenixWebFramework.renderers.components.converters.BiDirectionalConverter;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectKey;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderMode;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

import com.google.common.base.Strings;

/**
 * This renderer can be used as the input for a list of objects. The list of
 * objects the user can choose will be presented as an html list were each list
 * item will contain the presentation of the object and a radio button that
 * allows to choose that particular object. When submiting, the selected object
 * will be the value passed to the slot.
 * 
 * <p>
 * Example: <form> <input type="radio" name="option"/> <em>&lt;object A presentation&gt;</em><br/>
 * <input type="radio" name="option"/><em>&lt;object B presentation&gt;</em><br/>
 * <input type="radio" name="option"/><em>&lt;object C presentation&gt;</em> </form>
 */
public class RadioButtonListRenderer extends SelectionRenderer {
    private String format;

    private String eachClasses;

    private String eachStyle;

    private String eachSchema;

    private String eachLayout;

    private boolean saveOptions;

    private String nullOptionKey;

    private String nullOptionBundle;

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

    /**
     * This property allows you to configure the class attribute for each
     * object's presentation.
     * 
     * @property
     */
    public void setEachClasses(String classes) {
        this.eachClasses = classes;
    }

    public String getEachClasses() {
        return this.eachClasses;
    }

    /**
     * Allows yout to configure the style attribute for each object's
     * presentation.
     * 
     * @property
     */
    public void setEachStyle(String style) {
        this.eachStyle = style;
    }

    public String getEachStyle() {
        return this.eachStyle;
    }

    public String getEachLayout() {
        return eachLayout;
    }

    /**
     * Allows you to choose the layout in wich each object is to be presented.
     * 
     * @property
     */
    public void setEachLayout(String eachLayout) {
        this.eachLayout = eachLayout;
    }

    public String getEachSchema() {
        return eachSchema;
    }

    /**
     * Allows you to specify the schema that should be used when presenting each
     * individual object.
     * 
     * @property
     */
    public void setEachSchema(String eachSchema) {
        this.eachSchema = eachSchema;
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

    /**
     * Allow to add option null. Need to set the property setNullOptionBundle
     * 
     * @property
     */

    public void setNullOptionKey(String nullOptionKey) {
        this.nullOptionKey = nullOptionKey;
    }

    public String getNullOptionKey() {
        return nullOptionKey;
    }

    public void setNullOptionBundle(String nullOptionLabel) {
        this.nullOptionBundle = nullOptionLabel;
    }

    public String getNullOptionBundle() {
        return nullOptionBundle;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new RadioButtonListLayout();
    }

    class RadioButtonListLayout extends Layout {

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            HtmlRadioButtonList listComponent = new HtmlRadioButtonList();

            Schema schema = RenderKit.getInstance().findSchema(getEachSchema());

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
                MetaObjectKey key = metaObject.getKey();

                String layout = getEachLayout();

                HtmlLabel label = new HtmlLabel();

                if (Strings.isNullOrEmpty(layout)) {
                    if (Enum.class.isAssignableFrom(obj.getClass()) && Strings.isNullOrEmpty(getFormat())) {
                        fillBodyForRadioLabel(metaObject, obj, layout, label);
                    } else {
                        label.setText(getObjectLabel(obj));
                    }
                } else {
                    fillBodyForRadioLabel(metaObject, obj, layout, label);
                }

                label.setStyle(eachStyle);
                label.setClasses(eachClasses);

                String optionValue =
                        getConverter() instanceof BiDirectionalConverter ? ((BiDirectionalConverter) getConverter())
                                .deserialize(obj) : key.toString();

                HtmlRadioButton radioButton = listComponent.addOption(label, optionValue);
                label.setFor(radioButton);

                if (object != null && object.equals(obj)) {
                    radioButton.setChecked(true);
                }
            }

            if (!Strings.isNullOrEmpty(getNullOptionKey())) {
                HtmlLabel label = new HtmlLabel();
                label.setText(RenderUtils.getResourceString(getNullOptionBundle(), getNullOptionKey()));
                HtmlRadioButton addOption = listComponent.addOption(label, null);
                if (object == null) {
                    addOption.setChecked(true);
                }
            }

            if (isSaveOptions()) {
                savePossibleMetaObjects(possibleMetaObjects);
            }

            listComponent.setConverter(new SingleSelectOptionConverter(possibleMetaObjects, getConverter()));
            listComponent.setTargetSlot((MetaSlotKey) getInputContext().getMetaObject().getKey());

            return listComponent;
        }

        private void fillBodyForRadioLabel(MetaObject metaObject, Object obj, String layout, HtmlLabel label) {
            PresentationContext newContext = getContext().createSubContext(metaObject);
            newContext.setLayout(layout);
            newContext.setRenderMode(RenderMode.OUTPUT);

            RenderKit kit = RenderKit.getInstance();
            HtmlComponent component = kit.render(newContext, obj);
            label.setBody(component);
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

        protected String getObjectLabel(Object object) {
            if (getFormat() != null) {
                return RenderUtils.getFormattedProperties(getFormat(), object);
            }
            return String.valueOf(object);
        }
    }
}
