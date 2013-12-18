package org.fenixedu.bennu.core.presentationTier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.struts.action.ActionForward;

public class DefaultContext extends Context {
    private String layout = "/renderers/layout.jsp";

    private String body;
    private List<String> head;
    private List<String> scripts;

    {
        head = new ArrayList<String>();
        scripts = new ArrayList<String>();
    }

    public DefaultContext() {
        super();
    }

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

    public void addHead(String head) {
        this.head.add(head);
    }

    public void addScript(String script) {
        this.scripts.add(script);
    }

    public Collection<String> getHead() {
        return head;
    }

    public Collection<String> getScripts() {
        return scripts;
    }
}