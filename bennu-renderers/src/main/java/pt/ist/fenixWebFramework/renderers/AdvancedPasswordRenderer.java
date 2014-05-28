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
