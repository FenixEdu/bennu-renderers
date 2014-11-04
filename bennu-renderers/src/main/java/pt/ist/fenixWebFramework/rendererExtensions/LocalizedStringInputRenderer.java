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

import java.util.Collections;
import java.util.Properties;

import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixWebFramework.rendererExtensions.validators.LocalizedStringValidator;
import pt.ist.fenixWebFramework.rendererExtensions.validators.RequiredLocalizedStringValidator;
import pt.ist.fenixWebFramework.renderers.InputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.validators.ValidatorProperties;

import com.google.common.base.Strings;
import com.google.gson.JsonParser;

/**
 * This renderer provides a generic way of editing slots that contain a {@link MultiLanguageString}. The interface generated
 * allows the user to
 * incrementally add more values in different languages. The user can also
 * remove some of the values already introduced.
 * <p>
 * Example: <div> <div> <input type="text"/> <select> <option selected="selected" value="">-- Choose an option --</option> <option
 * value="eo">Esperanto</option> <option value="xx-klingon">Klingon</option> <option value="xx-piglatin">Pig Latin</option>
 * <option value="xx-elmer">Elmer Fudd</option> </select> <a href="#">Remove</a> </div> <div> <input type="text"/> <select>
 * <option selected="selected" value="">-- Choose an option --</option> <option value="eo">Esperanto</option> <option
 * value="xx-klingon">Klingon</option> <option value="xx-piglatin">Pig Latin</option> <option value="xx-elmer">Elmer Fudd</option>
 * </select> <a href="#">Remove</a> </div> <a href="#">Add</a> </div>
 * 
 * @author cfgi
 */
public class LocalizedStringInputRenderer extends InputRenderer {

    private String eachClasses;

    public String getEachClasses() {
        return this.eachClasses;
    }

    /**
     * The classes to apply to the div containing each language line.
     * 
     * @property
     */
    public void setEachClasses(String eachClasses) {
        this.eachClasses = eachClasses;
    }

    protected Converter getConverter() {
        return new LocalizedStringConverter();
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        MetaObject metaObject = getInputContext().getMetaObject();

        if (metaObject != null && metaObject instanceof MetaSlot) {
            MetaSlot metaSlot = (MetaSlot) metaObject;

            if (!metaSlot.hasValidator()) {
                Class defaultValidator = LocalizedStringValidator.class;
                metaSlot.setValidators(Collections.singletonList(new ValidatorProperties(defaultValidator, new Properties())));
            }
        }

        return new MultiLanguageStringInputLayout();
    }

    protected HtmlSimpleValueComponent getInputComponent() {
        return new HtmlTextInput();
    }

    protected class MultiLanguageStringInputLayout extends Layout {

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            LocalizedString localized = getLocalized(object);

            MetaSlot slot = (MetaSlot) getInputContext().getMetaObject();

            HtmlSimpleValueComponent input = getInputComponent();
            input.setValue(localized == null ? "" : localized.json().toString());
            input.setAttribute("bennu-localized-string", "");
            input.setTargetSlot(slot.getKey());
            input.setConverter(getConverter());
            if (slot.isRequired()
                    || slot.getValidators().stream()
                            .filter(prop -> RequiredLocalizedStringValidator.class.isAssignableFrom(prop.getType())).findAny()
                            .isPresent()) {
                input.setAttribute("required-any", "");
            }

            getInputContext().requireToolkit();

            return input;
        }

    }

    protected LocalizedString getLocalized(Object object) {
        if (object instanceof LocalizedString) {
            return (LocalizedString) object;
        } else {
            return null;
        }
    }

    public static class LocalizedStringConverter extends Converter {

        @Override
        public Object convert(Class type, Object value) {
            String text = (String) value;
            return Strings.isNullOrEmpty(text) ? null : processLocalized(LocalizedString.fromJson(new JsonParser().parse(text)));
        }

        protected Object processLocalized(LocalizedString mls) {
            return mls;
        }

    }

}
