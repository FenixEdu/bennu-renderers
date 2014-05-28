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

import pt.ist.fenixWebFramework.renderers.components.HtmlLabel;
import pt.ist.fenixWebFramework.renderers.components.HtmlRadioButton;
import pt.ist.fenixWebFramework.renderers.components.HtmlRadioButtonList;
import pt.ist.fenixWebFramework.renderers.components.HtmlSimpleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;

/**
 * The <code>EnumRadioInputRenderer</code> provides a way of doing the
 * input of an enum value by using a list of radio buttons. All the values
 * of that enum are presented as a radio button and the use can choose
 * one of the values.
 * 
 * <p>
 * Example:
 * <ul>
 * <li><input type="radio" name="same"/>Male</li>
 * <li><input type="radio" name="same" checked="checked"/>Female</li>
 * </ul>
 * 
 * @author cfgi
 */
public class EnumRadioInputRenderer extends EnumInputRenderer {

    @Override
    protected void addEnumElement(Enum enumerate, HtmlSimpleValueComponent holder, Enum oneEnum, String description) {
        HtmlRadioButtonList radioList = (HtmlRadioButtonList) holder;

        HtmlText descriptionComponent = new HtmlText(description);
        HtmlLabel label = new HtmlLabel();
        label.setBody(descriptionComponent);

        HtmlRadioButton radioButton = radioList.addOption(label, oneEnum.toString());
        label.setFor(radioButton);

        if (oneEnum.equals(enumerate)) {
            radioButton.setChecked(true);
        }
    }

    @Override
    protected HtmlSimpleValueComponent createInputContainerComponent(Enum enumerate) {
        return new HtmlRadioButtonList();
    }

}
