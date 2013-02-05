package pt.ist.fenixWebFramework.security.accessControl;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ METHOD, CONSTRUCTOR })
public @interface Checked {
    String value();
}
