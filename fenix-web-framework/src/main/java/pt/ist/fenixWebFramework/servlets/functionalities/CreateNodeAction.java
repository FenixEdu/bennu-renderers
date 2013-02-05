package pt.ist.fenixWebFramework.servlets.functionalities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.SOURCE)
public @interface CreateNodeAction {

    String bundle();

    String key();

    String groupKey();

}
