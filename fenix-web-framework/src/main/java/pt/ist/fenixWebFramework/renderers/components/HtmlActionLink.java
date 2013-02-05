package pt.ist.fenixWebFramework.renderers.components;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

public class HtmlActionLink extends HtmlSimpleValueComponent {
    private String text;

    private HtmlComponent body;

    private boolean activated;

    private String hiddenFieldId;

    public HtmlActionLink() {
        super();
        this.hiddenFieldId = getValidIdOrName("____" + getName());

        setActivated(false);
    }

    public HtmlActionLink(HtmlComponent body) {
        super();
        this.hiddenFieldId = getValidIdOrName("____" + getName());
        setBody(body);
    }

    public HtmlActionLink(String text) {
        this(new HtmlText(text));
    }

    public HtmlComponent getBody() {
        return this.body;
    }

    public void setBody(HtmlComponent body) {
        this.body = body;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public void setValue(String value) {
        setActivated(value != null && value.equals(getName()));
    }

    public String getHiddenFieldId() {
        return hiddenFieldId;
    }

    @Override
    public HtmlTag getOwnTag(PageContext context) {

        HtmlHiddenField hidden = new HtmlHiddenField(getName(), null);
        hidden.setId(this.hiddenFieldId);

        HtmlLink link = new HtmlLink();

        link.setText(getText());
        link.setBody(getBody());
        link.setUrl("#");
        link.setModuleRelative(false);
        link.setContextRelative(false);

        String existingScript = getOnClick();
        if (existingScript == null) {
            existingScript = "";
        }

        setOnClick(existingScript + "document.getElementById('" + hidden.getId() + "').value='" + getName() + "'; "
                + "document.getElementById('" + hidden.getId() + "').form.submit();");

        HtmlTag tag = super.getOwnTag(context);

        tag.setName(null);

        tag.addChild(hidden.getOwnTag(context));

        HtmlTag linkTag = link.getOwnTag(context);
        linkTag.copyAttributes(tag);
        tag.addChild(linkTag);

        return tag;
    }
}
