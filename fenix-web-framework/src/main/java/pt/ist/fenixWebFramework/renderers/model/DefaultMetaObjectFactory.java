package pt.ist.fenixWebFramework.renderers.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;
import pt.ist.fenixWebFramework.renderers.schemas.Signature;
import pt.ist.fenixWebFramework.renderers.schemas.SignatureParameter;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;

public class DefaultMetaObjectFactory extends MetaObjectFactory {

    @Override
    public MetaObjectCollection createMetaObjectCollection() {
        return new MetaObjectCollection();
    }

    @Override
    public MetaObject createMetaObject(Object object, Schema schema) {
        if (object instanceof Collection) {
            MetaObjectCollection multipleMetaObject = createMetaObjectCollection();

            for (Iterator iter = ((Collection) object).iterator(); iter.hasNext();) {
                Object element = iter.next();

                multipleMetaObject.add(createOneMetaObject(element, schema));
            }

            return multipleMetaObject;
        } else {
            return createOneMetaObject(object, schema);
        }
    }

    @Override
    public MetaObject createMetaObject(Class type, Schema schema) {
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

    protected void setInstanceCreator(Class type, Schema schema, MetaObject metaObject) {
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

    protected void addSlotDescriptions(Schema schema, MetaObject metaObject) {
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

    protected void addCompositeSlotSetters(Schema schema, SimpleMetaObject metaObject) {
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

    protected MetaObject createOneMetaObject(Object object, Schema schema) {
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

    private boolean isPrimitiveObject(Object object) {
        Class[] primitiveTypes =
                new Class[] { String.class, Number.class, Integer.TYPE, Long.TYPE, Short.TYPE, Character.TYPE, Float.TYPE,
                        Double.TYPE, Date.class, Enum.class };

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

    @Override
    public MetaSlot createMetaSlot(MetaObject metaObject, SchemaSlotDescription slotDescription) {
        MetaSlot metaSlot;

        if (metaObject instanceof CreationMetaObject) {
            metaSlot = new MetaSlotWithDefault(metaObject, slotDescription.getSlotName());
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
        // metaSlot.setSetterIgnored(slotDescription.isSetterIgnored());

        return metaSlot;
    }

}
