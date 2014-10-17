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
package pt.ist.fenixWebFramework.renderers.components.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixWebFramework.renderers.components.Constants;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlMultipleHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlMultipleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.controllers.Controllable;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlController;
import pt.ist.fenixWebFramework.renderers.contexts.InputContext;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectCollection;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;

import com.google.common.base.Predicate;

public class ComponentLifeCycle {
    private static final Logger logger = LoggerFactory.getLogger(ComponentLifeCycle.class);

    //
    // Utility classes
    //

    private class ComponentCollector {

        private final List<HtmlFormComponent> formComponents;

        private final List<HtmlController> controllers;

        public ComponentCollector(IViewState viewState, HtmlComponent component) {
            this.formComponents = new ArrayList<HtmlFormComponent>();
            this.controllers = new ArrayList<HtmlController>();

            if (component != null) {
                collect(component);

                InputContext context = (InputContext) viewState.getContext();
                if (context != null) {
                    collect(context.getForm().getSubmitButton());
                    collect(context.getForm().getCancelButton());
                }

                addHiddenComponents(viewState);
            }
        }

        private void addHiddenComponents(IViewState viewState) {
            // add hidden fields that were rendered by the framework
            for (HiddenSlot hiddenSlot : viewState.getHiddenSlots()) {
                HtmlFormComponent hiddenField;

                if (hiddenSlot.isMultiple()) {
                    hiddenField = new HtmlMultipleHiddenField(hiddenSlot.getName());
                } else {
                    hiddenField = new HtmlHiddenField(hiddenSlot.getName(), null);
                }

                hiddenField.setTargetSlot(hiddenSlot.getKey());
                this.formComponents.add(hiddenField);
            }
        }

        public List<HtmlFormComponent> getFormComponents() {
            return this.formComponents;
        }

        public List<HtmlController> getControllers() {
            return this.controllers;
        }

        private void collect(HtmlComponent component) {
            Predicate<HtmlComponent> isFormComponent = new Predicate<HtmlComponent>() {
                @Override
                public boolean apply(HtmlComponent component) {
                    if (component instanceof HtmlFormComponent) {
                        HtmlFormComponent formComponent = (HtmlFormComponent) component;

                        if (formComponent.getName() != null) {
                            return true;
                        }
                    }

                    return false;
                }
            };

            List<HtmlComponent> components = HtmlFormComponent.getComponents(component, isFormComponent);
            for (HtmlComponent comp : components) {
                this.formComponents.add((HtmlFormComponent) comp);
            }

            Predicate<HtmlComponent> hasController = new Predicate<HtmlComponent>() {
                @Override
                public boolean apply(HtmlComponent component) {
                    if (component instanceof Controllable) {
                        Controllable controllabelComponent = (Controllable) component;

                        if (controllabelComponent.hasController()) {
                            return true;
                        }
                    }

                    return false;
                }
            };

            components = HtmlFormComponent.getComponents(component, hasController);
            for (HtmlComponent comp : components) {
                this.controllers.add(((Controllable) comp).getController());
            }
        }
    }

    //
    // Main
    //

    private static ComponentLifeCycle instance = new ComponentLifeCycle();

    public static ComponentLifeCycle getInstance() {
        return ComponentLifeCycle.instance;
    }

    public static ViewDestination execute(HttpServletRequest request) throws Exception {
        return instance.doLifeCycle(request);
    }

