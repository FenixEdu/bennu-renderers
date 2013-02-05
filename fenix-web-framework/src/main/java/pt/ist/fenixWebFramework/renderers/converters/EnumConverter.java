/**
 * 
 */
package pt.ist.fenixWebFramework.renderers.converters;

import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class EnumConverter extends Converter {

    private Class enumClass;

    public EnumConverter() {
        super();
    }

    public EnumConverter(Class enumClass) {
        super();

        this.enumClass = enumClass;
    }

    @Override
    public Object convert(Class type, Object value) {
        Object[] enums;

        if (this.enumClass == null) {
            enums = type.getEnumConstants();
        } else {
            enums = this.enumClass.getEnumConstants();
            if (enums == null) {
                enums = this.enumClass.getEnclosingClass().getEnumConstants();
            }
        }

        for (Object enum1 : enums) {
            if (enum1.toString().equals(value)) {
                return enum1;
            }
        }

        return null;
    }

}