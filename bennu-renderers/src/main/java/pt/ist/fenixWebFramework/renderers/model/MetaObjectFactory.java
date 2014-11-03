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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import pt.ist.fenixWebFramework.rendererExtensions.factories.CreationDomainMetaObject;
import pt.ist.fenixWebFramework.rendererExtensions.factories.DomainMetaObject;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;
import pt.ist.fenixWebFramework.renderers.schemas.Signature;
import pt.ist.fenixWebFramework.renderers.schemas.SignatureParameter;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixframework.DomainObject;

public final class MetaObjectFactory {

    public static MetaObject createObject(Object object, Schema schema) {
        Schema usedSchema = schema;

        if (usedSchema == null && !(object instanceof Collection)) {
            usedSchema = SchemaFactory.create(object);
        }

        if (usedSchema == null && object instanceof Collection) {
            Collection collection = (Collection) object;

            if (!collection.isEmpty()) {
                usedSchema = SchemaFactory.create(collection.iterator().next());
            }
        }

        return createMetaObject(object, usedSchema);
    }

    public static MetaObject createObject(Class type, Schema schema) {
        Schema usedSchema = schema;

        if (usedSchema == null) {
            usedSchema = SchemaFactory.create(type);
        }

        return createMetaObject(type, usedSchema);
    }

    public static MetaSlot createSlot(MetaObject metaObject, SchemaSlotDescription slotDescription) {
        return createMetaSlot(metaObject, slotDescription);
    }

    private static MetaObject createMetaObject(Object object, Schema schema) {
        if (object instanceof Collection) {
            MetaObjectCollection multipleMetaObject = new MetaObjectCollection();

            for (Iterator iter = ((Collection) object).iterator(); iter.hasNext();) {
                Object element = iter.next();

                multipleMetaObject.add(createOneMetaObject(element, schema));
            }

            return multipleMetaObject;
        } else {
            return createOneMetaObject(object, schema);
        }
    }

    private static MetaObject createMetaObject(Class type, Schema schema) {
        if (DomainObject.class.isAssignableFrom(type)) {
            return createCreationMetaObject(type, schema);
        }
        CreationMetaObject metaObject;

        try {
            metaObject = new CreationMetaObject(type);
        } catch (Exception e) {
            throw new RuntimeException("could not create a new instance of " + type, e);
        }

        metaObject.setSchema(schema);

        addSlotDescriptions(schema, metaObject);
        setInstanceCreator(type, schema, metaObject);
        addCompositeSlotSetters(schema, metaObject);

        return metaObject;
    }

    private static MetaObject createCreationMetaObject(Class type, Schema schema) {
        CreationDomainMetaObject metaObject = new CreationDomainMetaObject(type);

        metaObject.setSchema(schema);

        addSlotDescriptions(schema, metaObject);
        setInstanceCreator(schema.getType(), schema, metaObject);
        addCompositeSlotSetters(schema, metaObject);

        return metaObject;
    }

    private static void setInstanceCreator(Class type, Schema schema, MetaObject metaObject) {
        Signature signature = schema.getConstructor();

        if (signature != null) {
            InstanceCreator creator = new InstanceCreator(type);

            for (SignatureParameter parameter : signature.getParameters()) {
                SchemaSlotDescription description = parameter.getSlotDescription();

                for (MetaSlot slot : metaObject.getAllSlots()) {
                    if (slot.getName().equals(description.getSlotName())) {
                        creator.addArgument(slot, parameter.getType());
                    }
                }
            }

            metaObject.setInstanceCreator(creator);
        }
    }

    private static void addSlotDescriptions(Schema schema, MetaObject metaObject) {
        List<SchemaSlotDescription> slotDescriptions = schema.getSlotDescriptions();
        for (SchemaSlotDescription description : slotDescriptions) {
            MetaSlot metaSlot = createMetaSlot(metaObject, description);

            if (!description.isHidden()) {
                metaObject.addSlot(metaSlot);
            } else {
                metaObject.addHiddenSlot(metaSlot);
            }
        }
    }

