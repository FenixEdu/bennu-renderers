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
package pt.ist.fenixWebFramework.renderers.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import pt.ist.fenixWebFramework.renderers.components.HtmlHiddenField;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.components.state.LifeCycleConstants;
import pt.ist.fenixWebFramework.renderers.components.state.ViewState;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;

public class ContextTag extends TagSupport {

    private List<IViewState> viewStates;
    private MetaObject metaObject;

    public ContextTag() {
        super();

        this.viewStates = new ArrayList<IViewState>();
    }

    @Override
    public void release() {
        super.release();

        this.viewStates = new ArrayList<IViewState>();
        this.metaObject = null;
    }

    @Override
    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            if (!this.viewStates.isEmpty()) {
                HtmlHiddenField hidden = new HtmlHiddenField(LifeCycleConstants.VIEWSTATE_PARAM_NAME, encodeViewStates());

                hidden.draw(this.pageContext);
            }
        } catch (IOException e) {
            throw new JspException(e);
        }

        release();
        return EVAL_PAGE;
    }

    private String encodeViewStates() throws IOException {
        return ViewState.encodeToBase64(this.viewStates);
    }

    public void addViewState(IViewState viewState) {
        this.viewStates.add(viewState);
    }

    public void setMetaObject(MetaObject metaObject) {
        this.metaObject = metaObject;
    }

    public MetaObject getMetaObject() {
        return this.metaObject;
    }
}
