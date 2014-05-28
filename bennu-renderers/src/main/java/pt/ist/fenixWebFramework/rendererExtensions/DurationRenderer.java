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

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Duration;
import org.joda.time.TimeOfDay;

import pt.ist.fenixWebFramework.renderers.DateRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

public class DurationRenderer extends DateRenderer {

    private boolean showFormat;

    private Duration duration;

    public DurationRenderer() {
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
        String format = "HH:mm:ss";
        return format.trim();
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        duration = (Duration) object;
        DateTime dateTime = TimeOfDay.MIDNIGHT.toDateTimeToday().plus(duration.toPeriod());
        final Date date = dateTime != null ? convertDateTimeToCalendar(dateTime).getTime() : null;

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

    private Calendar convertDateTimeToCalendar(DateTime dt) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR_OF_DAY, dt.get(DateTimeFieldType.hourOfDay()));
        calendar.set(Calendar.MINUTE, dt.get(DateTimeFieldType.minuteOfHour()));
        calendar.set(Calendar.SECOND, dt.get(DateTimeFieldType.secondOfMinute()));
        return calendar;
    }
}
