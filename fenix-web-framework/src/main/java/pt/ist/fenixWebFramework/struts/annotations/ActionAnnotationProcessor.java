package pt.ist.fenixWebFramework.struts.annotations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import pt.utl.ist.fenix.tools.util.FileUtils;

import com.sun.tools.javac.code.Symbol.ClassSymbol;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({ "pt.ist.fenixWebFramework.struts.annotations.Mapping" })
public class ActionAnnotationProcessor extends AbstractProcessor {

    static final String LOG_FILENAME = ".actionAnnotationLog";
    public static final String ENTRY_SEPERATOR = "\n";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        final Set<String> actions = new HashSet<String>();

        final File file = new File(LOG_FILENAME);
        if (file.exists()) {
            try {
                final String contents = FileUtils.readFile(LOG_FILENAME);
                for (final String line : contents.split(ENTRY_SEPERATOR)) {
                    actions.add(line);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        final Set<ClassSymbol> elements = (Set<ClassSymbol>) roundEnv.getElementsAnnotatedWith(Mapping.class);

        for (final ClassSymbol classSymbol : elements) {
            actions.add(classSymbol.getQualifiedName().toString());
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(LOG_FILENAME, true);
            for (final String action : actions) {
                fileWriter.append(action);
                fileWriter.write(ENTRY_SEPERATOR);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

}
