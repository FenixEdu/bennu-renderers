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
public @interface Forwards {

    Forward[] value();

    /**
     * The tile definition that all the @Forward's inside this annotation will
     * extend from. Each @Forward may in turn override this definition to extend
     * from it's own tile. In case no super-tile is specified, the module's
     * default tile definition will be obtained in runtime and used.
     * 
     * Useful when the default tile is either incomplete or not exactly what
     * you're looking for.
     */
    String extend() default "";
}