    public ViewDestination doLifeCycle(HttpServletRequest request) throws Exception {

        EditRequest editRequest = new EditRequest(request);
        List<IViewState> viewStates = editRequest.getAllViewStates();
        List<ViewStateHolder> viewStateHolders = new ArrayList<ViewStateHolder>();

        boolean allValid = true;
        boolean anySkip = false;
        boolean anyCanceled = false;
        boolean skipValidation = false;

        skipValidation = Boolean.parseBoolean(request.getParameter("skipValidation"));

        for (IViewState viewState : viewStates) {
            ViewStateHolder holder = new ViewStateHolder(viewState);
            viewStateHolders.add(holder);

            if (cancelRequested(editRequest)) {
                doCancel(viewState);
                anyCanceled = true;
                holder.setCanceled(true);
                continue;
            }

            HtmlComponent component = restoreComponent(viewState);

            viewState.setValid(true);
            viewState.setSkipUpdate(false);
            viewState.setSkipValidation(false);
            viewState.setCurrentDestination((ViewDestination) null);

            ComponentCollector collector = null;

            viewState.setUpdateComponentTree(true);
            while (viewState.getUpdateComponentTree()) {
                viewState.setUpdateComponentTree(false);

                collector = new ComponentCollector(viewState, component);
                updateComponent(collector, editRequest);

                runControllers(collector, viewState);
                component = viewState.getComponent();
            }

            holder.setComponent(component);
            holder.setCollector(collector);
            skipValidation = skipValidation || viewState.skipValidation();
        }

        for (ViewStateHolder holder : viewStateHolders) {
            if (!holder.isCanceled()) {
                IViewState viewState = holder.getViewState();

                if (viewState.isVisible() && !viewState.skipUpdate()) {
                    if (!skipValidation) {
                        viewState.setValid(validateComponent(viewState, holder.getComponent(), viewState.getMetaObject()));
                    }
                }

                if (viewState.isVisible() || isHiddenSlot(viewState)) {
                    if (viewState.isValid()) {
                        // updateMetaObject can get conversion errors
                        viewState.setValid(updateMetaObject(holder.getCollector(), editRequest, viewState));
                    }
                }

                allValid = allValid && viewState.isValid();
                anySkip = anySkip || viewState.skipUpdate();
            }
        }

        ViewDestination destination;
        try {
            if (allValid && !anySkip && !anyCanceled) {
                updateDomain(viewStates);
            }
        } finally {
            destination = getDestination(viewStates);
            prepareDestination(viewStates, editRequest);
        }

        return destination;
    }

    public static void doCancel(IViewState viewState) {
        viewState.setCurrentDestination("cancel");
        viewState.cancel();
    }

    private boolean cancelRequested(EditRequest editRequest) {
        return editRequest.getParameter(Constants.CANCEL_PROPERTY) != null;
    }

    private boolean isHiddenSlot(IViewState viewState) {
        return viewState.getMetaObject() instanceof MetaSlot && viewState.getHiddenSlots().size() > 0;
    }

    private ViewDestination getDestination(List<IViewState> viewStates) {
        ViewDestination destination = null;

        for (IViewState viewState : viewStates) {
            // Invisible viewstates have no influence in the destination
            // because they were not validated and no controller was run
            // for them
            if (!viewState.isVisible() && viewStates.size() > 1) {
                continue;
            }

            if (viewState.isCanceled()) {
                destination = viewState.getCurrentDestination();
            } else if (viewState.skipUpdate()) {
                destination = viewState.getCurrentDestination();

                if (destination == null) {
                    destination = viewState.getDestination("input");
                }

                if (destination == null) {
                    destination = viewState.getInputDestination();
                }
            } else {
                destination = viewState.getCurrentDestination();

                if (destination == null) {
                    // TODO: remove hardcoded?
                    destination = viewState.getDestination(viewState.isValid() ? "success" : "invalid");
                }

                if (destination == null && !viewState.isValid()) {
                    destination = viewState.getDestination("input");

                    if (destination == null) {
                        destination = viewState.getInputDestination();
                    }
                }
            }

            if (destination != null) {
                break;
            }
        }

        return destination;
    }

