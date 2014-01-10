package pt.ist.fenixWebFramework.renderers.model;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;

public class DefaultSchemaFactory extends SchemaFactory {

    @Override
    public Schema createSchema(Object object) {
        return createSchema(object == null ? Object.class : object.getClass());
    }

    @Override
    public Schema createSchema(Class type) {
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
