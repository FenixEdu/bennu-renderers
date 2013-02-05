package pt.ist.fenixWebFramework.renderers.components;

public class HtmlSubmitButton extends HtmlInputButton {

    public HtmlSubmitButton() {
        super("submit");
    }

    public HtmlSubmitButton(String text) {
        this();

        setText(text);
    }
}