    private boolean validateComponent(IViewState viewState, HtmlComponent component, MetaObject metaObject) {
        boolean valid = true;

        List<HtmlComponent> validators = component.getChildren(new Predicate<HtmlComponent>() {
            @Override
            public boolean apply(HtmlComponent component) {
                return component instanceof HtmlChainValidator;
            }
        });

        List<HtmlComponent> formComponents = HtmlComponent.getComponents(component, new Predicate<HtmlComponent>() {
            @Override
            public boolean apply(HtmlComponent component) {
                if (!(component instanceof HtmlFormComponent)) {
                    return false;
                }

                HtmlFormComponent formComponent = (HtmlFormComponent) component;
                return formComponent.getTargetSlot() != null;
            }
        });

        if (!formComponents.isEmpty()) {
            for (HtmlComponent boundComponent : formComponents) {
                HtmlFormComponent formComponent = (HtmlFormComponent) boundComponent;
                HtmlChainValidator chainValidator = formComponent.getChainValidator();
                if (chainValidator == null) {
                    chainValidator = new HtmlChainValidator(formComponent);
                }

                MetaSlotKey key = formComponent.getTargetSlot();
                MetaSlot slot = getMetaSlot(metaObject, key);

                for (HtmlValidator validator : slot.getValidatorsList()) {
                    chainValidator.addValidator(validator);
                }

                validators.add(chainValidator);
            }
        }

        for (HtmlComponent validator : validators) {
            HtmlChainValidator htmlValidator = (HtmlChainValidator) validator;

            htmlValidator.performValidation();
            valid = valid && htmlValidator.isValid();

            if (!htmlValidator.isValid()) { // validator message
                if (metaObject instanceof MetaSlot) {
                    viewState.addMessage(new ValidationMessage((MetaSlot) metaObject, htmlValidator.getErrorMessage()));
                } else {
                    HtmlFormComponent validatedFormComponent = (HtmlFormComponent) htmlValidator.getComponent();
                    MetaSlotKey key = validatedFormComponent.getTargetSlot();

                    if (key != null) {
                        MetaSlot slot = getMetaSlot(metaObject, key);

                        if (slot != null) {
                            viewState.addMessage(new ValidationMessage(slot, htmlValidator.getErrorMessage()));
                        }
                    }
                }
            }
        }

        return valid;
    }

    private void runControllers(ComponentCollector collector, IViewState viewState) {
        for (HtmlController controller : collector.getControllers()) {
            HtmlFormComponent formComponent = (HtmlFormComponent) controller.getControlledComponent();

            if (formComponent != null) {
                controller.execute(new ViewStateWrapper(viewState, formComponent.getName()));
            } else {
                controller.execute(viewState);
            }
        }
    }

    public void prepareDestination(List<IViewState> viewStates, HttpServletRequest request) throws IOException,
            ClassNotFoundException {
        request.setAttribute(LifeCycleConstants.VIEWSTATE_PARAM_NAME, viewStates);
    }

    public HtmlComponent restoreComponent(IViewState viewState) throws InstantiationException, IllegalAccessException {
        viewState.setPostBack(true);

        MetaObject metaObject = viewState.getMetaObject();

        if (metaObject == null) {
            viewState.setMetaObject(MetaObjectFactory.createObject(null, null));
            metaObject = viewState.getMetaObject();
        }

        metaObject.setUser(viewState.getUser());

        Class contextClass = viewState.getContextClass();
        if (contextClass != null) {
            String layout = viewState.getLayout();
            Properties properties = viewState.getProperties();

            InputContext context = (InputContext) contextClass.newInstance();
            context.setLayout(layout);
            context.setProperties(properties);

            context.setViewState(viewState);
            viewState.setContext(context);

            if (!viewState.isVisible()) {
                return new HtmlText();
            }

            if (isHiddenSlot(viewState)) {
                viewState.setComponent(new HtmlText());
            } else {
                Object object = metaObject.getObject();
                viewState.setComponent(RenderKit.getInstance().render(context, object, metaObject.getType()));
            }
        }

        HtmlComponent component = viewState.getComponent();
        return component != null ? component : new HtmlText();
    }

