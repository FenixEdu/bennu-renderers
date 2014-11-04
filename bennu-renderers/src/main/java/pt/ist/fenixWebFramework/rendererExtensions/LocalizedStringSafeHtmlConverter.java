/**
 * Copyright © 2008 Instituto Superior Técnico
 *
 * This file is part of Bennu Renderers Framework.
 *
 * Bennu Renderers Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Renderers Framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Renderers Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixWebFramework.rendererExtensions;

import java.util.Locale;

import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixWebFramework.rendererExtensions.LocalizedStringInputRenderer.LocalizedStringConverter;
import pt.ist.fenixWebFramework.rendererExtensions.htmlEditor.JsoupSafeHtmlConverter;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class LocalizedStringSafeHtmlConverter extends Converter {

    public LocalizedStringSafeHtmlConverter() {
    }

    @Override
    public Object convert(Class type, Object value) {
        Converter safeConverter = new JsoupSafeHtmlConverter();
        LocalizedStringConverter mlsConverter = new LocalizedStringConverter();

        LocalizedString mls = getLocalized(mlsConverter.convert(type, value));

        if (mls == null) {
            return null;
        }

        if (mls.getLocales().isEmpty()) {
            return null;
        }

        for (Locale locale : mls.getLocales()) {
            String text = (String) safeConverter.convert(String.class, mls.getContent(locale));

            if (text == null) {
                mls = mls.without(locale);
            } else {
                mls = mls.with(locale, text);
            }
        }

        return processLocalized(mls);
    }

    public Object processLocalized(LocalizedString object) {
        return object;
    }

    protected LocalizedString getLocalized(Object object) {
        if (object instanceof LocalizedString) {
            return (LocalizedString) object;
        } else {
            return null;
        }
    }

}
