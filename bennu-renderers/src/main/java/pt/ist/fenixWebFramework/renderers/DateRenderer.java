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
package pt.ist.fenixWebFramework.renderers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.fenixedu.commons.i18n.I18N;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * The renderer allows you to present dates in a simple way.
 * 
 * @author cfgi
 */
public class DateRenderer extends OutputRenderer {

    private static final String DEFAULT_FORMAT = "dd/MM/yyyy";

    private String format;

    /**
     * The format in which the date should be displayed. The format can
     * have the form accepted by {@link SimpleDateFormat}.
     * 
     * <p>
     * The default format is {@value #DEFAULT_FORMAT}.
     * 
     * @property
     */
    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return this.format == null ? DEFAULT_FORMAT : format;
    }

    public boolean isFormatSet() {
        return this.format != null;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                Date date = (Date) object;

                if (date == null) {
                    return new HtmlText();
                }

                DateFormat dateFormat = new SimpleDateFormat(getFormat(), I18N.getLocale());

                return new HtmlText(dateFormat.format(date));
            }

        };
    }
}
