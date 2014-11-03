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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.controllers.Controllable;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlController;
import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class HtmlForm extends HtmlComponent implements Controllable {

    public static final String POST = "post";
    public static final String GET = "get";

    public static final String URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String FORM_DATA = "multipart/form-data";

    private String action;
    private String method;
    private String encoding;
    private HtmlComponent body;
    private final List<HtmlHiddenField> hiddenFields;
    private HtmlSubmitButton submitButton;
    private HtmlCancelButton cancelButton;

    private HtmlController controller;

    public HtmlForm() {
        super();

        this.hiddenFields = new ArrayList<HtmlHiddenField>();

        setSubmitButton(new HtmlSubmitButton(RenderUtils.getResourceString("renderers.form.submit.name")));
        setCancelButton(new HtmlCancelButton(RenderUtils.getResourceString("renderers.form.cancel.name")));

        setEncoding(URL_ENCODED);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public HtmlComponent getBody() {
        return this.body;
    }

    public void setBody(HtmlComponent component) {
        this.body = component;
    }

    public List<HtmlHiddenField> getHiddenFields() {
        return this.hiddenFields;
    }

    public void addHiddenField(HtmlHiddenField htmlHiddenField) {
        this.hiddenFields.add(htmlHiddenField);
    }

    public HtmlCancelButton getCancelButton() {
        return this.cancelButton;
    }

    public void setCancelButton(HtmlCancelButton cancelButton) {
        configureButton(this.cancelButton = cancelButton);
    }

    public HtmlSubmitButton getSubmitButton() {
        return submitButton;
    }

    public void setSubmitButton(HtmlSubmitButton submitButton) {
        configureButton(this.submitButton = submitButton);
    }

    protected void configureButton(HtmlSubmitButton button) {
        if (button != null) {
            button.setClasses("inputbutton");
        }
    }

    @Override
    public List<HtmlComponent> getChildren() {
        List<HtmlComponent> children = super.getChildren();

        children.addAll(getHiddenFields());

        if (this.body != null) {
            children.add(this.body);
        }

        if (this.submitButton != null) {
            children.add(this.submitButton);
        }

        if (this.cancelButton != null) {
            children.add(this.cancelButton);
        }

        return children;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        addClass("form-horizontal");
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("form");

        tag.setAttribute("role", "form");

        tag.setAttribute("action", this.action);
        tag.setAttribute("method", this.method);
        tag.setAttribute("enctype", this.encoding);

        for (HtmlHiddenField field : this.hiddenFields) {
            tag.addChild(field.getOwnTag(context));
        }

        if (this.body != null) {
            tag.addChild(this.body.getOwnTag(context));
        }

        HtmlBlockContainer formGroup = new HtmlBlockContainer();
        formGroup.setClasses("form-group");
        HtmlBlockContainer buttonContainer = new HtmlBlockContainer();
        formGroup.addChild(buttonContainer);
        buttonContainer.setClasses("col-sm-offset-2 col-sm-10");

        if (this.submitButton != null) {
            buttonContainer.addChild(this.submitButton);
        }

        if (this.cancelButton != null) {
            buttonContainer.addChild(this.cancelButton);
        }

        tag.addChild(formGroup.getOwnTag(context));

        return tag;
    }

    @Override
    public boolean hasController() {
        return controller != null;
    }

    @Override
    public HtmlController getController() {
        return controller;
    }

    @Override
    public void setController(HtmlController controller) {
        this.controller = controller;
        this.controller.setControlledComponent(this);
    }
}
