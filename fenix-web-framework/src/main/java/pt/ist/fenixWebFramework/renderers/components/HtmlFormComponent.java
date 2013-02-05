package pt.ist.fenixWebFramework.renderers.components;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.controllers.Controllable;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlController;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.components.converters.Convertible;
import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;

public abstract class HtmlFormComponent extends HtmlComponent implements Convertible, Controllable, SlotChanger, Validatable {

    public static int COMPONENT_NUMBER = 0;

    private String name;

    private Converter converter;

    private HtmlController controller;

    private MetaSlotKey slotKey;

    private boolean disabled;

    private HtmlChainValidator chainValidator;

    public HtmlFormComponent() {
        super();

        this.name = getNewName();
        this.disabled = false;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this form component. If the name does not obey the rules
     * for component names then it may be transformed to become conformat. Thus {@link #getName()} may return a value that is not
     * equal to the name given
     * in this method.
     * 
     * @param name
     *            the desired name
     */
    public void setName(String name) {
        this.name = getValidIdOrName(name);
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    @Override
    public boolean hasConverter() {
        return this.converter != null;
    }

    @Override
    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    public Object getConvertedValue() {
        return null;
    }

    @Override
    public Object getConvertedValue(MetaSlot slot) {
        return null;
    }

    @Override
    public HtmlChainValidator getChainValidator() {
        return this.chainValidator;
    }

    @Override
    public void setChainValidator(HtmlChainValidator validator) {
        if (getChainValidator() != null) {
            getChainValidator().addValidator(validator);
        } else {
            this.chainValidator = validator;
        }

    }

    @Override
    public void addValidator(HtmlValidator htmlValidator) {
        if (this.chainValidator == null) {
            this.chainValidator = new HtmlChainValidator(this);
        }
        this.chainValidator.addValidator(htmlValidator);
    }

    @Override
    public boolean hasController() {
        return controller != null;
    }

    @Override
    public HtmlController getController() {
        return controller;
    }

    @Override
    public void setController(HtmlController controller) {
        this.controller = controller;
        this.controller.setControlledComponent(this);
    }

    @Override
    public MetaSlotKey getTargetSlot() {
        return this.slotKey;
    }

    @Override
    public boolean hasTargetSlot() {
        return this.slotKey != null;
    }

    @Override
    public void setTargetSlot(MetaSlotKey key) {
        setName(key != null ? key.toString() : null);
        this.slotKey = key;
    }

    public void bind(MetaSlot slot) {
        if (slot != null) {
            setTargetSlot(slot.getKey());
        }
    }

    public void bind(MetaObject object, String slotName) {
        bind(object.getSlot(slotName));
    }

    public static String getNewName() {
        int number;

        synchronized (HtmlFormComponent.class) {
            number = HtmlFormComponent.COMPONENT_NUMBER++;
        }

        String name = Integer.toHexString(number);
        return "C" + name.toUpperCase();
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setAttribute("name", getName());

        return tag;
    }
}
