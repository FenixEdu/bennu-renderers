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
package pt.ist.fenixWebFramework.rendererExtensions;

import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenu;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenuEntry;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenuOption;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlController;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * This renderer allows you to choose one string from a list of strings by using a menu. It also
 * offers an option to manually insert a new value through a text field.
 * <p>
 * Example: <select> <option>-</option> <option>a</option> <option>b</option> <option>c</option> <option
 * selected="selected">other</option> </select> <input type="text"/>
 * 
 * @author cfgi
 */
public class StringInputMenuOptionListRenderer extends InputMenuOptionListRenderer {

    private static final String OPTION_KEY = "__other";

    private String otherSize;

    public String getOtherSize() {
        return this.otherSize;
    }

    /**
     * Allows you to specify the size of the text field that appears when
     * entering a value manually.
     * 
     * @property
     */
    public void setOtherSize(String otherSize) {
        this.otherSize = otherSize;
    }

    @Override
    protected HtmlComponent renderComponent(Layout layout, Object object, Class type) {
        String value = (String) object;
        MetaSlot slot = (MetaSlot) getInputContext().getMetaObject();

        HtmlMenu menu = (HtmlMenu) super.renderComponent(layout, object, type);
        HtmlMenuOption otherOption = createOtherOption(menu);

        HtmlHiddenField field = new HtmlHiddenField();

        HtmlTextInput other = new HtmlTextInput();
        other.setSize(getOtherSize());

        boolean otherSelected = true;
        for (HtmlMenuEntry entry : menu.getEntries()) {
            if (entry.isSelected()) {
                other.setStyle("display: none;");
                otherSelected = false;
                break;
            }
        }

        if (otherSelected) {
            otherOption.setSelected(true);
            other.setValue(value);
        }

        field.setValue(value);
        field.bind(slot);
        field.setController(new CopyController(menu, other));

        menu.setTargetSlot(null);
        menu.setName(getLocalName(slot, "menu"));
        other.setName(getLocalName(slot, "other"));

        HtmlContainer container = new HtmlBlockContainer();

        container.addChild(createSwitchScript(field, menu, other));
        container.addChild(field);
        container.addChild(menu);
        container.addChild(other);

        return container;
    }

    private HtmlMenuOption createOtherOption(HtmlMenu menu) {
        String title = RenderUtils.getResourceString("renderers.menu.other.title");
        HtmlMenuOption otherOption = menu.createOption(title);
        otherOption.setValue(OPTION_KEY);

        return otherOption;
    }

    private String getLocalName(MetaSlot slot, String name) {
        return slot.getKey().toString() + "_" + name;
    }

    private HtmlComponent createSwitchScript(HtmlHiddenField field, HtmlMenu menu, HtmlTextInput other) {
        HtmlScript script = new HtmlScript();
        script.setContentType("text/javascript");

        other.setId(other.getName());

        String name = "showOther" + hashCode();
        String body =
                String.format("\n" + "function %s(s, id)  {\n" + "  var e = document.getElementById(id);\n"
                        + "  if (s.value == '%s') {\n" + "    e.style.display = 'inline';\n" + "  }\n" + "  else {\n"
                        + "   e.style.display = 'none';\n" + "   e.value = '';\n" + "  }\n" + "}", name, OPTION_KEY);

        script.setScript(body);
        menu.setOnChange(String.format("%s(this, '%s')", name, other.getId()));

        return script;
    }

    public static class CopyController extends HtmlController {

        private final HtmlMenu menu;
        private final HtmlTextInput other;

        public CopyController(HtmlMenu menu, HtmlTextInput other) {
            this.menu = menu;
            this.other = other;
        }

        @Override
        public void execute(IViewState viewState) {
            HtmlSimpleValueComponent hidden = (HtmlSimpleValueComponent) getControlledComponent();

            String menuValue = menu.getValue();
            if (menuValue == null || menuValue.equals(OPTION_KEY)) {
                String value = this.other.getValue();

                if (value != null) {
                    value = value.trim();

                    if (value.length() == 0) {
                        value = null;
                    }
                }

                hidden.setValue(value);
            } else {
                hidden.setValue(this.menu.getValue());
            }
        }

    }
}
