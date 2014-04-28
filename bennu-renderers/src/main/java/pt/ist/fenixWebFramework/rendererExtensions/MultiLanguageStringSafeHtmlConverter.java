package pt.ist.fenixWebFramework.rendererExtensions;

import java.util.Locale;

import pt.ist.fenixWebFramework.rendererExtensions.MultiLanguageStringInputRenderer.MultiLanguageStringConverter;
import pt.ist.fenixWebFramework.rendererExtensions.htmlEditor.JsoupSafeHtmlConverter;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class MultiLanguageStringSafeHtmlConverter extends Converter {

    private final boolean mathJaxEnabled;

    public MultiLanguageStringSafeHtmlConverter(final boolean mathJaxEnabled) {
        this.mathJaxEnabled = mathJaxEnabled;
    }

    @Override
    public Object convert(Class type, Object value) {
        // SafeHtmlConverter safeConverter = new SafeHtmlConverter();
        Converter safeConverter = new JsoupSafeHtmlConverter(mathJaxEnabled);
        MultiLanguageStringConverter mlsConverter = new MultiLanguageStringConverter();

        MultiLanguageString mls = (MultiLanguageString) mlsConverter.convert(type, value);

        if (mls == null) {
            return null;
        }

        if (mls.getAllLocales().isEmpty()) {
            return null;
        }

        for (Locale locale : mls.getAllLocales()) {
            String text = (String) safeConverter.convert(String.class, mls.getContent(locale));

            if (text == null) {
                mls = mls.without(locale);
            } else {
                mls = mls.with(locale, text);
            }
        }

        return mls;
    }

}
