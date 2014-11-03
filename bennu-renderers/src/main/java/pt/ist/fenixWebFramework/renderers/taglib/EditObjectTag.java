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
package pt.ist.fenixWebFramework.renderers.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlForm;
import pt.ist.fenixWebFramework.renderers.components.HtmlHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.components.state.HiddenSlot;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.components.state.LifeCycleConstants;
import pt.ist.fenixWebFramework.renderers.components.state.ViewState;
import pt.ist.fenixWebFramework.renderers.contexts.InputContext;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;
import pt.ist.fenixWebFramework.renderers.validators.RequiredValidator;
import pt.utl.ist.fenix.tools.util.Pair;

public class EditObjectTag extends BaseRenderObjectTag implements ValidatorContainerTag {

    private static final Collection<Class<?>> formTagTypes = new ConcurrentLinkedQueue<>();

    static {
        formTagTypes.add(FormTag.class);
    }

    public static void registerFormTagType(Class<?> type) {
        formTagTypes.add(type);
    }

    private boolean nested;

    private boolean visible;

    private String action;

    private String slot;

    private Map<String, Properties> validators = new HashMap<String, Properties>();

    private String converter;

    private List<HiddenSlot> hiddenSlots;

    private Properties validatorProperties;

    public EditObjectTag() {
        super();

        this.nested = false;
        this.visible = true;

        this.hiddenSlots = new ArrayList<HiddenSlot>();
        this.validatorProperties = new Properties();
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isNested() {
        return this.nested;
    }

    public void setNested(boolean nested) {
        this.nested = nested;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSlot() {
        return this.slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getConverter() {
        return this.converter;
    }

    public void setConverter(String converter) {
        this.converter = converter;
    }

    @Override
    public void addValidator(final String name) {
        this.validators.put(name, new Properties());
    }

    @Override
    public void release() {
        super.release();

        this.nested = false;
        this.visible = true;
        this.action = null;
        this.slot = null;
        this.converter = null;
        this.validators = new HashMap<String, Properties>();
        this.hiddenSlots = new ArrayList<HiddenSlot>();
        this.validatorProperties = new Properties();
    }

    protected boolean isPostBack() {
        return getViewState() != null;
    }

    @Override
    protected Object getTargetObject() throws JspException {
        if (!isPostBack()) {
            return super.getTargetObject();
        } else {
            IViewState viewState = getViewState();
            return viewState.getMetaObject().getObject();
        }
    }

    @Override
    protected PresentationContext createPresentationContext(Object object, String layout, Schema schema, Properties properties) {
        IViewState viewState = createViewState(object);

        if (viewState.getContext() != null) {
            return viewState.getContext();
        } else {
            InputContext context = new InputContext();

            context.setSchema(schema);
            context.setLayout(layout);
            context.setProperties(properties);

            viewState.setContext(context);

            context.setViewState(viewState);

            return context;
        }
    }

    @Override
    protected HtmlComponent renderObject(PresentationContext context, Object object) throws JspException {
        if (!isVisible()) {
            return null;
        }

        if (isPostBack()) {
            return retrieveComponent();
        } else {
            if (getSlot() == null) {
                if (getType() == null) {
                    return RenderKit.getInstance().render(context, object);
                } else {
                    try {
                        Class type = Class.forName(getType());

                        return RenderKit.getInstance().render(context, object, type);
                    } catch (ClassNotFoundException e) {
                        throw new JspException("could not get class named " + getType(), e);
                    }
                }
            } else {
                MetaObject metaObject = context.getMetaObject();
                return RenderKit.getInstance().render(context, metaObject.getObject(), metaObject.getType());
            }
        }
    }

    @Override
    protected boolean isNullAccepted() {
        return getSlot() != null;
    }

    protected HtmlComponent retrieveComponent() {
        return getViewState().getComponent();
    }

    @Override
    protected void drawComponent(PresentationContext context, HtmlComponent component) throws JspException, IOException {
        InputContext inputContext = (InputContext) context;
        IViewState viewState = inputContext.getViewState();

        HtmlComponent componentToDraw;

        List<HtmlHiddenField> hiddenFields = new ArrayList<HtmlHiddenField>();
        for (HiddenSlot slot : this.hiddenSlots) {
            if (getSlot() != null) {
                MetaSlot metaSlot = (MetaSlot) viewState.getMetaObject();
                slot.setKey(metaSlot.getKey());
            }

            for (String value : slot.getValues()) {
                HtmlHiddenField field = new HtmlHiddenField(slot.getName(), value);
                field.setTargetSlot(slot.getKey());

                hiddenFields.add(field);
            }

            viewState.addHiddenSlot(slot);
        }

        if (hasParentForm()) {
            addViewStateToParentForm(viewState);
        } else {
            HtmlHiddenField htmlViewStateField =
                    new HtmlHiddenField(LifeCycleConstants.VIEWSTATE_PARAM_NAME, ViewState.encodeToBase64(Collections
                            .singletonList(viewState)));
            hiddenFields.add(htmlViewStateField);
        }

        if (isNested() || hasFormParent()) {
            if (hiddenFields != null && !hiddenFields.isEmpty()) {
                HtmlBlockContainer container = new HtmlBlockContainer();
                container.setClasses("dinline");
                for (HtmlHiddenField field : hiddenFields) {
                    container.addChild(field);
                }

                if (component != null) {
                    container.addChild(component);
                }

                componentToDraw = container;
            } else {
                componentToDraw = component == null ? new HtmlText() : component;
            }
        } else {
            HtmlForm form = inputContext.getForm();

            form.setId(getId());
            form.setAction(getActionPath());
            form.setMethod(HtmlForm.POST);

            form.setBody(component);

            for (HtmlHiddenField field : hiddenFields) {
                form.addHiddenField(field);
            }

            componentToDraw = form;
        }

        componentToDraw.draw(pageContext);
    }

    private boolean hasFormParent() {
        for (Class<?> type : formTagTypes) {
            if (findAncestorWithClass(this, type) != null) {
                return true;
            }
        }

        return false;
    }

    protected void addViewStateToParentForm(IViewState viewState) {
        ContextTag parent = (ContextTag) findAncestorWithClass(this, ContextTag.class);
        parent.addViewState(viewState);
    }

    protected boolean hasParentForm() {
        return findAncestorWithClass(this, ContextTag.class) != null;
    }

    protected String getActionPath() {
        HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();

        String action = getAction();
        if (action == null) {
            action = getCurrentPath();
        }

        String actionMappingURL = RenderUtils.getActionMappingURL(action, pageContext);
        return response.encodeURL(actionMappingURL);
    }

    protected IViewState getViewState() {
        List<IViewState> viewStates = (List<IViewState>) pageContext.findAttribute(LifeCycleConstants.VIEWSTATE_PARAM_NAME);

        if (viewStates == null) {
            return null;
        }

        for (IViewState viewState : viewStates) {
            if (getId() == null) {
                if (viewState.getId() != null) {
                    continue;
                }

                if (!getInputDestination().equals(viewState.getInputDestination())) {
                    continue;
                }
            } else if (!getId().equals(viewState.getId())) {
                continue;
            }

            if (getSlot() != null) {
                if (!(viewState.getMetaObject() instanceof MetaSlot)) {
                    continue;
                }

                MetaSlot metaSlot = (MetaSlot) viewState.getMetaObject();

                if (!metaSlot.getName().equals(getSlot())) {
                    continue;
                }
            }

            return viewState;
        }

        return null;
    }

    protected IViewState createViewState(Object targetObject) {
        IViewState viewState;

        if (isPostBack()) {
            viewState = getViewState();
            viewState.setVisible(isVisible());

            MetaObject metaObject = viewState.getMetaObject();

            if (metaObject instanceof MetaSlot) {
                updateHiddenSlots(((MetaSlot) metaObject).getMetaObject());
            } else {
                updateHiddenSlots(metaObject);
            }
        } else {
            viewState = new ViewState(getId());
            viewState.setVisible(isVisible());

            viewState.setLayout(getLayout());
            viewState.setProperties(getRenderProperties());
            viewState.setRequest((HttpServletRequest) pageContext.getRequest());

            Schema schema = getComputedSchema(targetObject);
            MetaObject metaObject = createMetaObject(targetObject, schema);
            viewState.setMetaObject(metaObject);

            setViewStateDestinations(viewState);
        }

        return viewState;
    }

    protected Schema getComputedSchema(Object targetObject) {
        if (getSlot() == null && isVisible()) {
            return getRealSchema();
        } else {
            Schema schema = new Schema(null, targetObject.getClass());

            if (isVisible()) {
                SchemaSlotDescription slotDescription = new SchemaSlotDescription(getSlot());

                slotDescription.setValidators(getValidatorsClass());
                slotDescription.setConverter(getConverterClass());

                schema.addSlotDescription(slotDescription);
            }

            return schema;
        }
    }

    protected Properties getValidatorProperties() {
        return this.validatorProperties;
    }

    protected Class<Converter> getConverterClass() {
        String converterName = getConverter();

        if (converterName == null) {
            return null;
        }

        try {
            return (Class<Converter>) Class.forName(converterName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("specified converter does not exist: " + converterName, e);
        }
    }

    protected List<Pair<Class<HtmlValidator>, Properties>> getValidatorsClass() {
        List<Pair<Class<HtmlValidator>, Properties>> res = new ArrayList<Pair<Class<HtmlValidator>, Properties>>();
        Map<String, Properties> validatorsMap = getValidators();

        for (Entry<String, Properties> entry : getValidators().entrySet()) {
            try {
                Class<HtmlValidator> validatorClass = (Class<HtmlValidator>) Class.forName(entry.getKey());
                res.add(new Pair<Class<HtmlValidator>, Properties>(validatorClass, entry.getValue()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("specified validator does not exist: " + entry.getKey(), e);
            }
        }
        return res;
    }

    private void updateHiddenSlots(MetaObject metaObject) {
        for (HiddenSlot hiddenSlot : this.hiddenSlots) {
            for (MetaSlot slot : metaObject.getHiddenSlots()) {
                if (slot.getName().equals(hiddenSlot.getName())) {
                    hiddenSlot.setKey(slot.getKey());
                }
            }
        }
    }

    protected MetaObject createMetaObject(Object targetObject, Schema schema) {
        MetaObject metaObject = getNewMetaObject(targetObject, schema);

        for (HiddenSlot slot : this.hiddenSlots) {
            MetaSlot metaSlot = metaObject.getSlot(slot.getName());

            if (metaSlot == null) {
                SchemaSlotDescription slotDescription = new SchemaSlotDescription(slot.getName());
                slotDescription.setConverter(slot.getConverter());

                metaSlot = MetaObjectFactory.createSlot(metaObject, slotDescription);
                metaObject.addHiddenSlot(metaSlot);
            } else {
                metaSlot.setConverter(slot.getConverter());
            }

            slot.setKey(metaSlot.getKey());
        }

        // are we editing a single slot or the entire object
        if (getSlot() == null) {
            return metaObject;
        } else {
            MetaSlot metaSlot = metaObject.getSlot(getSlot());

            if (metaSlot != null) {
                return metaSlot;
            }

            throw new RuntimeException("specified slot '" + getSlot() + "' does not exist in object " + targetObject);
        }
    }

    protected MetaObject getNewMetaObject(Object targetObject, Schema schema) {
        return MetaObjectFactory.createObject(targetObject, schema);
    }

    public void addHiddenSlot(String slot, boolean multiple, String value, Class<Converter> converter) {
        if (getSlot() != null) {
            setVisible(false);
        }

        HiddenSlot hiddenSlot = getHiddenSlot(slot);

        if (hiddenSlot == null) {
            if (getSlot() != null && this.hiddenSlots.size() > 0) {
                throw new RuntimeException("can only pass hidden values to slot '" + getSlot() + "'");
            }

            hiddenSlot = new HiddenSlot(slot, value, converter);
            this.hiddenSlots.add(hiddenSlot);
        } else {
            hiddenSlot.addValue(value);
        }

        hiddenSlot.setConverter(converter);
        hiddenSlot.setMultiple(hiddenSlot.isMultiple() || multiple);
    }

    public HiddenSlot getHiddenSlot(String name) {
        for (HiddenSlot slot : this.hiddenSlots) {
            if (slot.getName().equals(name)) {
                return slot;
            }
        }

        return null;
    }

    @Override
    public void addValidatorProperty(String validator, String name, String value) {
        this.validators.get(validator).setProperty(name, value);
    }

    protected Map<String, Properties> getValidators() {
        return validators;
    }

    protected void setValidators(Map<String, Properties> validators) {
        this.validators = validators;
    }

    public void setValidator(String validatorName) {
        this.validators.clear();
        this.validators.put(validatorName, new Properties());
    }

    public boolean isRequired() {
        return validators.containsKey(RequiredValidator.class.getName());
    }

    public void setRequired(boolean required) {
        if (required) {
            validators.put(RequiredValidator.class.getName(), new Properties());
        }
    }
}
