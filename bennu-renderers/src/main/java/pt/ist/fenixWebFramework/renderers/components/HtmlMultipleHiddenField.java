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

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlMultipleHiddenField extends HtmlMultipleValueComponent {

    public HtmlMultipleHiddenField(String name) {
        super();

        setName(name);
    }

    public HtmlMultipleHiddenField() {
        super();
    }

    public void addValue(String value) {
        String[] values = getValues();

        if (values == null) {
            setValues(new String[] { value });
        } else {
            String[] newValues = new String[values.length + 1];

            for (int i = 0; i < values.length; i++) {
                newValues[i] = values[i];
            }

            newValues[values.length] = value;
            setValues(newValues);
        }
    }

    public void removeValue(int i) {
        String[] values = getValues();

        if (values == null) {
            return;
        }

        if (i < 0 || i >= values.length) {
            return;
        }

        if (values.length == 1) {
            setValues((String[]) null);
            return;
        }

        String[] newValues = new String[values.length - 1];
        for (int j = 0; j < newValues.length; j++) {
            if (j < i) {
                newValues[j] = values[j];
            } else {
                newValues[j] = values[j + 1];
            }
        }

        setValues(newValues);
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName(null);

        String[] values = getValues();
        for (String value : values) {
            HtmlHiddenField hidden = new HtmlHiddenField(getName(), value);
            hidden.setTargetSlot(getTargetSlot());

            tag.addChild(hidden.getOwnTag(context));
        }

        return tag;
    }
}
