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

public class ValidatorTag extends TagSupport implements PropertyContainerTag {

    private ValidatorContainerTag parent;

    private String name;

    @Override
    public void release() {
        super.release();

        this.name = null;
        this.parent = null;
    }

    public void setClass(String name) {
        this.name = name;
    }

    @Deprecated
    public String getName() {
        return name;
    }

    @Deprecated
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int doStartTag() throws JspException {
        this.parent = (ValidatorContainerTag) findAncestorWithClass(this, ValidatorContainerTag.class);

        if (this.parent == null) {
            throw new RuntimeException("validator tag can only be used inside an input tag or a schema slot description tag");
        }

        String name = this.name;
        if (name != null) {
            this.parent.addValidator(this.name);
        }

        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    @Override
    public void addProperty(String name, String value) {
        this.parent.addValidatorProperty(this.name, name, value);
    }

}
