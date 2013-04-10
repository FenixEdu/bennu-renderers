package pt.ist.fenixWebFramework.services;

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

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({ "pt.ist.fenixWebFramework.services.Service" })
public class ServiceAnnotationProcessor extends FenixWebFrameworkAbstractProcessor {

    protected static final String LOG_FILENAME = ".serviceAnnotationLog";
    protected static final String FIELD_SEPARATOR = " ";
    protected static final String ENTRY_SEPARATOR = "\n";

    private static final Set<String> ENTRY_SET = new HashSet<String>();

    @Override
    public void processElements(Set<? extends Element> elements) {
	for (final Element element : elements) {
	    final ClassSymbol classSymbol = (ClassSymbol) element.getEnclosingElement();
	    String className = processClassName(classSymbol);
	    String entry = getEntry(className, element.getSimpleName().toString());
	    ENTRY_SET.add(entry);
	}
    }

    private String getEntry(String className, String methodName) {
	StringBuilder entryBuilder = new StringBuilder();
	entryBuilder.append(className);
	entryBuilder.append(FIELD_SEPARATOR);
	entryBuilder.append(methodName);
	entryBuilder.append(ENTRY_SEPARATOR);
	return entryBuilder.toString();
    }

    private String processClassName(ClassSymbol classSymbol) {
	Symbol symbol = classSymbol.getEnclosingElement();
	if (symbol instanceof ClassSymbol) {
	    return processClassName((ClassSymbol) symbol) + "$" + classSymbol.getSimpleName();
	}
	return classSymbol.getQualifiedName().toString();
    }

    @Override
    public String getLogFilename() {
	return LOG_FILENAME;
    }

    @Override
    public Class<? extends Annotation> getAnnotationClass() {
	return Service.class;
    }

    @Override
    public void writeLogFile(Writer writer) throws IOException {
	for (String entry : ENTRY_SET) {
	    writer.append(entry);
	}
    }

}
