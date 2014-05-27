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

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.components.state.LifeCycleConstants;
import pt.ist.fenixWebFramework.renderers.components.state.Message;
import pt.ist.fenixWebFramework.renderers.components.state.Message.Type;

public class HasMessagesTag extends TagSupport {

    private String forName;
    private String type;

    public String getFor() {
        return this.forName;
    }

    public void setFor(String forName) {
        this.forName = forName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void release() {
        super.release();

        this.forName = null;
        this.type = null;
    }

    @Override
    public int doStartTag() throws JspException {
        IViewState viewState = getViewStateWithId(this.pageContext, getFor());

        if (viewState == null) {
            return SKIP_BODY;
        } else if (!hasMessages(viewState, getType())) {
            return SKIP_BODY;
        } else {
            return EVAL_BODY_INCLUDE;
        }
    }

    private boolean hasMessages(IViewState viewState, String type) {
        if (type == null) {
            return !viewState.getMessages().isEmpty();
        } else {
            Type messageType = getMessageType();

            for (Message message : viewState.getMessages()) {
                if (messageType.equals(message.getType())) {
                    return true;
                }
            }

            return false;
        }
    }

    public Type getMessageType() {
        String type = getType();

        if (type != null) {
            return Type.valueOf(type.toUpperCase());
        } else {
            return null;
        }
    }

    public static IViewState getViewStateWithId(PageContext context, String id) {
        List<IViewState> viewStates = (List<IViewState>) context.findAttribute(LifeCycleConstants.VIEWSTATE_PARAM_NAME);

        if (viewStates != null) {
            for (IViewState state : viewStates) {
                if (id == null) {
                    return state;
                }

                if (id.equals(state.getId())) {
                    return state;
                }
            }
        }

        return null;
    }

}
