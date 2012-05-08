package pt.ist.fenixWebFramework.renderers.taglib;

public interface ValidatorContainerTag {

    public void addValidator(String validatorClassName);

    public void addValidatorProperty(String validatorClassName, String propertyName, String propertyValue);
}
