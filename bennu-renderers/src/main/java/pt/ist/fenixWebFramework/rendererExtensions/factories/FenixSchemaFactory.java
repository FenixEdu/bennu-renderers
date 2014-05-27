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
package pt.ist.fenixWebFramework.rendererExtensions.factories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixWebFramework.renderers.model.SchemaFactory;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DomainClass;
import pt.ist.fenixframework.dml.Role;
import pt.ist.fenixframework.dml.Slot;

public class FenixSchemaFactory extends SchemaFactory {

    private static final Logger logger = LoggerFactory.getLogger(FenixSchemaFactory.class);

    @Override
    public Schema createSchema(Object object) {
        if (object instanceof DomainObject) {
            try {
                return FenixSchemaFactory.getSchemaForDomainObject(object.getClass().getName());
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        } else if (object instanceof DomainClass) {
            try {
                return FenixSchemaFactory.getSchemaForDomainObject(((DomainClass) object).getFullName());
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return super.createSchema(object);
    }

    @Override
    public Schema createSchema(Class<?> type) {
        if (DomainObject.class.isAssignableFrom(type)) {
            try {
                return FenixSchemaFactory.getSchemaForDomainObject(type.getName());
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return super.createSchema(type);
    }

    public static List<Slot> getDomainClassSlots(DomainClass domainClass) {
        List<Slot> slots = new ArrayList<Slot>();

        while (domainClass != null) {
            for (Iterator<Slot> iter = domainClass.getSlots(); iter.hasNext();) {
                slots.add(iter.next());
            }
            domainClass = (DomainClass) domainClass.getSuperclass();
        }

        return slots;
    }

    public static List<Role> getDomainClassRoles(DomainClass domainClass) {
        List<Role> roles = new ArrayList<Role>();

        while (domainClass != null) {
            for (Iterator<Role> iter = domainClass.getRoleSlots(); iter.hasNext();) {
                Role role = iter.next();

                roles.add(role);
            }

            domainClass = (DomainClass) domainClass.getSuperclass();
        }

        return roles;
    }

    public static Schema getSchemaForDomainObject(String name) throws ClassNotFoundException {
        DomainClass domainClass = FenixFramework.getDomainModel().findClass(name);

        Schema schema = new Schema(Class.forName(domainClass.getFullName()));

        List<Slot> slots = getDomainClassSlots(domainClass);
        for (Slot slot : slots) {
            schema.addSlotDescription(new SchemaSlotDescription(slot.getName()));
        }

        // List<Role> roles = getDomainClassRoles(domainClass);
        // for (Role role : roles) {
        // schema.addSlotDescription(new SchemaSlotDescription(role.getName()));
        // }

        return schema;
    }

}
