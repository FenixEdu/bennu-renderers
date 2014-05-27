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

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTimeFieldType;
import org.joda.time.base.AbstractPartial;

import pt.ist.fenixWebFramework.renderers.DateRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

public class PartialRenderer extends DateRenderer {

    private boolean showFormat;
    private AbstractPartial partial;

    public PartialRenderer() {
        super();
        this.showFormat = false;
    }

    public boolean isShowFormat() {
        return showFormat;
    }

    /**
     * 
     * @property
     */
    public void setShowFormat(boolean showFormat) {
        this.showFormat = showFormat;
    }

    @Override
    public String getFormat() {
        if (isFormatSet()) {
            return super.getFormat();
        }

        String format = "dd/MM/yyyy HH:mm:ss";

        if (!this.partial.isSupported(DateTimeFieldType.dayOfMonth())) {
            format = format.replace("dd/", "");
        }

        if (!this.partial.isSupported(DateTimeFieldType.monthOfYear())) {
            format = format.replace("MM/", "");
        }

        if (!this.partial.isSupported(DateTimeFieldType.year())) {
            if (this.partial.isSupported(DateTimeFieldType.dayOfMonth())
                    || this.partial.isSupported(DateTimeFieldType.monthOfYear())) {
                format = format.replace("/yyyy", "");
            } else {
                format = format.replace("yyyy", "");
            }
        }

        if (!this.partial.isSupported(DateTimeFieldType.hourOfDay())) {
            format = format.replace("HH:", "");
        }

        if (!this.partial.isSupported(DateTimeFieldType.minuteOfHour())) {
            format = format.replace("mm:", "");
        }

        if (!this.partial.isSupported(DateTimeFieldType.secondOfMinute())) {
            if (this.partial.isSupported(DateTimeFieldType.hourOfDay())
                    || this.partial.isSupported(DateTimeFieldType.minuteOfHour())) {
                format = format.replace(":ss", "");
            } else {
                format = format.replace("ss", "");
            }
        }

        return format.trim();
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        this.partial = (AbstractPartial) object;
        final Date date = this.partial != null ? convertPartialToCalendar(this.partial).getTime() : null;

        final Layout superLayout = super.getLayout(date, type);

        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                HtmlText text = (HtmlText) superLayout.createComponent(date, type);

                String formatText = isShowFormat() ? " (" + getFormat() + ")" : "";
                text.setText(text.getText() + formatText);

                return text;
            }

        };
    }

    private Calendar convertPartialToCalendar(AbstractPartial partial) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        DateTimeFieldType[] fieldTypes = partial.getFieldTypes();
        for (DateTimeFieldType fieldType : fieldTypes) {
            if (fieldType.equals(DateTimeFieldType.dayOfMonth())) {
                calendar.set(Calendar.DAY_OF_MONTH, partial.get(DateTimeFieldType.dayOfMonth()));
                continue;
            }

            if (fieldType.equals(DateTimeFieldType.monthOfYear())) {
                calendar.set(Calendar.MONTH, partial.get(DateTimeFieldType.monthOfYear()) - 1);
                continue;
            }

            if (fieldType.equals(DateTimeFieldType.year())) {
                calendar.set(Calendar.YEAR, partial.get(DateTimeFieldType.year()));
                continue;
            }

            if (fieldType.equals(DateTimeFieldType.hourOfDay())) {
                calendar.set(Calendar.HOUR_OF_DAY, partial.get(DateTimeFieldType.hourOfDay()));
                continue;
            }

            if (fieldType.equals(DateTimeFieldType.minuteOfHour())) {
                calendar.set(Calendar.MINUTE, partial.get(DateTimeFieldType.minuteOfHour()));
                continue;
            }

            if (fieldType.equals(DateTimeFieldType.secondOfMinute())) {
                calendar.set(Calendar.SECOND, partial.get(DateTimeFieldType.secondOfMinute()));
                continue;
            }
        }

        return calendar;
    }
}
