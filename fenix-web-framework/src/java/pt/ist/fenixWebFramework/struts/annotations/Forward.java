/**
 * 
 */
package pt.ist.fenixWebFramework.struts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Forward {

    String name();

    String path();

    /**
     * The tile definition that this forward will directly extend from. In case
     * no super-tile is specified, the module's default tile definition will be
     * obtained in runtime and used.
     * 
     * Useful when the default tile is either incomplete or not exactly what
     * you're looking for.
     */
    String extend() default "";

    boolean redirect() default false;

    boolean contextRelative() default false;

    boolean useTile() default true;

}
