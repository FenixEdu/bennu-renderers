package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlSubmitButton;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlSubmitButtonController;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;

public class StringCaseChangerRenderer extends StringInputRenderer {

    @Override
    protected HtmlComponent createTextField(Object object, Class type) {
        HtmlInlineContainer container = new HtmlInlineContainer();
        HtmlContainer fieldComponent = (HtmlContainer) super.createTextField(object, type);

        HtmlSimpleValueComponent component = (HtmlSimpleValueComponent) fieldComponent.getChildren().get(0);
        HtmlSubmitButton caseChangeButton = new HtmlSubmitButton("");
        HtmlSubmitButton capitalizeButton = new HtmlSubmitButton("Capitalize");

        nameButton(caseChangeButton, "case-button-name");
        nameButton(capitalizeButton, "capitalize-button-name");

        container.addChild(component);
        container.addChild(new HtmlText(getFormatLabel()));
        container.addChild(caseChangeButton);
        container.addChild(capitalizeButton);

        caseChangeButton.setController(new CaseChangeController(component, caseChangeButton, capitalizeButton));
        capitalizeButton.setController(new CapitalizeController(component));

        return container;
    }

    private void nameButton(HtmlSubmitButton button, String attribute) {
        String buttonName = (String) getInputContext().getViewState().getAttribute(attribute);

        if (buttonName == null) {
            getInputContext().getViewState().setAttribute(attribute, button.getName());
        } else {
            button.setName(buttonName);
        }
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new TextFieldLayout() {

            @Override
            public void applyStyle(HtmlComponent component) {
                super.applyStyle(((HtmlInlineContainer) component).getChildren().get(0));
            }

            @Override
            protected void setContextSlot(HtmlComponent component, MetaSlotKey slotKey) {
                HtmlContainer container = (HtmlInlineContainer) component;
                HtmlSimpleValueComponent field = (HtmlSimpleValueComponent) container.getChildren().get(0);

                super.setContextSlot(field, slotKey);
            }

        };
    }

    class CaseChangeController extends HtmlSubmitButtonController {

        private HtmlSubmitButton button;
        private HtmlSubmitButton capitalize;
        private HtmlSimpleValueComponent input;

        public CaseChangeController(HtmlSimpleValueComponent component, HtmlSubmitButton button, HtmlSubmitButton capitalizeButton) {
            this.input = component;
            this.button = button;
            this.capitalize = capitalizeButton;

            setupButtons();
        }

        private void setupButtons() {
            this.button.setText(isUpperCase() ? "To Upper Case" : "To Lower Case");
            this.capitalize.setVisible(isUpperCase());
        }

        private boolean isUpperCase() {
            if (getInputContext().getViewState().getAttribute("isUpperCase") == null) {
                return true;
            }

            return ((Boolean) getInputContext().getViewState().getAttribute("isUpperCase")).booleanValue();
        }

        private void setUpperCase(boolean isUpperCase) {
            getInputContext().getViewState().setAttribute("isUpperCase", new Boolean(isUpperCase));
        }

        @Override
        protected void buttonPressed(IViewState viewState, HtmlSubmitButton button) {
            String text = this.input.getValue();
            this.input.setValue(isUpperCase() ? text.toUpperCase() : text.toLowerCase());

            setUpperCase(!isUpperCase());
            setupButtons();
        }
    }

    class CapitalizeController extends HtmlSubmitButtonController {

        private HtmlSimpleValueComponent input;

        public CapitalizeController(HtmlSimpleValueComponent component) {
            this.input = component;
        }

        @Override
        protected void buttonPressed(IViewState viewState, HtmlSubmitButton button) {
            String text = this.input.getValue();
            this.input.setValue(capitalize(text));
        }

        private String capitalize(String text) {
            StringBuilder buffer = new StringBuilder();
            char ch, prevCh;

            prevCh = ' ';
            for (int i = 0; i < text.length(); i++) {
                ch = text.charAt(i);

                if (Character.isLetter(ch) && !Character.isLetter(prevCh)) {
                    buffer.append(Character.toUpperCase(ch));
                } else {
                    buffer.append(ch);
                }

                prevCh = ch;
            }

            return buffer.toString();
        }

    }
}
