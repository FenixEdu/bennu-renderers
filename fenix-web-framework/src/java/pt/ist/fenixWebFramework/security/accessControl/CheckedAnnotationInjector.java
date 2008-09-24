package pt.ist.fenixWebFramework.security.accessControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.utl.ist.fenix.tools.util.FileUtils;

public class CheckedAnnotationInjector {

    public static void main(String[] args) {
	final String methodCallPrefix = args[0];
	final String methodCallSuffix = args[1];

	final ClassPool classPool = ClassPool.getDefault();
	classPool.appendSystemPath();
	try {
	    for (int i = 2; i < args.length; i++) {
		final String arg = args[i];
		classPool.appendClassPath(arg);
	    }
	} catch (NotFoundException e) {
	    throw new Error(e);
	}

//	classPool.importPackage("pt.ist.fenixWebFramework.security");
//	classPool.importPackage("pt.ist.fenixframework.pstm");
//	classPool.importPackage("pt.ist.fenixWebFramework._development");

	File file = null;
	try {
	    file = new File(CheckedAnnotationProcessor.LOG_FILENAME);
	    if (file.exists()) {
		final Map<String, Set<String[]>> linesByClass = new HashMap<String, Set<String[]>>();

		final String fileContents = FileUtils.readFile(new FileInputStream(file));
		final String[] lines = fileContents.split(CheckedAnnotationProcessor.ENTRY_SEPERATOR);
		for (final String line : lines) {
		    final String[] strings = line.split(CheckedAnnotationProcessor.FIELD_SEPERATOR);
		    if (!linesByClass.containsKey(strings[0])) {
			linesByClass.put(strings[0], new HashSet<String[]>());
		    }
		    final Set<String[]> methods = linesByClass.get(strings[0]);
		    methods.add(strings);
		}

		
		process(methodCallPrefix, methodCallSuffix, args[2], classPool, linesByClass);
	    }
	} catch (FileNotFoundException e) {
	    throw new Error(e);
	} catch (IOException e) {
	    throw new Error(e);
	} finally {
	    if (file != null && file.exists()) {
		file.delete();
	    }
	}
    }

    private static void process(final String methodCallPrefix, final String methodCallSuffix, final String outputFolder, final ClassPool classPool, final Map<String, Set<String[]>> linesByClass) {
	for (final Entry<String, Set<String[]>> entry : linesByClass.entrySet()) {
	    process(methodCallPrefix, methodCallSuffix, outputFolder, classPool, entry.getKey(), entry.getValue());
	}
    }

    private static void process(final String methodCallPrefix, final String methodCallSuffix, final String outputFolder, final ClassPool classPool, final String className, final Set<String[]> value) {
	try {
	    final CtClass classToInject = classPool.get(className);
	    for (final String[] strings : value) {
		process(methodCallPrefix, methodCallSuffix, outputFolder, classPool, classToInject, strings);
	    }
	    classToInject.writeFile(outputFolder);
	    classToInject.detach();
	} catch (NotFoundException e) {
	    throw new Error(e);
	} catch (CannotCompileException e) {
	    throw new Error(e);
	} catch (IOException e) {
	    throw new Error(e);
	}
    }

    private static void process(final String methodCallPrefix, final String methodCallSuffix, final String outputFolder, final ClassPool classPool, final CtClass classToInject, final String[] strings) throws CannotCompileException {
	final String methodName = strings[1];
	final String annParamValue = strings[2];
	for (final CtMethod ctMethod : classToInject.getDeclaredMethods()) {
	    if (ctMethod.getName().equals(methodName)) {
		inject(methodCallPrefix, methodCallSuffix, classToInject, ctMethod, annParamValue);
	    }
	}
    }

    private static void inject(final String methodCallPrefix, final String methodCallSuffix, final CtClass classToInject, final CtMethod ctMethod, final String annParamValue) throws CannotCompileException {
	final String codeToInject = getCodeToInject(methodCallPrefix, methodCallSuffix, annParamValue);
	ctMethod.insertBefore(codeToInject);
    }

    private static String getCodeToInject(final String methodCallPrefix, final String methodCallSuffix, final String annParamValue) {
	final StringBuilder stringBuilder = new StringBuilder();

	stringBuilder.append("{");
	stringBuilder.append(methodCallPrefix);
	stringBuilder.append(annParamValue.trim());
	stringBuilder.append(methodCallSuffix);
	stringBuilder.append("}");

	return stringBuilder.toString();
    }

}
