package pt.ist.fenixWebFramework.security.accessControl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.tools.javac.code.Attribute.Constant;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
//cannot use ActiveObject.class.getName() the value must be a constant expression BLEAH!
@SupportedAnnotationTypes({ "pt.ist.fenixWebFramework.security.accessControl.Checked" })
public class CheckedAnnotationProcessor extends AbstractProcessor {

    static final String LOG_FILENAME = ".checkedAnnotationLog";
    static final String FIELD_SEPERATOR = "\t";
    static final String ENTRY_SEPERATOR = "\n";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(LOG_FILENAME, true);

            final Set<Element> annotatedElements = (Set<Element>) roundEnv.getElementsAnnotatedWith(Checked.class);

            for (final Element element : annotatedElements) {
                if (element instanceof MethodSymbol) {
                    final MethodSymbol methodSymbol = (MethodSymbol) element;
                    final ClassSymbol classSymbol = (ClassSymbol) methodSymbol.getEnclosingElement();
                    final String className = classSymbol.getQualifiedName().toString();
                    final String annotationConstantValue = getAnnotationConstantValue(element);

                    fileWriter.write(className);
                    fileWriter.write(FIELD_SEPERATOR);
                    fileWriter.write(methodSymbol.getSimpleName().toString());
                    fileWriter.write(FIELD_SEPERATOR);
                    fileWriter.write(annotationConstantValue);
                    fileWriter.write(ENTRY_SEPERATOR);
                } else {
                    System.out.println("Element: " + element);
                }
            }
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new Error(e);
                }
            }
        }

        return true;
    }

    private String getAnnotationConstantValue(final Element element) {
        for (final AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().asElement().toString().equals(Checked.class.getName())) {
                for (final Entry entry : annotationMirror.getElementValues().entrySet()) {
                    //final MethodSymbol = entry.getKey();
                    final Constant constant = (Constant) entry.getValue();
                    return (String) constant.getValue();
                }
            }
        }
        return null;
    }

}
