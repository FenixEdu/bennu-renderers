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

import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectCollection;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.schemas.SchemaSlotDescription;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixframework.DomainObject;

public class FenixMetaObjectFactory extends MetaObjectFactory {

    @Override
    public MetaObjectCollection createMetaObjectCollection() {
        return new DomainMetaObjectCollection();
    }

    @Override
    protected MetaObject createOneMetaObject(Object object, Schema schema) {
        if (object instanceof DomainObject) {
            // persistent object
            return createDomainMetaObject(object, schema);
        } else {
            // standard object
            return super.createOneMetaObject(object, schema);
        }
    }

    @Override
    public MetaObject createMetaObject(Class type, Schema schema) {
        if (DomainObject.class.isAssignableFrom(type)) {
            return createCreationMetaObject(type, schema);
        } else {
            return super.createMetaObject(type, schema);
        }
    }

    private MetaObject createDomainMetaObject(Object object, Schema schema) {
        DomainMetaObject metaObject = new DomainMetaObject((DomainObject) object);

        metaObject.setSchema(schema);

        addSlotDescriptions(schema, metaObject);
        addCompositeSlotSetters(schema, metaObject);

        return metaObject;
    }

    private MetaObject createCreationMetaObject(Class type, Schema schema) {
        CreationDomainMetaObject metaObject = new CreationDomainMetaObject(type);

        metaObject.setSchema(schema);

        addSlotDescriptions(schema, metaObject);
        setInstanceCreator(schema.getType(), schema, metaObject);
        addCompositeSlotSetters(schema, metaObject);

        return metaObject;
    }

    @Override
    public MetaSlot createMetaSlot(MetaObject metaObject, SchemaSlotDescription slotDescription) {
        MetaSlot metaSlot;

        if (metaObject instanceof CreationDomainMetaObject) {
            metaSlot = new MetaSlotWithDefault(metaObject, slotDescription.getSlotName());
        } else if (metaObject instanceof DomainMetaObject) {
            metaSlot = new MetaSlot(metaObject, slotDescription.getSlotName());
        } else {
            metaSlot = super.createMetaSlot(metaObject, slotDescription);
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
        // metaSlot.setSetterIgnored(slotDescription.isSetterIgnored());

        return metaSlot;
    }
}
