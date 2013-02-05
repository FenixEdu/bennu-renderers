package pt.ist.fenixWebFramework.servlets.functionalities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
//cannot use ActiveObject.class.getName() the value must be a constant expression BLEAH!
@SupportedAnnotationTypes({ "pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeAction" })
public class CreateNodeActionAnnotationProcessor extends AbstractProcessor {

    public static final String LOG_FILENAME = ".createNodeActionAnnotationLog";
    public static final String FIELD_SEPERATOR = " ";
    public static final String ENTRY_SEPERATOR = "\n";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(LOG_FILENAME, true);

            final Set<MethodSymbol> annotatedElements =
                    (Set<MethodSymbol>) roundEnv.getElementsAnnotatedWith(CreateNodeAction.class);

            for (final MethodSymbol methodElement : annotatedElements) {
                final ClassSymbol classSymbol = (ClassSymbol) methodElement.getEnclosingElement();
                final String className = classSymbol.getQualifiedName().toString();

                final CreateNodeAction createNodeAction = methodElement.getAnnotation(CreateNodeAction.class);
                final String bundle = createNodeAction.bundle();
                final String key = createNodeAction.key();
                final String groupKey = createNodeAction.groupKey();

                final Mapping mapping = classSymbol.getAnnotation(Mapping.class);
                final String module = mapping.module();
                final String path = mapping.path();

                fileWriter.write(bundle);
                fileWriter.write(FIELD_SEPERATOR);
                fileWriter.write(groupKey);
                fileWriter.write(FIELD_SEPERATOR);
                fileWriter.write(key);
                fileWriter.write(FIELD_SEPERATOR);
                if (module != null && module.length() > 0) {
                    fileWriter.write("/");
                    fileWriter.write(module);
                }
                fileWriter.write(path);
                fileWriter.write(".do?method=");
                fileWriter.write(methodElement.getSimpleName().toString());
                fileWriter.write(ENTRY_SEPERATOR);
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
