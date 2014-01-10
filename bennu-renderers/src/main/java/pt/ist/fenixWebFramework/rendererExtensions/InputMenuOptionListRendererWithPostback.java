/*
 * Created on Apr 27, 2006
 */
package pt.ist.fenixWebFramework.rendererExtensions;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenu;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlController;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.components.state.ViewDestination;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public class InputMenuOptionListRendererWithPostback extends InputMenuOptionListRenderer {

    private final String HIDDEN_NAME = "postback";

    private String destination;

    public String getDestination() {
        return destination;
    }

    /**
     * Allows to choose the postback destination. If this property is not
     * specified the default "postback" destination is used.
     * 
     * @property
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public HtmlComponent render(Object object, Class type) {
        HtmlInlineContainer container = new HtmlInlineContainer();

        String prefix =
                HtmlComponent.getValidIdOrName(((MetaSlot) getInputContext().getMetaObject()).getKey().toString()
                        .replaceAll("\\.", "_").replaceAll("\\:", "_"));

        HtmlHiddenField hidden = new HtmlHiddenField(prefix + HIDDEN_NAME, "");

        HtmlMenu menu = (HtmlMenu) super.render(object, type);
        menu.setOnChange("this.form." + prefix + HIDDEN_NAME + ".value='true';this.form.submit();");
        menu.setController(new PostBackController(hidden, getDestination()));

        container.addChild(hidden);
        container.addChild(menu);

        return container;
    }

    private static class PostBackController extends HtmlController {

        private final HtmlHiddenField hidden;

        private final String destination;

        public PostBackController(HtmlHiddenField hidden, String destination) {
            this.hidden = hidden;
            this.destination = destination;
        }

        @Override
        public void execute(IViewState viewState) {
            if (hidden.getValue() != null && hidden.getValue().equalsIgnoreCase("true")) {
                String destinationName = this.destination == null ? "postback" : this.destination;
                ViewDestination destination = viewState.getDestination(destinationName);

                if (destination != null) {
                    viewState.setCurrentDestination(destination);
                } else {
                    viewState.setCurrentDestination("postBack");
                }

                hidden.setValue("false");
                viewState.setSkipValidation(true);
            }

        }

    }
}
