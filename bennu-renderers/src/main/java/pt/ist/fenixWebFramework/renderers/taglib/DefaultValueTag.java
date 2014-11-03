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
package pt.ist.fenixWebFramework.renderers.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

// fr:default slot="" name="" property="" scope="" value="" converter=""/>
public class DefaultValueTag extends TagSupport {

    private String slot;
    private String name;
    private String property;
    private String scope;
    private String value;
    private String converter;

    public String getConverter() {
        return this.converter;
    }

    public void setConverter(String converter) {
        this.converter = converter;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSlot() {
        return this.slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void release() {
        super.release();

        this.slot = null;
        this.name = null;
        this.property = null;
        this.scope = null;
        this.value = null;
        this.converter = null;
    }

    @Override
    public int doStartTag() throws JspException {
        validateAttributes();

        CreateObjectTag parent = getParentCreateTag();

        if (parent == null) {
            throw new JspException("the default value tag can only be used inside a create tag");
        }

        parent.setDefaultValue(findSlot(), findObject(), getConverterClass());

        return SKIP_BODY;
    }

    private String findSlot() {
        String slot = getSlot();

        if (slot == null) {
            slot = getParentCreateTag().getSlot();
        }

        if (slot == null) {
            throw new RuntimeException("you must define the slot attribute or have a parent create tag with a slot defined");
        }

        return slot;
    }

    protected void validateAttributes() throws JspException {
        if (getName() == null && getValue() == null) {
            throw new JspException("you must define either the 'name' or 'value' attributes");
        }

        if (getName() == null && getProperty() != null) {
            throw new JspException("you must define the 'name' attribute to use the 'property' attribute");
        }

        if (getName() == null && getScope() != null) {
            throw new JspException("you must define the 'name' attribute to use the 'scope' attribute");
        }

        if (getName() != null && getValue() != null) {
            throw new JspException("you can't define both the 'name' and 'value' attributes");
        }

        if (getName() != null && getConverter() != null) {
            throw new JspException(
                    "you can't define both the 'name' and 'converter' attributes, converter is to be used with the 'value' attribute only");
        }
    }

    protected CreateObjectTag getParentCreateTag() {
        return (CreateObjectTag) findAncestorWithClass(this, CreateObjectTag.class);
    }

    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    protected Object findObject() throws JspException {
        if (getName() == null) {
            return getValue();
        }

        return BaseRenderObjectTag.lookup(this.pageContext, getName(), getProperty(), getScope());
    }

    protected Class getConverterClass() throws JspException {
        if (getConverter() == null) {
            return null;
        }

        try {
            String converterName = getConverter();
            return Class.forName(converterName);
        } catch (ClassNotFoundException e) {
            throw new JspException("converter class '" + getConverter() + "' could not be found");
        }
    }

}
