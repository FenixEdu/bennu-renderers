/**
 * 
 */
package pt.ist.fenixWebFramework.servlets.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Shezad Anavarali Date: Sep 14, 2009
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {

    String value();

}
