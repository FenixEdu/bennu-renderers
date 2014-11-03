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
package pt.ist.fenixWebFramework.renderers.model;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;
import pt.ist.fenixframework.DomainModelUtil;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.dml.DomainClass;
import pt.ist.fenixframework.dml.Slot;

public final class SchemaFactory {

    public static Schema create(Object object) {
        if (object instanceof DomainObject) {
            return getSchemaForDomainObject(object.getClass());
        } else if (object instanceof DomainClass) {
            return getSchemaForDomainObject(((DomainClass) object).getFullName());
        }
        return create(object == null ? Object.class : object.getClass());
    }

    public static Schema create(Class<?> type) {
        if (DomainObject.class.isAssignableFrom(type)) {
            return getSchemaForDomainObject(type);
        }

        Schema schema = new Schema(type);

        if (Collection.class.isAssignableFrom(type)) {
            return schema;
        }

        List<String> filteredSlots = Arrays.asList(new String[] { "class" });
        List<PropertyDescriptor> descriptors =
                new ArrayList<PropertyDescriptor>(Arrays.asList(PropertyUtils.getPropertyDescriptors(type)));

        for (PropertyDescriptor descriptor : descriptors) {
            if (!filteredSlots.contains(descriptor.getName())) {
                schema.addSlotDescription(new SchemaSlotDescription(descriptor.getName()));
            }
        }

        return schema;
    }

    private static Schema getSchemaForDomainObject(String name) {
        try {
            return getSchemaForDomainObject(Class.forName(name));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot create schema for unknown class: " + name, e);
        }
    }

    private static Schema getSchemaForDomainObject(Class<?> type) {
        DomainClass domainClass = DomainModelUtil.getDomainClassFor((Class<? extends DomainObject>) type);

        Schema schema = new Schema(type);

        while (domainClass != null) {
            for (Slot slot : domainClass.getSlotsList()) {
                schema.addSlotDescription(new SchemaSlotDescription(slot.getName()));
            }
            domainClass = (DomainClass) domainClass.getSuperclass();
        }

        return schema;
    }

}
