package pt.ist.fenixWebFramework.renderers.model;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.bennu.core.domain.User;

/**
 * The MetaObjectCollection provides a wrapper for several meta objects.
 * It's purpose, besides behaving like a collection of objects or better a list,
 * is to coordinate the {@link #commit()} operation for all the meta objects
 * contained in this MetaObjectCollection.
 * 
 * @author cfgi
 */
public class MetaObjectCollection extends MetaObject {

    private final List<MetaObject> metaObjects;

    public MetaObjectCollection() {
        super();

        this.metaObjects = new ArrayList<MetaObject>();
    }

    public List<MetaObject> getAllMetaObjects() {
        return this.metaObjects;
    }

    public void add(MetaObject metaObject) {
        this.metaObjects.add(metaObject);
    }

    public boolean remove(MetaObject metaObject) {
        return this.metaObjects.remove(metaObject);
    }

    @Override
    public void setUser(User user) {
        super.setUser(user);

        for (MetaObject metaObject : getAllMetaObjects()) {
            metaObject.setUser(user);
        }
    }

    @Override
    public Object getObject() {
        List<Object> objects = new ArrayList<Object>();

        for (MetaObject metaObject : getAllMetaObjects()) {
            objects.add(metaObject.getObject());
        }

        return objects;
    }

    /**
     * @return <code>ArrayList.class</code>
     */
    @Override
    public Class getType() {
        return ArrayList.class;
    }

    @Override
    public List<MetaSlot> getSlots() {
        List<MetaSlot> slots = new ArrayList<MetaSlot>();

        for (MetaObject metaObject : getAllMetaObjects()) {
            slots.addAll(metaObject.getSlots());
        }

        return slots;
    }

    @Override
    public void addSlot(MetaSlot slot) {
    }

    @Override
    public boolean removeSlot(MetaSlot slot) {
        return false;
    }

    @Override
    public MetaObjectKey getKey() {
        return null;
    }

    @Override
    public List<MetaSlot> getHiddenSlots() {
        List<MetaSlot> slots = new ArrayList<MetaSlot>();

        for (MetaObject metaObject : getAllMetaObjects()) {
            slots.addAll(metaObject.getHiddenSlots());
        }

        return slots;
    }

    @Override
    public void addHiddenSlot(MetaSlot slot) {
    }

    @Override
    public void commit() {
        for (MetaObject metaObject : getAllMetaObjects()) {
            metaObject.commit();
        }
    }
}
