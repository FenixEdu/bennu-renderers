package pt.ist.fenixWebFramework.struts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TileCustomPropertyName {

    /**
     * Allows specifying that a Tile parameter name does not match correctly the
     * property that it is mapped to.
     */
    String value();

}
