package pt.ist.fenixWebFramework.servlets.rest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import pt.utl.ist.fenix.tools.util.FileUtils;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({ "pt.ist.fenixWebFramework.servlets.rest.Path" })
public class RestAnnotationProcessor extends AbstractProcessor {

    static final String LOG_FILENAME = ".restAnnotationLog";
    public static final String ENTRY_SEPERATOR = "\n";
    public static final String LINE_SEPERATOR = "\t";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        final Map<String, String> resources = new HashMap<String, String>();

        final File file = new File(LOG_FILENAME);
        if (file.exists()) {
            try {
                final String contents = FileUtils.readFile(LOG_FILENAME);
                for (final String line : contents.split(ENTRY_SEPERATOR)) {
                    String[] values = line.split(LINE_SEPERATOR);
                    if (values.length == 2) {
                        resources.put(values[0], values[1]);
                    }
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Path.class);
        for (final Element element : elementsAnnotatedWith) {
            if (element instanceof TypeElement) {
                resources.put(((TypeElement) element).getQualifiedName().toString(), element.getAnnotation(Path.class).value());
            }
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(LOG_FILENAME, true);
            for (final Entry<String, String> entry : resources.entrySet()) {
                fileWriter.append(entry.getKey());
                fileWriter.append(LINE_SEPERATOR);
                fileWriter.append(entry.getValue());
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
