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

import org.apache.commons.beanutils.ConvertUtils;

import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public abstract class HtmlMultipleValueComponent extends HtmlFormComponent {

    public String[] values;

    public HtmlMultipleValueComponent() {
        super();

        values = new String[0];
    }

    public void setValues(String... values) {
        this.values = values;
    }

    @Override
    public String[] getValues() {
        return values;
    }

    @Override
    public String getValue() {
        String[] values = getValues();

        if (values == null) {
            return null;
        }

        if (values.length == 0) {
            return null;
        }

        return values[0];
    }

    @Override
    public Object getConvertedValue() {
        if (hasConverter()) {
            return getConverter().convert(Object.class, getValue());
        }

        return ConvertUtils.convert(getValue(), Object.class);
    }

    @Override
    public Object getConvertedValue(MetaSlot slot) {
        if (hasConverter()) {
            return getConverter().convert(slot.getStaticType(), getValues());
        }

        if (slot.hasConverter()) {
            try {
                return slot.getConverter().newInstance().convert(slot.getStaticType(), getValues());
            } catch (Exception e) {
                throw new RuntimeException("converter specified in meta slot generated an exception", e);
            }
        }

        return ConvertUtils.convert(getValues(), slot.getStaticType());
    }
}