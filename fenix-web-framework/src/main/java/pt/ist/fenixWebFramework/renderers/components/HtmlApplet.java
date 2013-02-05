package pt.ist.fenixWebFramework.renderers.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlApplet extends HtmlComponent {

    private final Map<String, String> properties = new HashMap<String, String>();

    private String code;
    private String archive;
    private int width;
    private int height;

    public HtmlApplet() {
        super();
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = super.getOwnTag(context);

        tag.setName("applet");
        tag.setAttribute("code", getCode());
        tag.setAttribute("archive", getArchive());
        tag.setAttribute("width", getWidth() + "px");
        tag.setAttribute("height", getHeight() + "px");

        for (Entry<String, String> entry : properties.entrySet()) {
            HtmlTag paramTag = new HtmlTag("param");

            paramTag.setAttribute("name", entry.getKey());
            paramTag.setAttribute("value", entry.getValue());

            tag.addChild(paramTag);
        }

        return tag;
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public void removeProperty(String key) {
        properties.remove(key);
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
