package pt.ist.bennu.core.presentationTier;

import org.apache.struts.action.ActionForward;

public class DefaultContext extends Context {
    private String layout = "/renderers/layout.jsp";

    private String body;
    private String head;

    public DefaultContext(String path) {
        super(path);
    }

    @Override
    public ActionForward forward(String body) {
        this.body = body;
        return new ActionForward(layout);
    }

    public String getBody() {
        return body;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

}