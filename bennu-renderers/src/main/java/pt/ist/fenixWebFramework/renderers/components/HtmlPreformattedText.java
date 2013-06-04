package pt.ist.fenixWebFramework.renderers.components;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlPreformattedText extends HtmlBlockContainer {

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("pre");

        return tag;
    }

}
