package pt.ist.fenixWebFramework.annotation;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public abstract class FenixWebFrameworkAbstractProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
	Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(getAnnotationClass());
	processElements(elements);
	if (roundEnv.processingOver()) {
	    Writer logFileWriter = null;
	    try {
		FileObject logFile = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", getLogFilename());
		logFileWriter = logFile.openWriter();
		writeLogFile(logFileWriter);
	    } catch (Exception e) {
		throw new Error(e);
	    } finally {
		if (logFileWriter != null) {
		    try {
			logFileWriter.flush();
			logFileWriter.close();
		    } catch (IOException ioe) {
			throw new Error(ioe);
		    }
		}
	    }
	}
	return true;
    }

    public abstract String getLogFilename();

    public abstract Class<? extends Annotation> getAnnotationClass();

    public abstract void processElements(Set<? extends Element> elements);

    public abstract void writeLogFile(Writer writer) throws IOException;

}
