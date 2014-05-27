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
package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class RegexpValidator extends HtmlValidator {

    private String regexp;

    /**
     * Required constructor.
     */

    public RegexpValidator() {
        this(".*");
    }

    public RegexpValidator(String regex) {
        setRegexp(regex);
    }

    public RegexpValidator(HtmlChainValidator htmlChainValidator) {
        this(htmlChainValidator, ".*");
    }

    public RegexpValidator(HtmlChainValidator htmlChainValidator, String regexp) {
        super(htmlChainValidator);

        setRegexp(regexp);

        // default messsage
        setMessage("renderers.validator.regexp");
    }

    public String getRegexp() {
        return this.regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    @Override
    protected String getResourceMessage(String message) {
        return RenderUtils.getFormatedResourceString(getBundle(), message, new Object[] { getRegexp() });
    }

    public String getValue() {
        return getComponent().getValue();
    }

    @Override
    public void performValidation() {
        String text = getValue();
        setValid(text.matches(getRegexp()));
    }

    @Override
    public boolean hasJavascriptSupport() {
        return true;
    }

    @Override
    protected String getSpecificValidatorScript() {
        return "function(element) { var text = $(element).attr('value');" + "return text.length == 0 || text.match('"
                + getRegexp() + "');}";
    }

}