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

import pt.ist.fenixWebFramework.renderers.components.HtmlFormComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.Validatable;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public abstract class HtmlValidator extends AbstractHtmlValidator {

    private HtmlChainValidator htmlChainValidator;

    private String message;

    private boolean isKey;

    private String bundle;

    private String clearHandler;

    private String errorHandler;

    protected HtmlValidator() {
        super();
        setKey(true);
        setBundle("RENDERER_RESOURCES");
    }

    public HtmlValidator(HtmlChainValidator htmlChainValidator) {
        this();
        htmlChainValidator.addValidator(this);
        this.htmlChainValidator = htmlChainValidator;
    }

    @Override
    public Validatable getComponent() {
        return this.htmlChainValidator.getComponent();
    }

    public String getClearHandler() {
        return clearHandler;
    }

    public void setClearHandler(String clearHandler) {
        this.clearHandler = clearHandler;
    }

    public String getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(String errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getErrorMessage() {
        if (isKey()) {
            String errorMessage = getResourceMessage(getMessage());

            if (errorMessage != null) {
                return errorMessage;
            } else {
                return getMessage();
            }
        } else {
            return getMessage();
        }
    }

    protected String getResourceMessage(String message) {
        return RenderUtils.getResourceString(getBundle(), message);
    }

    protected void setHtmlChainValidator(HtmlChainValidator htmlChainValidator) {
        this.htmlChainValidator = htmlChainValidator;
    }

    @Override
    public boolean isKey() {
        return this.isKey;
    }

    public void setKey(boolean isKey) {
        this.isKey = isKey;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public boolean hasJavascriptSupport() {
        return false;
    }

    public HtmlScript bindJavascript(HtmlFormComponent formComponent) {
        HtmlScript script = new HtmlScript();
        String escapeId = getValidatableId(formComponent);

        String bindTo = bindJavascriptEventsTo(formComponent);

        script.setScript("$(\"#" + escapeId + "\").validate({ " + (bindTo != null ? "bindEventsTo: \"" + bindTo + "\"," : "")
                + "validationHandler: " + getSpecificValidatorScript() + ", errorMessage: \"" + getJavascriptErrorMessage()
                + "\"" + (getClearHandler() != null ? ", clearHandler: " + getClearHandler() : "")
                + (getErrorHandler() != null ? ", errorHandler: " + getErrorHandler() : "") + "});");

        return script;
    }

    protected String getValidatableId(HtmlFormComponent formComponent) {
        return RenderUtils.escapeId(formComponent.getId());
    }

    protected String getSpecificValidatorScript() {
        return "";
    }

    protected String getJavascriptErrorMessage() {
        return getErrorMessage();
    }

    protected String bindJavascriptEventsTo(HtmlFormComponent formComponent) {
        return null;
    }
}
