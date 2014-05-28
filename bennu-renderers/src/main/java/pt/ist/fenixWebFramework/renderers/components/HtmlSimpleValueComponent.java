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

import org.apache.commons.beanutils.ConvertUtils;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public abstract class HtmlSimpleValueComponent extends HtmlFormComponent {

    private String value;

    public HtmlSimpleValueComponent() {
        super();
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String[] getValues() {
        if (getValue() == null) {
            return null;
        }

        return new String[] { getValue() };
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
            return getConverter().convert(slot.getStaticType(), getValue());
        }

        if (slot.hasConverter()) {
            try {
                return slot.getConverter().newInstance().convert(slot.getStaticType(), getValue());
            } catch (Exception e) {
                throw new RuntimeException("converter specified in meta slot generated an exception: " + e, e);
            }
        }

        if (getValue() == null) {
            return null;
        }
        return ConvertUtils.convert(getValue(), slot.getStaticType());
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setAttribute("value", HtmlText.escape(this.value));

        return tag;
    }
}