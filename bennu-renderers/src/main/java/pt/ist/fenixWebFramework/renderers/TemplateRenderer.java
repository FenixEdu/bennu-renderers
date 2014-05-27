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

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTemplate;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * This renderer allows you to delegate the presentation of an object to
 * a JSP page. This can be usefull if you want to incrementally replace
 * some page with the use of renderers, use some of the functionalities
 * available in the JSP template and that are provided by this
 * renderer and the <code>renderers-template.tld</code>, or present a value
 * in a complex and less orthodox way.
 * 
 * @author cfgi
 */
public class TemplateRenderer extends OutputRenderer {

    private String template;

    public String getTemplate() {
        return template;
    }

    /**
     * The location of the template page that ill be used to present the
     * value. This location if relative to the application context but must
     * begin with a /.
     * 
     * @property
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                return new HtmlTemplate(getTemplate(), getContext().getMetaObject());
            }

        };
    }

}
