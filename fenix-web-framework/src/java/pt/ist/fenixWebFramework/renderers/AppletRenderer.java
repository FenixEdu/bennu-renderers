package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlApplet;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * The default output renderer for the signature applet
 * 
 * @author diogo
 */
public class AppletRenderer extends OutputRenderer {

    private String bundle;

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

		HtmlApplet applet = new HtmlApplet();

		applet.setObjectId(objectId);

		return applet;
	    }
	};
    }

}
