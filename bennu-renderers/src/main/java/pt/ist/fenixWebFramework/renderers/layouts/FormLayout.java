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
            HtmlBlockContainer slotContainer = new HtmlBlockContainer();

            if (displayLabel()) {
                label.addClass("control-label");
                label.addClass("col-sm-2");
                container.addChild(label);
                slotContainer.addClass("col-sm-10");
            } else {
                slotContainer.addClass("col-sm-12");
            }

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

            helpLabel.ifPresent(help -> {
                slotContainer.setAttribute("style", "padding: 0 15px;");                
                slotContainer.addClass("input-group");
            });
            slotContainer.addBlock((tag, context) -> {
                helpLabel.ifPresent(help -> {               
                	HtmlText helpButton = new HtmlText(
                	        "<span class=\"input-group-btn\">" +
                    			"<button type=\"button\" class=\"btn btn-sm\" data-toggle=\"popover\" data-placement=\"left\" data-trigger=\"focus\" title=\"\" data-content=\""+ helpLabel.get() +"\">" + 
                    					"<span class=\"glyphicon glyphicon-info-sign\" aria-hidden=\"true\"></span>" + 
                    			"</button>" +
                    		"</span>"
                			);
                	helpButton.setEscaped(false);
                    tag.addChild(helpButton.getOwnTag(context));                    
                    HtmlScript script = new HtmlScript();
                    script.setScript("$('[data-toggle=popover]').popover();");
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

    public boolean displayLabel() {
        return true;
    }

}
