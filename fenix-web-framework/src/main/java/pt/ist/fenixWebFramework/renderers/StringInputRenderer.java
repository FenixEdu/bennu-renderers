package pt.ist.fenixWebFramework.renderers;

import org.apache.commons.collections.Predicate;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;

/**
 * This renderer provides a standard way of doing the input of a string. The
 * string is read with a text input field.
 * 
 * <p>
 * Example: <input type="text" value="the string"/>
 * 
 * @author cfgi
 */
public class StringInputRenderer extends TextFieldRenderer {

    @Override
    protected HtmlComponent createTextField(Object object, Class type) {
        String string = (String) object;

        HtmlTextInput input = new HtmlTextInput();
        input.setValue(string);

        HtmlContainer container = new HtmlInlineContainer();
        container.addChild(input);
        container.addChild(new HtmlText(getFormatLabel()));

        return container;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new StringInputFieldLayout();
    }

    class StringInputFieldLayout extends TextFieldLayout {

        @Override
        protected void setContextSlot(HtmlComponent component, MetaSlotKey slotKey) {
            HtmlComponent actualComponent = component instanceof HtmlTextInput ? component : component.getChild(new Predicate() {

                @Override
                public boolean evaluate(Object arg0) {
                    return arg0 instanceof HtmlTextInput;
                }

            });
            super.setContextSlot(actualComponent, slotKey);
        }

        @Override
        public void applyStyle(HtmlComponent component) {
            HtmlComponent actualComponent = component instanceof HtmlTextInput ? component : component.getChild(new Predicate() {

                @Override
                public boolean evaluate(Object arg0) {
                    return arg0 instanceof HtmlTextInput;
                }

            });
            super.applyStyle(actualComponent);
        }

    }
}
