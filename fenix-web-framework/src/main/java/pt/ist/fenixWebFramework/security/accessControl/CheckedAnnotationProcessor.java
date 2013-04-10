package pt.ist.fenixWebFramework.security.accessControl;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import pt.ist.fenixWebFramework.annotation.FenixWebFrameworkAbstractProcessor;

import com.sun.tools.javac.code.Attribute.Constant;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ "pt.ist.fenixWebFramework.security.accessControl.Checked" })
public class CheckedAnnotationProcessor extends FenixWebFrameworkAbstractProcessor {

    protected static final String LOG_FILENAME = ".checkedAnnotationLog";
    protected static final String FIELD_SEPARATOR = "\t";
    protected static final String ENTRY_SEPARATOR = "\n";

    private static Set<String> ENTRY_SET = new HashSet<String>();

    private String getAnnotationConstantValue(final Element element) {
	for (final AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
	    if (annotationMirror.getAnnotationType().asElement().toString().equals(Checked.class.getName())) {
		for (final Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror
			.getElementValues().entrySet()) {
		    final Constant constant = (Constant) entry.getValue();
		    return (String) constant.getValue();
		}
	    }
	}
	return null;
    }

    @Override
    public Class<? extends Annotation> getAnnotationClass() {
	return Checked.class;
    }

    @Override
    public void processElements(Set<? extends Element> elements) {
	for (final Element element : elements) {
	    if (element instanceof MethodSymbol) {
		final MethodSymbol methodSymbol = (MethodSymbol) element;
		final ClassSymbol classSymbol = (ClassSymbol) methodSymbol.getEnclosingElement();
		final String className = classSymbol.getQualifiedName().toString();
		final String annotationConstantValue = getAnnotationConstantValue(element);

		String entry = getEntry(className, methodSymbol.getSimpleName().toString(), annotationConstantValue);
		ENTRY_SET.add(entry);
	    } else {
		System.out.println("Element: " + element);
	    }
	}
    }

    public String getEntry(String className, String method, String annotationConstantValue) {
	StringBuilder entryBuilder = new StringBuilder();
	entryBuilder.append(className);
	entryBuilder.append(FIELD_SEPARATOR);
	entryBuilder.append(method);
	entryBuilder.append(FIELD_SEPARATOR);
	entryBuilder.append(annotationConstantValue);
	entryBuilder.append(ENTRY_SEPARATOR);
	return entryBuilder.toString();
    }

    @Override
    public String getLogFilename() {
	return LOG_FILENAME;
    }

    @Override
    public void writeLogFile(Writer writer) throws IOException {
	for (String entry : ENTRY_SET) {
	    writer.append(entry);
	}
    }

}