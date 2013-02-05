package pt.ist.fenixWebFramework.renderers;

import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.Validatable;
import pt.ist.fenixWebFramework.renderers.contexts.InputContext;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderMode;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;

/**
 * The base renderer for every input renderer.
 * 
 * @author cfgi
 */
public abstract class InputRenderer extends Renderer {
    private static final Logger logger = Logger.getLogger(InputRenderer.class);

    public InputContext getInputContext() {
        return (InputContext) getContext();
    }

    protected Validatable findValidatableComponent(HtmlComponent component) {
        if (component == null) {
            return null;
        }

        if (component instanceof Validatable) {
            return (Validatable) component;
        } else {
            List<HtmlComponent> children = component.getChildren(new Predicate() {

                @Override
                public boolean evaluate(Object component) {
                    if (!(component instanceof HtmlFormComponent)) {
                        return false;
                    }

                    HtmlFormComponent formComponent = (HtmlFormComponent) component;
                    return formComponent.hasTargetSlot();
                }

            });

            if (children.size() > 0) {
                return (Validatable) children.get(0);
            }
        }

        return null;
    }

    protected HtmlChainValidator getChainValidator(Validatable inputComponent, MetaSlot slot) {
        if (inputComponent == null) {
            return null;
        }

        HtmlChainValidator chainValidator = new HtmlChainValidator(inputComponent);
        for (HtmlValidator validator : slot.getValidatorsList()) {
            chainValidator.addValidator(validator);
        }
        return chainValidator;
    }

    @Override
    protected HtmlComponent renderSlot(MetaSlot slot) {
        PresentationContext newContext = getContext().createSubContext(slot);
        newContext.setSchema(slot.getSchema() != null ? slot.getSchema() : null);
        newContext.setLayout(slot.getLayout());
        newContext.setProperties(slot.getProperties());

        if (slot.isReadOnly()) {
            newContext.setRenderMode(RenderMode.getMode("output"));
        }

        Object value = slot.getObject();
        Class type = slot.getType();

        RenderKit kit = RenderKit.getInstance();
        return kit.render(newContext, value, type);
    }
}
