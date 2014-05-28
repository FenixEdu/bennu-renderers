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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;

public class SchemaConfigTag extends BodyTagSupport {

    private BaseRenderObjectTag parent = null;

    private String type;

    private String bundle;

    private List<SchemaSlotDescription> slots;

    public SchemaConfigTag() {
    }

    @Override
    public int doStartTag() throws JspException {
        this.slots = new ArrayList<SchemaSlotDescription>();
        this.parent = (BaseRenderObjectTag) findAncestorWithClass(this, BaseRenderObjectTag.class);

        if (this.parent == null) {
            throw new RuntimeException("layout tag can only be used inside a renderer tag");
        }

        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        Schema anonymousSchema = null;
        try {
            anonymousSchema = new Schema(null, Class.forName(getType()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (SchemaSlotDescription slot : getSlots()) {
            anonymousSchema.addSlotDescription(slot);
        }

        this.parent.setAnonymousSchema(anonymousSchema);

        release();
        return EVAL_PAGE;
    }

    @Override
    public void release() {
        this.parent = null;
        this.slots = null;
        super.release();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SchemaSlotDescription> getSlots() {
        return slots;
    }

    public void addSlots(SchemaSlotDescription slot) {
        this.slots.add(slot);
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

}
