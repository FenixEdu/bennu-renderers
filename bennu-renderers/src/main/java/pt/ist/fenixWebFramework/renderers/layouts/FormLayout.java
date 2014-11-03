package pt.ist.fenixWebFramework.renderers.layouts;

import java.util.Optional;
import java.util.function.Supplier;

import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlLabel;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;

public abstract class FormLayout extends Layout {

    @Override
    public HtmlComponent createComponent(Object object, Class type) {
        HtmlBlockContainer topLevelContainer = new HtmlBlockContainer();

        int rowNumber = getNumberOfRows();

        for (int rowIndex = 0; rowIndex < rowNumber; rowIndex++) {
            HtmlBlockContainer container = new HtmlBlockContainer();
            topLevelContainer.addChild(container);

            container.addClass("form-group");

            HtmlLabel label = new HtmlLabel(getLabelText(rowIndex));
            label.addClass("control-label");
            label.addClass("col-sm-2");
            container.addChild(label);

            HtmlBlockContainer slotContainer = new HtmlBlockContainer();
            slotContainer.addClass("col-sm-10");
            container.addChild(slotContainer);

            HtmlComponent renderedSlot = getRenderedSlot(rowIndex);
            slotContainer.addChild(renderedSlot);

            Supplier<Optional<String>> validationErrors = getValidationError(rowIndex);

            slotContainer.addBlock((tag, context) -> {
                validationErrors.get().ifPresent(error -> {
                    HtmlText text = new HtmlText(error);
                    text.setClasses("help-block");
                    text.setStyle("margin-bottom: 0");
                    tag.addChild(text.getOwnTag(context));
                });
            });

            Optional<String> helpLabel = getHelpLabel(rowIndex);

            slotContainer.addBlock((tag, context) -> {
                helpLabel.ifPresent(help -> {
                    tag.setAttribute("data-toggle", "tooltip");
                    tag.setAttribute("title", help);
                    tag.setAttribute("data-placement", "bottom");

                    HtmlScript script = new HtmlScript();
                    script.setScript("$('[data-toggle=tooltip]').tooltip();");
                    tag.addChild(script.getOwnTag(context));
                });
            });

            container.addBlock((tag, context) -> {
                validationErrors.get().ifPresent(
                        error -> {
                            tag.setAttribute("class", container.getClasses() == null ? "has-error" : container.getClasses()
                                    + " has-error");
                        });
            });

            label.setFor(renderedSlot.getId());
        }

        return topLevelContainer;
    }

    public abstract int getNumberOfRows();

    public abstract String getLabelText(int rowIndex);

    public abstract HtmlComponent getRenderedSlot(int rowIndex);

    public abstract Supplier<Optional<String>> getValidationError(int rowIndex);

    public Optional<String> getHelpLabel(int rowIndex) {
        return Optional.empty();
    }

}
