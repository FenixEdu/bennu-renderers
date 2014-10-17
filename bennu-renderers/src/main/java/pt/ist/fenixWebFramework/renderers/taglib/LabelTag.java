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

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.fenixedu.commons.i18n.I18N;

import pt.ist.fenixWebFramework.renderers.components.Constants;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.utils.RenderersMessageSource;

public class LabelTag extends BodyTagSupport {

    private String property;

    private String bundle;

    private String key;

    private String arg0;

    public LabelTag() {
        super();
    }

    public String getBundle() {
        return this.bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public int doEndTag() throws JspException {
        MetaObject object = (MetaObject) pageContext.findAttribute(Constants.TEMPLATE_OBJECT_NAME);

        if (object == null) {
            throw new JspException("This tag can only be used inside a render template.");
        }

        if (getProperty() != null) {
            MetaSlot slot = findMetaSlot(object, getProperty());

            if (slot != null) {
                if (getKey() != null) {
                    if (getArg0() == null) {
                        write(RenderUtils.getSlotLabel(object.getType(), getProperty(), getBundle(), getKey()));
                    } else {
                        write(RenderUtils.getSlotLabel(object.getType(), getProperty(), getBundle(), getKey(), getArg0()));
                    }
                } else if (getBundle() != null) {
                    if (getArg0() == null) {
                        write(RenderUtils.getSlotLabel(object.getType(), getProperty(), getBundle(), slot.getLabelKey()));
                    } else {
                        write(RenderUtils.getSlotLabel(object.getType(), getProperty(), getBundle(), slot.getLabelKey(),
                                getArg0()));
                    }
                } else {
                    write(slot.getLabel());
                }
            } else {
                write(RenderUtils.getSlotLabel(object.getType(), getProperty(), getBundle(), getKey()));
            }
        } else if (getKey() != null) {
            RenderersMessageSource resources = RenderUtils.getMessageResources(getBundle());
            write(resources.getMessage(I18N.getLocale(), getKey()).orElse("!" + getKey() + "!"));
        } else {
            throw new JspException("must specify a property or a key");
        }

        return EVAL_PAGE;
    }

    protected void write(String string) throws JspException {
        try {
            pageContext.getOut().write(string);
        } catch (IOException e) {
            throw new JspException("could not write to page: ", e);
        }
    }

    private MetaSlot findMetaSlot(MetaObject object, String property) {
        List<MetaSlot> slots = object.getSlots();

        for (MetaSlot slot : slots) {
            if (slot.getName().equals(property)) {
                return slot;
            }
        }

        return null;
    }

    @Override
    public void release() {
        super.release();

        this.property = null;
        this.key = null;
    }

    public String getArg0() {
        return arg0;
    }

    public void setArg0(String arg0) {
        this.arg0 = arg0;
    }

}
