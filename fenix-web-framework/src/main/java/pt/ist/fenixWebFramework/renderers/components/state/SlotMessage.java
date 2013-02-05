package pt.ist.fenixWebFramework.renderers.components.state;

import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public class SlotMessage extends Message {

    private MetaSlot slot;

    protected SlotMessage(Type type, MetaSlot slot, String message) {
        super(type, message);

        setSlot(slot);
    }

    public MetaSlot getSlot() {
        return this.slot;
    }

    protected void setSlot(MetaSlot slot) {
        this.slot = slot;
    }

}
