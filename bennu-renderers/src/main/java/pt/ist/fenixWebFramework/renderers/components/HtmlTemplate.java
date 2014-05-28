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
package pt.ist.fenixWebFramework.renderers.components;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlIncludeTag;
import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

/**
 * This component is intended to as an abstraction to a certain page fragment.
 * 
 * @author cfgi
 */
public class HtmlTemplate extends HtmlComponent {
    private String template;
    transient private Object object;

    public HtmlTemplate(String template, Object object) {
        this.template = template;
        this.object = object;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        Map<String, Object> environment = new HashMap<String, Object>();
        environment.put(Constants.TEMPLATE_OBJECT_NAME, this.object);

        return new HtmlIncludeTag(context, template, environment);
    }
}