    private static void addCompositeSlotSetters(Schema schema, SimpleMetaObject metaObject) {
        for (Signature setterSignature : schema.getSpecialSetters()) {
            CompositeSlotSetter compositeSlotSetter = new CompositeSlotSetter(metaObject, setterSignature.getName());

            for (SignatureParameter parameter : setterSignature.getParameters()) {
                SchemaSlotDescription description = parameter.getSlotDescription();

                for (MetaSlot slot : metaObject.getAllSlots()) {
                    if (slot.getName().equals(description.getSlotName())) {
                        compositeSlotSetter.addArgument(slot, parameter.getType());
                    }
                }
            }

            metaObject.addCompositeSetter(compositeSlotSetter);
        }
    }

    private static MetaObject createOneMetaObject(Object object, Schema schema) {
        if (object instanceof DomainObject) {
            // persistent object
            return createDomainMetaObject(object, schema);
        }
        MetaObject result;

        if (isPrimitiveObject(object)) {
            result = new PrimitiveMetaObject(object);
        } else if (object != null && !(object instanceof Serializable)) {
            TransientMetaObject metaObject = new TransientMetaObject(object);
            addSlotDescriptions(schema, metaObject);
            result = metaObject;
        } else {
            SimpleMetaObject metaObject = new SimpleMetaObject(object);

            addSlotDescriptions(schema, metaObject);
            addCompositeSlotSetters(schema, metaObject);

            result = metaObject;
        }

        result.setSchema(schema);
        return result;
    }

    private static MetaObject createDomainMetaObject(Object object, Schema schema) {
        DomainMetaObject metaObject = new DomainMetaObject((DomainObject) object);

        metaObject.setSchema(schema);

        addSlotDescriptions(schema, metaObject);
        addCompositeSlotSetters(schema, metaObject);

        return metaObject;
    }

    private static final Class<?>[] primitiveTypes = new Class[] { String.class, Number.class, Integer.TYPE, Long.TYPE,
            Short.TYPE, Character.TYPE, Float.TYPE, Double.TYPE, Date.class, Enum.class };

    private static boolean isPrimitiveObject(Object object) {
        if (object == null) {
            return true;
        }

        for (Class type : primitiveTypes) {
            if (type.isAssignableFrom(object.getClass())) {
                return true;
            }
        }

        return false;
    }

    private static MetaSlot createMetaSlot(MetaObject metaObject, SchemaSlotDescription slotDescription) {
        MetaSlot metaSlot;

        if (metaObject instanceof CreationMetaObject || metaObject instanceof CreationDomainMetaObject) {
            metaSlot = new MetaSlotWithDefault(metaObject, slotDescription.getSlotName());
        } else if (metaObject instanceof DomainMetaObject) {
            metaSlot = new MetaSlot(metaObject, slotDescription.getSlotName());
        } else {
            metaSlot = new MetaSlot(metaObject, slotDescription.getSlotName());
        }

        metaSlot.setLabelKey(slotDescription.getKey());
        metaSlot.setLabelArg0(slotDescription.getArg0());
        metaSlot.setBundle(slotDescription.getBundle());
        metaSlot.setSchema(RenderKit.getInstance().findSchema(slotDescription.getSchema()));
        metaSlot.setLayout(slotDescription.getLayout());
        metaSlot.setValidators(slotDescription.getValidators());
        metaSlot.setDefaultValue(slotDescription.getDefaultValue());
        metaSlot.setProperties(slotDescription.getProperties());
        metaSlot.setConverter(slotDescription.getConverter());
        metaSlot.setReadOnly(slotDescription.isReadOnly());
        metaSlot.setHelpLabel(slotDescription.getHelpLabel());
        metaSlot.setDescription(slotDescription.getDescription());
        metaSlot.setDescriptionFormat(slotDescription.getDescriptionFormat());

        return metaSlot;
    }

}
