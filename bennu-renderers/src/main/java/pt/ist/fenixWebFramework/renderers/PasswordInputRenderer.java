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
import pt.ist.fenixWebFramework.renderers.components.HtmlPasswordInput;

/**
 * This renderer provides a standard way of doing the input of a password. The
 * password is read with a password input field.
 * 
 * <p>
 * Example: <input type="password" value="the password"/>
 * 
 * @author naat
 */
public class PasswordInputRenderer extends TextFieldRenderer {

    @Override
    protected HtmlComponent createTextField(Object object, Class type) {
        String string = (String) object;

        HtmlPasswordInput inputPassword = new HtmlPasswordInput();
        inputPassword.setValue(string);

        return inputPassword;
    }

}
