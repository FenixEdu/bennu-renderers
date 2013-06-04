package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class RegexpValidator extends HtmlValidator {

    private String regexp;

    /**
     * Required constructor.
     */

    public RegexpValidator() {
        this(".*");
    }

    public RegexpValidator(String regex) {
        setRegexp(regex);
    }

    public RegexpValidator(HtmlChainValidator htmlChainValidator) {
        this(htmlChainValidator, ".*");
    }

    public RegexpValidator(HtmlChainValidator htmlChainValidator, String regexp) {
        super(htmlChainValidator);

        setRegexp(regexp);

        // default messsage
        setMessage("renderers.validator.regexp");
    }

    public String getRegexp() {
        return this.regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    @Override
    protected String getResourceMessage(String message) {
        return RenderUtils.getFormatedResourceString(getBundle(), message, new Object[] { getRegexp() });
    }

    public String getValue() {
        return getComponent().getValue();
    }

    @Override
    public void performValidation() {
        String text = getValue();
        setValid(text.matches(getRegexp()));
    }

    @Override
    public boolean hasJavascriptSupport() {
        return true;
    }

    @Override
    protected String getSpecificValidatorScript() {
        return "function(element) { var text = $(element).attr('value');" + "return text.length == 0 || text.match('"
                + getRegexp() + "');}";
    }

}