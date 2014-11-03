package pt.ist.fenixWebFramework.renderers.validators;

import java.io.Serializable;
import java.util.Properties;

public class ValidatorProperties implements Serializable {

    private static final long serialVersionUID = -928420849795251675L;

    private final Class<HtmlValidator> type;
    private final Properties properties;

    public ValidatorProperties(Class<HtmlValidator> type, Properties properties) {
        super();
        this.type = type;
        this.properties = properties;
    }

    public Class<HtmlValidator> getType() {
        return type;
    }

    public Properties getProperties() {
        return properties;
    }

}
