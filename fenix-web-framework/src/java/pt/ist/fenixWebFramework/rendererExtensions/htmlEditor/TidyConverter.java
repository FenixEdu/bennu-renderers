package pt.ist.fenixWebFramework.rendererExtensions.htmlEditor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Properties;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;
import org.w3c.tidy.TidyMessageListener;

import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public abstract class TidyConverter extends Converter {

    private static final String TIDY_PROPERTIES = "HtmlEditor-Tidy.properties";

    private static final String ENCODING = "ISO-8859-1";

    public String getTidyProperties() {
	return TIDY_PROPERTIES;
    }

    @Override
    public Object convert(Class type, Object value) {
	String htmlText = (String) value;

	if (htmlText == null || htmlText.length() == 0) {
	    return null;
	}

	ByteArrayInputStream inStream = new ByteArrayInputStream(htmlText.getBytes());
	ByteArrayOutputStream outStream = new ByteArrayOutputStream();

	Tidy tidy = createTidyParser();

	TidyErrorsListener errorListener = new TidyErrorsListener();
	tidy.setMessageListener(errorListener);
	Document document = tidy.parseDOM(inStream, null);

	if (errorListener.isBogus()) {
	    throw new ConversionException("renderers.converter.safe.invalid");
	}

	parseDocument(outStream, tidy, document);
	final String documentAsString = getDocumentAsString(document);
	try {
	    return filterOutput(new String(documentAsString.getBytes(), ENCODING));
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	    throw new ConversionException("tidy.converter.ending.notSupported.critical");
	}
	// return filterOutput(documentAsString);
    }

    public String getDocumentAsString(Document doc) {
	OutputFormat format = new OutputFormat(doc);
	format.setIndenting(false);
	format.setOmitXMLDeclaration(true);
	format.setVersion("1.0");
	format.setEncoding(ENCODING);
	Writer out = new StringWriter();
	XMLSerializer serializer = new XMLSerializer(out, format);
	try {
	    serializer.serialize(doc);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new ConversionException("tidy.converter.ending.notSupported.critical");
	}
	return out.toString();
    }

    protected String filterOutput(String output) {
	return output;
    }

    private Tidy createTidyParser() {
	Tidy tidy = new Tidy();

	Properties properties = new Properties();
	try {
	    properties.load(getClass().getResourceAsStream(getTidyProperties()));
	} catch (IOException e) {
	    e.printStackTrace();
	}

	tidy.setConfigurationFromProps(properties);

	return tidy;
    }

    protected abstract void parseDocument(OutputStream outStream, Tidy tidy, Document document);

    class TidyErrorsListener implements TidyMessageListener {

	boolean bogus;

	public boolean isBogus() {
	    return this.bogus;
	}

	public void setBogus(boolean bogus) {
	    this.bogus = bogus;
	}

	@Override
	public void messageReceived(TidyMessage message) {
	    if (message.getLevel().equals(TidyMessage.Level.ERROR)) {
		setBogus(true);
	    }
	}

    }
}
