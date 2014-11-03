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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.fenixedu.commons.i18n.I18N;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.converters.DateConverter;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * This renderer provides a simple way of doing the input of a date. The date
 * is accepted from a text input field using a certain format. The
 * format beeing accepted is shown to the right.
 * 
 * <p>
 * Example: <input type="text" value="01/02/3456"/> dd/MM/yyyy
 * 
 * @author cfgi
 */
public class DateInputRenderer extends TextFieldRenderer {

    private String format;

    /**
     * The format in which the date should be displayed. The format can
     * have the form accepted by
     * <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
     * 
     * <p>
     * The default format is {@value DateConverter#DEFAULT_FORMAT}.
     * 
     * @property
     */
    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return this.format == null ? DateConverter.DEFAULT_FORMAT : format;
    }

    public boolean isFormatSet() {
        return this.format != null;
    }

    protected Locale getLocale() {
        return I18N.getLocale();
    }

    @Override
    protected HtmlComponent createTextField(Object object, Class type) {
        Date date = (Date) object;

        Locale locale = getLocale();
        SimpleDateFormat dateFormat = new SimpleDateFormat(getFormat(), locale);

        HtmlTextInput dateInput = new HtmlTextInput();

        if (date != null) {
            dateInput.setValue(dateFormat.format(date));
        }

        dateInput.setConverter(getDateConverter(dateFormat));

        HtmlContainer container = new HtmlInlineContainer();
        container.addChild(dateInput);
        container.addChild(new HtmlText(getFormatLabel()));

        return container;
    }

    @Override
    protected String getFormatLabel() {
        if (isKey()) {
            return RenderUtils.getResourceString(getBundle(), getFormatText());
        } else {
            if (getFormatText() != null) {
                return getFormatText();
            } else {
                return getFormat();
            }
        }
    }

    protected Converter getDateConverter(SimpleDateFormat dateFormat) {
        return new DateConverter(dateFormat);
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new DateTextFieldLayout();
    }

    class DateTextFieldLayout extends TextFieldLayout {

        @Override
        protected void setContextSlot(HtmlComponent component, MetaSlotKey slotKey) {
            HtmlContainer container = (HtmlContainer) component;

            super.setContextSlot(container.getChildren().get(0), slotKey);
        }

        @Override
        public void applyStyle(HtmlComponent component) {
            HtmlContainer container = (HtmlContainer) component;

            super.applyStyle(container.getChildren().get(0));
        }

    }

}
