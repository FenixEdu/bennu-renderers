package pt.ist.fenixWebFramework.struts.annotations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
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

        final SortedSet<String> lines = new TreeSet<String>();

        final File file = new File(LOG_FILENAME);
        if (file.exists()) {
            try {
                final String contents = FileUtils.readFile(LOG_FILENAME);
                for (final String line : contents.split(ENTRY_SEPERATOR)) {
                    lines.add(line);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        for (final Element iter : roundEnv.getElementsAnnotatedWith(Mapping.class)) {
            if (ClassSymbol.class.isAssignableFrom(iter.getClass())) {
                final ClassSymbol classSymbol = (ClassSymbol) iter;
                lines.add(classSymbol.getQualifiedName().toString());
            } else {
                System.out.println("Ignoring " + iter.getSimpleName());
            }
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(LOG_FILENAME);
            for (final String line : lines) {
                fileWriter.append(line);
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
