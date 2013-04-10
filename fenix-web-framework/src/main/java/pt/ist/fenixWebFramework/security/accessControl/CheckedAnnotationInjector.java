package pt.ist.fenixWebFramework.security.accessControl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import org.apache.commons.io.FileUtils;

public class CheckedAnnotationInjector {
    private static final String CALL_PREFIX = "net.sourceforge.fenixedu.injectionCode.AccessControl.check(this, net.sourceforge.fenixedu.predicates.";
    private static final String CALL_STATIC_PREFIX = "net.sourceforge.fenixedu.injectionCode.AccessControl.check(net.sourceforge.fenixedu.predicates.";
    private static final String CALL_SUFFIX = ");";

    public static void inject(File outputFolder, ClassLoader loader) {
	ClassPool classPool = ClassPool.getDefault();
	classPool.appendClassPath(new LoaderClassPath(loader));

	File file = null;
	try {
	    final URL resource = CheckedAnnotationInjector.class.getClassLoader().getResource(
		    CheckedAnnotationProcessor.LOG_FILENAME);
	    if (resource == null) {
		System.out.printf("File not found :%s. Skipping checked annotation injection.\n",
			CheckedAnnotationProcessor.LOG_FILENAME);
		return;
	    }
	    final URI uri = resource.toURI();
	    file = new File(uri);
	    if (file.exists()) {
		Map linesByClass = new HashMap();

		String fileContents = FileUtils.readFileToString(file);
		String[] lines = fileContents.split("\n");
		for (String line : lines) {
		    String[] strings = line.split("\t");
		    if (!linesByClass.containsKey(strings[0])) {
			linesByClass.put(strings[0], new HashSet());
		    }
		    Set methods = (Set) linesByClass.get(strings[0]);
		    methods.add(strings);
		}

		process(CALL_PREFIX, CALL_STATIC_PREFIX, CALL_SUFFIX, outputFolder.getAbsolutePath(), classPool, linesByClass);
	    } else {
		throw new Error("[CheckedAnnotationInjector] : couldn't inject @Service, file not found : "
			+ CheckedAnnotationProcessor.LOG_FILENAME);
	    }
	} catch (FileNotFoundException e) {
	    throw new Error(e);
	} catch (IOException e) {
	    throw new Error(e);
	} catch (URISyntaxException e) {
	    throw new Error(e);
	} finally {
	    if (file != null && file.exists()) {
		file.delete();
	    }
	}
    }

    public static void main(String[] args) {
	String methodCallPrefix = args[0];
	String methodCallStaticPrefix = args[1];
	String methodCallSuffix = args[2];

	ClassPool classPool = ClassPool.getDefault();
	classPool.appendSystemPath();
	try {
	    for (int i = 3; i < args.length; i++) {
		String arg = args[i];
		classPool.appendClassPath(arg);
	    }
	} catch (NotFoundException e) {
	    throw new Error(e);
	}

	File file = null;
	try {
	    file = new File(".checkedAnnotationLog");
	    if (file.exists()) {
		Map linesByClass = new HashMap();

		String fileContents = FileUtils.readFileToString(file);
		String[] lines = fileContents.split("\n");
		for (String line : lines) {
		    String[] strings = line.split("\t");
		    if (!linesByClass.containsKey(strings[0])) {
			linesByClass.put(strings[0], new HashSet());
		    }
		    Set methods = (Set) linesByClass.get(strings[0]);
		    methods.add(strings);
		}

		process(methodCallPrefix, methodCallStaticPrefix, methodCallSuffix, args[3], classPool, linesByClass);
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

    private static void process(String methodCallPrefix, String methodCallStaticPrefix, String methodCallSuffix,
	    String outputFolder, ClassPool classPool, Map<String, Set<String[]>> linesByClass) {
	for (Map.Entry entry : linesByClass.entrySet()) {
	    process(methodCallPrefix, methodCallStaticPrefix, methodCallSuffix, outputFolder, classPool, (String) entry.getKey(),
		    (Set) entry.getValue());
	}
    }

    private static void process(String methodCallPrefix, String methodCallStaticPrefix, String methodCallSuffix,
	    String outputFolder, ClassPool classPool, String className, Set<String[]> value) {
	try {
	    CtClass classToInject = classPool.get(className);
	    for (String[] strings : value) {
		process(methodCallPrefix, methodCallStaticPrefix, methodCallSuffix, outputFolder, classPool, classToInject,
			strings);
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

    private static void process(String methodCallPrefix, String methodCallStaticPrefix, String methodCallSuffix,
	    String outputFolder, ClassPool classPool, CtClass classToInject, String[] strings) throws CannotCompileException {
	String methodName = strings[1];
	String annParamValue = strings[2];
	// System.out.printf("[CheckedAnnotationInjector] inject %s\n", new
	// Object[] { classToInject.getName() });
	boolean found = false;
	for (CtMethod ctMethod : classToInject.getDeclaredMethods()) {
	    if (ctMethod.getName().equals(methodName)) {
		inject(methodCallPrefix, methodCallStaticPrefix, methodCallSuffix, ctMethod, annParamValue);
		found = true;
	    }
	}
	if (!found) {
	    System.out.printf("[CheckedAnnotationInjector] can't inject method %s in %s. Method not found.\n", new Object[] {
		    methodName, classToInject.getName() });
	}
    }

    private static void inject(String methodCallPrefix, String methodCallStaticPrefix, String methodCallSuffix,
	    CtMethod ctMethod, String annParamValue) throws CannotCompileException {
	ctMethod.insertBefore(getCodeToInject(Modifier.isStatic(ctMethod.getModifiers()) ? methodCallStaticPrefix
		: methodCallPrefix, methodCallSuffix, annParamValue));
    }

    private static String getCodeToInject(String methodCallPrefix, String methodCallSuffix, String annParamValue) {
	StringBuilder stringBuilder = new StringBuilder();

	stringBuilder.append("{");
	stringBuilder.append(methodCallPrefix);
	stringBuilder.append(annParamValue.trim());
	stringBuilder.append(methodCallSuffix);
	stringBuilder.append("}");

	return stringBuilder.toString();
    }
}