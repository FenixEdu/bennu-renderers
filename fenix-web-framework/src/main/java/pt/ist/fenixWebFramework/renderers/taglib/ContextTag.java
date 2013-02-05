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
                HtmlHiddenField hidden = new HtmlHiddenField(LifeCycleConstants.VIEWSTATE_LIST_PARAM_NAME, encodeViewStates());

                hidden.draw(this.pageContext);
            }
        } catch (IOException e) {
            throw new JspException(e);
        }

        release();
        return EVAL_PAGE;
    }

    private String encodeViewStates() throws IOException {
        return ViewState.encodeListToBase64(this.viewStates);
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
