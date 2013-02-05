package pt.ist.fenixWebFramework.renderers.components;

import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;

public interface SlotChanger {
    public void setTargetSlot(MetaSlotKey key);

    public boolean hasTargetSlot();

    public MetaSlotKey getTargetSlot();
}