    private void updateComponent(ComponentCollector collector, EditRequest editRequest) {
        List<HtmlFormComponent> formComponents = collector.getFormComponents();

        for (HtmlFormComponent formComponent : formComponents) {
            String name = formComponent.getName();

            if (formComponent instanceof HtmlMultipleValueComponent) {
                String[] values = editRequest.getParameterValues(name);

                if (values == null) {
                    values = new String[0];
                }

                ((HtmlMultipleValueComponent) formComponent).setValues(values);
            } else if (formComponent instanceof HtmlSimpleValueComponent) {
                String value = editRequest.getParameter(name);

                ((HtmlSimpleValueComponent) formComponent).setValue(value);
            }
        }
    }

    private void updateDomain(List<IViewState> viewStates) {
        List<MetaObject> metaObjectsToCommit = new ArrayList<MetaObject>();
        MetaObjectCollection metaObjectCollection = MetaObjectFactory.createObjectCollection();

        // TODO: check if should update viewstates that are not visible
        for (IViewState state : viewStates) {
            MetaObject metaObject = state.getMetaObject();

            if (metaObject instanceof MetaSlot) {
                metaObject = ((MetaSlot) metaObject).getMetaObject();
            }

            if (!metaObjectsToCommit.contains(metaObject)) {
                metaObjectsToCommit.add(metaObject);
            }

            metaObjectCollection.setUser(state.getUser());
        }

        for (MetaObject object : metaObjectsToCommit) {
            metaObjectCollection.add(object);
        }

        metaObjectCollection.commit();
    }

    /**
     * @return true if no conversion error occurs
     */
    private boolean updateMetaObject(ComponentCollector collector, EditRequest editRequest, IViewState viewState)
            throws Exception {
        boolean hasConvertError = false;

        List<HtmlFormComponent> formComponents = collector.getFormComponents();
        for (HtmlFormComponent formComponent : formComponents) {
            MetaSlotKey targetSlot = formComponent.getTargetSlot();

            if (targetSlot == null) {
                continue;
            }

            MetaSlot metaSlot = getMetaSlot(viewState.getMetaObject(), targetSlot);

            if (metaSlot == null) {
                continue;
            }

            // ensure that slots marked as read-only are not changed
            if (metaSlot.isReadOnly()) {
                continue;
            }

            try {
                Object finalValue = formComponent.getConvertedValue(metaSlot);
                metaSlot.setObject(finalValue);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("failed to do conversion for slot " + metaSlot.getName() + ": " + e);
                logger.debug("conversation stacktrace", e);
                addConvertError(viewState, metaSlot, e);
                hasConvertError = true;
            }
        }

        // TODO: this is confuse, it means it's valid if it does not have a
        // conversion error
        return !hasConvertError;
    }

    private MetaSlot getMetaSlot(MetaObject metaObject, MetaSlotKey targetSlot) {
        if (metaObject instanceof MetaSlot) {
            if (metaObject.getKey().equals(targetSlot)) {
                return (MetaSlot) metaObject;
            } else {
                metaObject = ((MetaSlot) metaObject).getMetaObject();
            }
        }

        for (MetaSlot slot : metaObject.getAllSlots()) {
            if (slot.getKey().equals(targetSlot)) {
                return slot;
            }
        }

        return null;
    }

    private void addConvertError(IViewState viewState, MetaSlot metaSlot, Exception exception) {
        viewState.addMessage(new ConversionMessage(metaSlot, exception.getLocalizedMessage()));
    }

    private static class ViewStateHolder {
        private IViewState viewState;
        private HtmlComponent component;
        private ComponentCollector collector;
        private boolean canceled;

        public ViewStateHolder(IViewState viewState) {
            this.viewState = viewState;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }

        public HtmlComponent getComponent() {
            return component;
        }

        public ComponentCollector getCollector() {
            return collector;
        }

        public IViewState getViewState() {
            return viewState;
        }

        public void setViewState(IViewState viewState) {
            this.viewState = viewState;
        }

        public void setComponent(HtmlComponent component) {
            this.component = component;
        }

        public void setCollector(ComponentCollector collector) {
            this.collector = collector;
        }

    }
}
