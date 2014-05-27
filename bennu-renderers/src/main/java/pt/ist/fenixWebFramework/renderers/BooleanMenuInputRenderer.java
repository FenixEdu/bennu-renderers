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

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenu;
import pt.ist.fenixWebFramework.renderers.components.HtmlMenuOption;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

import com.google.common.base.Strings;

/**
 * This renderer provides an alternative way of doing the input of a boolean
 * value. This renderer presents an html menu with two options: one for the <tt>true</tt> value, and other for the <tt>false</tt>
 * value.
 * 
 * <p>
 * The options text is retrieved from the resources using the keys <tt>TRUE</tt> and <tt>FALSE</tt>.
 * 
 * <p>
 * Example: <select> <option>Yes</option> <option>No</option> </select>
 * 
 * @author cfgi
 */
public class BooleanMenuInputRenderer extends InputRenderer {

    private String nullOptionKey;
    private String bundle;

    public String getNullOptionKey() {
        return nullOptionKey;
    }

    public void setNullOptionKey(String nullOptionKey) {
        this.nullOptionKey = nullOptionKey;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                HtmlMenu menu = new HtmlMenu();

                if (getNullOptionKey() != null) {
                    String defaultOptionTitle = RenderUtils.getResourceString(getBundle(), getNullOptionKey());
                    menu.createDefaultOption(defaultOptionTitle).setSelected(object == null);
                    menu.setConverter(new Converter() {
                        @Override
                        public Object convert(Class type, Object value) {
                            return Strings.isNullOrEmpty((String) value) ? null : Boolean.valueOf((String) value);
                        }
                    });
                }

                HtmlMenuOption trueOption = menu.createOption(RenderUtils.getResourceString("TRUE"));
                HtmlMenuOption falseOption = menu.createOption(RenderUtils.getResourceString("FALSE"));

                trueOption.setValue("true");
                falseOption.setValue("false");

                if (object != null) {
                    (((Boolean) object) ? trueOption : falseOption).setSelected(true);
                }

                menu.setTargetSlot((MetaSlotKey) getInputContext().getMetaObject().getKey());

                return menu;
            }

        };
    }

}
