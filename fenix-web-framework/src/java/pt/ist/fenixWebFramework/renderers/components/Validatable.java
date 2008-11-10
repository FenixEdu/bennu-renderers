package pt.ist.fenixWebFramework.renderers.components;

import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;

public interface Validatable {
    public String getValue();

    public String[] getValues();

    public void setChainValidator(HtmlChainValidator htmlChainValidator);

    public void addValidator(HtmlValidator htmlValidator);

    public HtmlChainValidator getChainValidator();
}
