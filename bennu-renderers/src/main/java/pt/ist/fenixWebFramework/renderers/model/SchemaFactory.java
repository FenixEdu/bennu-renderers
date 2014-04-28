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
