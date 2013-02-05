package pt.ist.fenixWebFramework.renderers.components.state;

import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public class ValidationMessage extends SlotMessage {

    public ValidationMessage(MetaSlot slot, String message) {
        super(Message.Type.VALIDATION, slot, message);
    }

}
