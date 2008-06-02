package pt.ist.fenixWebFramework;


public class Config extends pt.ist.fenixframework.Config {

    /**
     * This <strong>required</strong> parameter specifies the default
     * language that will be used to construct the default <code>java.util.Locale<code>
     * used by the frameworks localization tools.
     */
    protected String defaultLanguage = null;

    /**
     * This <strong>required</strong> parameter specifies the default
     * language that will be used to construct the default <code>java.util.Locale<code>
     * used by the frameworks localization tools.
     */
    protected String defaultLocation = null;

    /**
     * This <strong>optional</strong> parameter specifies the default
     * variant that will be used to construct the default <code>java.util.Locale<code>
     * used by the frameworks localization tools.
     */
    protected String defaultVariant = null;


    // TODO : make method in superclass visible in the hierarchy so we don't need to replicate the code here.
    private static void checkRequired(Object obj, String fieldName) {
        if (obj == null) {
            throw new Error("The required field '" + fieldName + "' was not specified in the FenixFramework config.");
        }
    }

    public void checkConfig() {
	super.checkConfig();
        checkRequired(defaultLanguage, "defaultLanguage");
        checkRequired(defaultLocation, "defaultLocation");
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getDefaultLocation() {
        return defaultLocation;
    }

    public String getDefaultVariant() {
        return defaultVariant;
    }

}
