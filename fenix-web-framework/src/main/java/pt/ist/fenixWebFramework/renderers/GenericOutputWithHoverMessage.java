package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class GenericOutputWithHoverMessage extends AbstractToolTipRenderer {

    private String format;

    private String hoverMessage;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getHoverMessage() {
        return hoverMessage;
    }

    public void setHoverMessage(String hover) {
        this.hoverMessage = hover;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {

        return new ToolTipLayout() {
            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                PresentationContext context = getContext();

                context.setLayout(getSubLayout());
                context.setProperties(getProperties());

                HtmlComponent component = RenderKit.getInstance().render(context, object, type);
                String hoverMessage = null;

                if (getFormat() != null) {
                    hoverMessage = RenderUtils.getFormattedProperties(getFormat(), getTargetObject(object));
                } else {
                    if (isKey()) {
                        hoverMessage = RenderUtils.getResourceString(getBundle(), getHoverMessage());
                    } else {
                        hoverMessage = getHoverMessage();
                    }
                }

                return wrapUpCompletion(component, new HtmlText(hoverMessage, isEscape()));
            }
        };
    }

}
