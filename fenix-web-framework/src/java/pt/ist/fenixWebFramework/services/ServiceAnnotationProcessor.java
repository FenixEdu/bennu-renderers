package pt.ist.fenixWebFramework.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
//cannot use ActiveObject.class.getName() the value must be a constant expression BLEAH!
@SupportedAnnotationTypes( { "pt.ist.fenixWebFramework.services.Service" })
public class ServiceAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

	FileWriter fileWriter = null;
	try {
	    fileWriter = new FileWriter(".serviceAnnotationLog", true);

	    final Set<MethodSymbol> annotatedElements = (Set<MethodSymbol>) roundEnv.getElementsAnnotatedWith(Service.class);

	    for (final MethodSymbol methodElement : annotatedElements) {
		final ClassSymbol classSymbol = (ClassSymbol) methodElement.getEnclosingElement();
		final String className = classSymbol.getQualifiedName().toString();

		fileWriter.write(className);
		fileWriter.write(" ");
		fileWriter.write(methodElement.getSimpleName().toString());
		for (final VarSymbol varSymbol : methodElement.getParameters()) {
		    fileWriter.write(" ");
		    fileWriter.write(varSymbol.getSimpleName().toString());
		}
		fileWriter.write('\n');
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

}
