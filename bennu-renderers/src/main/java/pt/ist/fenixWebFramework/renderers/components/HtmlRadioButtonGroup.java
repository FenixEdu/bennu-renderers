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
package pt.ist.fenixWebFramework.renderers.components;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlRadioButtonGroup extends HtmlSimpleValueComponent {

    private List<HtmlRadioButton> radioButtons;

    public HtmlRadioButtonGroup() {
        super();

        this.radioButtons = new ArrayList<HtmlRadioButton>();
    }

    public List<HtmlRadioButton> getRadioButtons() {
        return this.radioButtons;
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);

        for (HtmlRadioButton radio : getRadioButtons()) {
            if (value != null && value.equals(radio.getValue())) {
                radio.setChecked(true);
            } else {
                radio.setChecked(false);
            }
        }
    }

    public HtmlRadioButton createRadioButton() {
        HtmlRadioButton radio = new HtmlRadioButton() {

            @Override
            public void setChecked(boolean checked) {
                HtmlRadioButtonGroup.this.setChecked(this, checked);

                super.setChecked(checked);
            }

        };

        getRadioButtons().add(radio);
        radio.setName(getName());

        return radio;
    }

    protected void setChecked(HtmlRadioButton button, boolean checked) {
        if (!checked) {
            return;
        }

        for (HtmlRadioButton radio : getRadioButtons()) {
            if (radio.equals(button)) {
                continue;
            }

            radio.setChecked(false);
        }
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        for (HtmlRadioButton radio : getRadioButtons()) {
            radio.setName(getName());

            if (getTargetSlot() != null) {
                radio.setTargetSlot(getTargetSlot());
            }
        }

        return new HtmlText().getOwnTag(context);
    }
}