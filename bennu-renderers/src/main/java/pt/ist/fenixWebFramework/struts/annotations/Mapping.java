/**
 * 
 */
package pt.ist.fenixWebFramework.struts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.struts.action.ActionForm;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {

    String path();

    String formBean() default "";

    String attribute() default "";

    Class<? extends ActionForm> formBeanClass() default ActionForm.class;

    String input() default "";

    String module() default "";

    String scope() default "request";

    boolean validate() default true;

    String parameter() default "method";

    Class<?> functionality() default Object.class;
}
