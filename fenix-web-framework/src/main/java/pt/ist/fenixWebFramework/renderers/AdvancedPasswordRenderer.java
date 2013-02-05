package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBox;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlPasswordInput;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class AdvancedPasswordRenderer extends InputRenderer {

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                HtmlBlockContainer container = new HtmlBlockContainer();
                HtmlPasswordInput passwordInput = new HtmlPasswordInput();
                container.addChild(passwordInput);
                passwordInput.setId(passwordInput.getName());
                HtmlCheckBox checkBox = new HtmlCheckBox();
                checkBox.setText(RenderUtils.getResourceString("RENDERER_RESOURCES",
                        "renderers.AdvancedPasswordRenderer.showPassword"));
                checkBox.setId(checkBox.getName());
                container.addChild(checkBox);
                HtmlScript script = new HtmlScript();

                String passwordInputId = RenderUtils.escapeId(passwordInput.getId());
                String checkBoxId = RenderUtils.escapeId(checkBox.getId());

                script.setScript("$(\"#" + checkBoxId + "\").click( function() { " + "var password = $(\"#" + passwordInputId
                        + "\").attr('value');" + "var id = '" + passwordInputId + "';" + "if ($(this).attr('checked')) { $(\"#"
                        + passwordInputId
                        + "\").after(\"<input type='text' id=\" + id + \" value=\" + password + \" />\").remove(); }"
                        + "else { $(\"#" + passwordInputId
                        + "\").after(\"<input type='password' id=\" + id + \" value=\" + password + \" />\").remove(); }" + "});");
                container.addChild(script);
                return container;
            }

        };
    }

}
