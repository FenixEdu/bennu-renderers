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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import com.google.common.base.Predicate;

/**
 * The base renderer for every input renderer.
 * 
 * @author cfgi
 */
public abstract class InputRenderer extends Renderer {
    private static final Logger logger = LoggerFactory.getLogger(InputRenderer.class);

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
            List<HtmlComponent> children = component.getChildren(new Predicate<HtmlComponent>() {

                @Override
                public boolean apply(HtmlComponent component) {
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
            newContext.setRenderMode(RenderMode.OUTPUT);
        }

        Object value = slot.getObject();
        Class type = slot.getType();

        RenderKit kit = RenderKit.getInstance();
        return kit.render(newContext, value, type);
    }
}
