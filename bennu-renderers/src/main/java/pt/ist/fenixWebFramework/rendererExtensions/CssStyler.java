package pt.ist.fenixWebFramework.rendererExtensions;

public class CssStyler {
    private String useIf;
    private String useIfNot;
    private String styleClass;

    public String getUseIf() {
        return useIf;
    }

    public void setUseIf(String useIf) {
        this.useIf = useIf;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getUseIfNot() {
        return useIfNot;
    }

    public void setUseIfNot(String useIfNot) {
        this.useIfNot = useIfNot;
    }

}
