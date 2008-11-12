package dml;

import java.util.Arrays;

public class ConverterClassAwareDmlCompiler {

    public static void main(String[] args) throws Exception {
	final int c = args.length;
	final String[] baseArgs = Arrays.copyOfRange(args, 0, c - 2);
	FenixCodeGeneratorReadFromRsWithConverterClassParam.convertersClass = args[c - 2];
	FenixCodeGeneratorReadFromRsWithConverterClassParam.convertersPackagePrefixWithDot = args[c - 1];
        DmlCompiler.main(baseArgs);
    }

}
