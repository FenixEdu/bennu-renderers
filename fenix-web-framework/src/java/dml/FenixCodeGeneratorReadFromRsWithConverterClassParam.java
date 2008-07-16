package dml;


public class FenixCodeGeneratorReadFromRsWithConverterClassParam extends FenixCodeGeneratorReadFromRs {

    private static final String RESULT_SET_READER_CLASS = "pt.ist.fenixframework.pstm.ResultSetReader";
    private static final String GET_ENUM_METHOD = "pt.ist.fenixframework.pstm.Enum2SqlConversion.getEnum";

    static String convertersClass;
    static String convertersPackagePrefixWithDot;

    public FenixCodeGeneratorReadFromRsWithConverterClassParam(final CompilerArgs compilerArgs, final DomainModel domainModel) {
	super(compilerArgs, domainModel);
    }

    @Override
    protected String getRsReaderExpression(Slot slot) {
        String slotType = slot.getType();
        boolean isEnum = getDomainModel().isEnumType(slotType);
        String converter = slotTypeConverters.getProperty(slotType);

        if ((converter != null) && isEnum) {
            System.err.printf("[DML Compiler] WARNING: Enum type '%s' with a converter specified '%s' (using the converter)\n",
                              slotType,
                              converter);
            // ignore enum status
            isEnum = false;
        }

        String sqlType = (isEnum ? "VARCHAR" : slotTypeToSQLType.getProperty(slotType));
        if (sqlType == null) {
            throw new Error("FenixCodeGeneratorReadFromRs: couldn't find the SQL type corresponding to " + slotType);
        }

        StringBuilder expression = new StringBuilder();

        if (isEnum) {
            expression.append(GET_ENUM_METHOD);
            expression.append("(");
            expression.append(slotType);
            expression.append(".class, ");
        } else {
            if (converter != null) {
                expression.append(convertersClass);
                expression.append(".");
                // strip package prefix to make code more readable (or less unreadable...)
                converter = converter.replace(convertersPackagePrefixWithDot, "");
                // transform all remaining dots into $
                converter = converter.replace('.', '$');
                expression.append(converter);
                expression.append(".sqlToJava(");
            }
        }

        expression.append(RESULT_SET_READER_CLASS);
        expression.append(".getFrom");
        expression.append(sqlType);
        expression.append("(rs, \"");
        expression.append(convertToDBStyle(slot.getName()));
        expression.append("\")");

        if (isEnum || (converter != null)) {
            expression.append(")");
        }

        return expression.toString();
    }

    // MAJOR HACK!!!!!  The two following methods were copied verbatim from the net.sourceforge.fenixedu.util.StringFormatter class
    private static String splitCamelCaseString(String string) {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (char c : string.toCharArray()) {
            if (first) {
                first = false;
            } else if (Character.isUpperCase(c)) {
                result.append(' ');
            }
            result.append(c);
        }

        return result.toString();
    }

    private static String convertToDBStyle(String string) {
        return splitCamelCaseString(string).replace(' ', '_').toUpperCase();
    }

}
