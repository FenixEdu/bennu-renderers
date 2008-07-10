package pt.ist.fenixWebFramework.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import pt.utl.ist.fenix.tools.util.FileUtils;

public class ServiceAnnotationInjector {

    public static void main(String[] args) {
	ClassPool classPool = ClassPool.getDefault();

	classPool.appendSystemPath();
	try {
	    classPool.appendClassPath(args[0]);
	} catch (NotFoundException e) {
	    throw new Error(e);
	}

	File file = null;
	try {
	    file = new File(".serviceAnnotationLog");
	    if (file.exists()) {
		final String fileContents = FileUtils.readFile(new FileInputStream(file));
		final String[] lines = fileContents.split("\n");
		for (final String line : lines) {
		    final String[] strings = line.split(" ");
		    process(args[0], classPool, strings[0], strings[1], strings);
		}
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

    private static void process(final String outputFolder, final ClassPool classPool, final String className, final String methodName, String[] args) {
	try {
	    CtClass classToInject = classPool.get(className);
	    for (final CtMethod ctMethod : classToInject.getDeclaredMethods()) {
		if (ctMethod.getName().equals(methodName)) {
		    ctMethod.setName("_" + methodName + "_");

		    final CtMethod newCtMethod = new CtMethod(ctMethod.getReturnType(), methodName, ctMethod.getParameterTypes(), ctMethod.getDeclaringClass());
		    newCtMethod.setModifiers(ctMethod.getModifiers());
		    newCtMethod.setBody(getWrapperMethod(ctMethod, args));
		    classToInject.addMethod(newCtMethod);
		}
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

    private static String getWrapperMethod(final CtMethod ctMethod, final String[] args) throws NotFoundException {
	final CtClass returnType = ctMethod.getReturnType();
	final boolean isVoid = returnType.getSimpleName().equals("void");

	final StringBuilder stringBuilder = new StringBuilder();
	stringBuilder.append("{\n");

	// TODO : insert some code here...
	stringBuilder.append("System.out.println(\"Yupi B \");\n");

	if (!isVoid) {
	    stringBuilder.append(returnType.getSimpleName());
	    stringBuilder.append(" _result_ = ");
	}
	stringBuilder.append(ctMethod.getName());
	stringBuilder.append("($$);");

	// TODO : insert some more code here.
	stringBuilder.append("System.out.println(\"Yupi A\");\n");

	if (!isVoid) {
	    stringBuilder.append("return _result_;\n");
	}
	stringBuilder.append("}");
	return stringBuilder.toString();
    }

}
