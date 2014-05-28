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

import java.util.Date;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.renderers.DateRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * Renders a DateTime as a simple Date. This renderer convers the incoming
 * DateTime into a Date and then presents the Date in a standard way.
 * 
 * @author cfgi
 */
public class DateTimeAsDateRenderer extends DateRenderer {

    @Override
    protected Layout getLayout(Object object, Class type) {
        Date date = object == null ? null : ((DateTime) object).toDate();

        return super.getLayout(date, Date.class);
    }

    @Override
    protected HtmlComponent renderComponent(Layout layout, Object object, Class type) {
        Date date = object == null ? null : ((DateTime) object).toDate();

        return super.renderComponent(layout, date, Date.class);
    }

}
