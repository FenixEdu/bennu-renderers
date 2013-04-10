package pt.ist.fenixWebFramework.struts.annotations;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;

import pt.ist.fenixWebFramework.annotation.FenixWebFrameworkAbstractProcessor;

import com.sun.tools.javac.code.Symbol.ClassSymbol;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ "pt.ist.fenixWebFramework.struts.annotations.Mapping" })
public class ActionAnnotationProcessor extends FenixWebFrameworkAbstractProcessor {

    static final String LOG_FILENAME = ".actionAnnotationLog";
    public static final String ENTRY_SEPARATOR = "\n";

    private final Set<String> actions = new HashSet<String>();

    @Override
    public String getLogFilename() {
	return LOG_FILENAME;
    }

    @Override
    public Class<? extends Annotation> getAnnotationClass() {
	return Mapping.class;
    }

    @Override
    public void writeLogFile(Writer writer) throws IOException {
	for (final String action : actions) {
	    writer.append(action);
	    writer.write(ENTRY_SEPARATOR);
	}
    }

    @Override
    public void processElements(Set<? extends Element> elements) {
	for (Element element : elements) {
	    actions.add(((ClassSymbol) element).getQualifiedName().toString());
	}
    }

}
