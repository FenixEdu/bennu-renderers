package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlApplet;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

/**
 * The default output renderer for the signature applet
 * 
 * @author diogo
 */
public class AppletRenderer extends OutputRenderer {

    private String bundle;
    private String link;

    private String code;
    private String archive;
    private int width;
    private int height;

    public String getBundle() {
	return this.bundle;
    }

    /**
     * Chooses the bundle in which the labels will be searched.
     * 
     * @property
     */
    public void setBundle(String bundle) {
	this.bundle = bundle;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
	return new Layout() {

	    @Override
	    public HtmlComponent createComponent(Object object, Class type) {
		String objectId = (String) object;

		String url = getLink();
		String fullUrl = url + "&" + GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME + "="
			+ GenericChecksumRewriter.calculateChecksum(url);

		HtmlApplet applet = new HtmlApplet();

		applet.setURL(fullUrl);
		applet.setCode(getCode());
		applet.setArchive(getArchive());
		applet.setWidth(getWidth());
		applet.setHeight(getHeight());

		return applet;
	    }
	};
    }

    public void setLink(String link) {
	this.link = link;
    }

    public String getLink() {
	return link;
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
