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
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyTag extends BodyTagSupport {
    private static final Logger logger = LoggerFactory.getLogger(PropertyTag.class);

    private String name = null;

    private String value = null;

    public PropertyTag() {
    }

    @Override
    public void release() {
        super.release();

        this.name = null;
        this.value = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int doStartTag() throws JspException {
        if (getValue() != null) {
            return SKIP_BODY;
        } else {
            return EVAL_BODY_BUFFERED;
        }
    }

    @Override
    public int doEndTag() throws JspException {
        PropertyContainerTag parent = (PropertyContainerTag) findAncestorWithClass(this, PropertyContainerTag.class);

        if (parent != null) {
            if (getValue() != null) {
                parent.addProperty(getName(), getValue());
            } else {
                parent.addProperty(getName(), getBodyContent().getString());
            }
        } else {
            logger.warn("property tag was using inside an invalid container\ncould not set property: " + getName() + "="
                    + getValue());
        }

        return super.doEndTag();
    }

}
