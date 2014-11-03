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
import java.util.Collection;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBox;
import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBoxList;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlLabel;
import pt.ist.fenixWebFramework.renderers.components.HtmlListItem;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectKey;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderMode;

import com.google.common.base.Predicate;

/**
 * This renderer can be used as the input for a list of objects. The list of
 * objects the user can choose will be presented as an html list were each list
 * item will contain the presentation of the object and a checkbox that allows
 * to choose that particular object. When submiting, all the checked objects
 * will be added to a list and that list will be the value passed to the slot.
 * 
 * <p>
 * Example:
 * <ul>
 * <li><input type="checkbox"/><em>&lt;object A presentation&gt;</em></li>
 * <li><input type="checkbox" checked="checked"/> <em>&lt;object B presentation&gt;</em></li>
 * <li><input type="checkbox"/><em>&lt;object C presentation&gt;</em></li>
 * </ul>
 * 
 * @author cfgi
 */
public class CheckBoxOptionListRenderer extends SelectionRenderer {
    private String eachClasses;

    private String eachStyle;

    private String eachSchema;

    private String eachLayout;

    private boolean saveOptions;

    private boolean selectAllShown;

    private String checkBoxClasses;

    private String checkBoxStyle;

    private String listItemClasses;

    private String listItemStyle;

    private boolean ordered;

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

    public boolean isSelectAllShown() {
        return this.selectAllShown;
    }

    /**
     * Makes the renderer add and option that selects and unselects all the
     * remaining options.
     * 
     * @property
     */
    public void setSelectAllShown(boolean selectAllShown) {
        this.selectAllShown = selectAllShown;
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

    public String getCheckBoxClasses() {
        return checkBoxClasses;
    }

    /**
     * Specifies the class applied to the input element
     * 
     * @property
     */
    public void setCheckBoxClasses(String checkBoxClasses) {
        this.checkBoxClasses = checkBoxClasses;
    }

    public String getCheckBoxStyle() {
        return checkBoxStyle;
    }

    /**
     * Specifies the style applied to the input element
     * 
     * @property
     */
    public void setCheckBoxStyle(String checkBoxStyle) {
        this.checkBoxStyle = checkBoxStyle;
    }

    public String getListItemClasses() {
        return listItemClasses;
    }

    /**
     * Specifies the classes applied to the list element
     * 
     * @property
     */
    public void setListItemClasses(String listItemClasses) {
        this.listItemClasses = listItemClasses;
    }

    public String getListItemStyle() {
        return listItemStyle;
    }

    /**
     * Specifies the style applied to the list element
     * 
     * @property
     */
    public void setListItemStyle(String listItemStyle) {
        this.listItemStyle = listItemStyle;
    }

    public boolean isOrdered() {
        return ordered;
    }

    /**
     * Specifies if the generated list will be ordered
     * 
     * @property
     */
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new CheckBoxListLayout();
    }

    protected class CheckBoxListLayout extends Layout {

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            Collection collection = (Collection) object;

            HtmlCheckBoxList listComponent = new HtmlCheckBoxList();
            listComponent.getList().setOrdered(isOrdered());

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

                PresentationContext newContext = getContext().createSubContext(metaObject);
                newContext.setLayout(layout);
                newContext.setRenderMode(RenderMode.OUTPUT);

                RenderKit kit = RenderKit.getInstance();
                HtmlComponent component = kit.render(newContext, obj);

                HtmlLabel label = new HtmlLabel();
                label.setBody(component);
                label.setStyle(eachStyle);
                label.setClasses(eachClasses);

                HtmlCheckBox checkBox = listComponent.addOption(label, key.toString());
                label.setFor(checkBox);
                checkBox.setClasses(getCheckBoxClasses());
                checkBox.setStyle(getCheckBoxStyle());

                if (collection != null && collection.contains(obj)) {
                    checkBox.setChecked(true);
                }
            }

            listComponent.setSelectAllShown(listComponent.getList().getItems().size() > 1 && isSelectAllShown());

            if (isSaveOptions()) {
                savePossibleMetaObjects(possibleMetaObjects);
            }

            List<HtmlComponent> components = listComponent.getChildren(new Predicate<HtmlComponent>() {
                @Override
                public boolean apply(HtmlComponent component) {
                    return component instanceof HtmlListItem;
                }
            });

            for (HtmlComponent component : components) {
                HtmlListItem listItem = (HtmlListItem) component;

                listItem.setStyle(getListItemStyle());
                listItem.setClasses(getListItemClasses());
            }

            // TODO: make providers only provide a converter for a single object
            // make a wrapper converter that calls that converter for each value
            // this allows converters to be used to menus and checkboxes
            listComponent.setConverter(new MultipleSelectOptionConverter(possibleMetaObjects, getConverter()));
            listComponent.setTargetSlot((MetaSlotKey) getInputContext().getMetaObject().getKey());

            return listComponent;
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

    }

}
