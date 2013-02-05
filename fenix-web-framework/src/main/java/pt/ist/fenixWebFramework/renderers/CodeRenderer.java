package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlPreformattedText;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

public class CodeRenderer extends OutputRenderer {

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                String text = (String) object;

                if (text == null) {
                    return new HtmlText();
                }

                HtmlPreformattedText container = new HtmlPreformattedText();
                container.setIndented(false);

                container.addChild(new HtmlText(text, true, true));

                return container;
            }

        };
    }

}
