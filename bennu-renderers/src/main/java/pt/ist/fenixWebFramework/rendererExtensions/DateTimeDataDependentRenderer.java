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
/*
 * Author : Goncalo Luiz
 * Creation Date: Jul 26, 2006,10:56:35 AM
 */
package pt.ist.fenixWebFramework.rendererExtensions;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.renderers.FormatRenderer;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * @author <a href="mailto:goncalo@ist.utl.pt">Goncalo Luiz</a><br>
 * <br>
 *         Created on Jul 26, 2006,10:56:35 AM
 * 
 */
public class DateTimeDataDependentRenderer extends FormatRenderer {

    private String formatWithTime;
    private String formatWithoutTime;

    public String getFormatWithoutTime() {
        return formatWithoutTime;
    }

    public void setFormatWithoutTime(String formatWithoutTime) {
        this.formatWithoutTime = formatWithoutTime;
    }

    public String getFormatWithTime() {
        return formatWithTime;
    }

    public void setFormatWithTime(String formatWithTime) {
        this.formatWithTime = formatWithTime;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        if (object == null) {
            return super.getLayout(object, type);
        }

        DateTime dateTime = (DateTime) object;
        if (dateTime.getHourOfDay() == 0 && dateTime.getMinuteOfHour() == 0) {
            setFormat(getFormatWithoutTime());
        } else {
            setFormat(getFormatWithTime());
        }

        return super.getLayout(object, type);
    }

}
