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

import pt.ist.fenixWebFramework.rendererExtensions.factories.FenixSchemaFactory;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;

public abstract class SchemaFactory {

    private static final SchemaFactory currentFactory = new FenixSchemaFactory();

    public static Schema create(Object object) {
        return currentFactory.createSchema(object);
    }

    public static Schema create(Class<?> type) {
        return currentFactory.createSchema(type);
    }

    public Schema createSchema(Object object) {
        return createSchema(object == null ? Object.class : object.getClass());
    }

    public Schema createSchema(Class<?> type) {
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
}
