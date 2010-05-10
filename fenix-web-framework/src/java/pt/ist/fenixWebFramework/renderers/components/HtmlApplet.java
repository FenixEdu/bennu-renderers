package pt.ist.fenixWebFramework.renderers.components;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlApplet extends HtmlComponent {

    private final HtmlTag param = new HtmlTag("param");

    private String code;
    private String archive;
    private int width;
    private int height;

    public HtmlApplet() {
	super();
    }

    public void setURL(String value) {
	param.setAttribute("name", "url");
	param.setAttribute("value", value);
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
	HtmlTag tag = super.getOwnTag(context);

	tag.setName("applet");
	tag.setAttribute("code", getCode());
	tag.setAttribute("archive", getArchive());
	tag.setAttribute("width", getWidth() + "px");
	tag.setAttribute("height", getHeight() + "px");

	tag.addChild(param);

	return tag;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getArchive() {
	return archive;
    }

    public void setArchive(String archive) {
	this.archive = archive;
    }

    public int getWidth() {
	return width;
    }

    public void setWidth(int width) {
	this.width = width;
    }

    public int getHeight() {
	return height;
    }

    public void setHeight(int height) {
	this.height = height;
    }
}
