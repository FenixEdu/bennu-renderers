package pt.ist.fenixWebFramework.renderers.components.state;

import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public class ConversionMessage extends SlotMessage {

    public ConversionMessage(MetaSlot slot, String message) {
        super(Message.Type.CONVERSION, slot, message);
    }

}
