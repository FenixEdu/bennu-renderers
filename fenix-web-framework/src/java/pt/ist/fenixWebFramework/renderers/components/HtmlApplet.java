package pt.ist.fenixWebFramework.renderers.components;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlApplet extends HtmlComponent {

    private final HtmlTag param = new HtmlTag("param");

    public HtmlApplet() {
	super();
    }

    public void setObjectId(String value) {
	param.setAttribute("name", "objectId");
	param.setAttribute("value", value);
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
	HtmlTag tag = super.getOwnTag(context);

	tag.setName("applet");
	tag.setAttribute("code", "AssinaturaElectronicaQualificada.class");
	tag.setAttribute("archive", "http://pabon.ist.utl.pt/aeq/aeq.jar");
	tag.setAttribute("width", "800px");
	tag.setAttribute("height", "800px");

	tag.addChild(param);

	return tag;
    }
}
