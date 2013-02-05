package pt.ist.fenixWebFramework.struts.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tile {
    /**
     * The name of the tile definition that this tile will directly extend from.
     * In case no super-tile is specified, the module's default tile definition
     * will be obtained in runtime and used.
     * 
     * Useful when the default tile is either incomplete or not exactly what
     * you're looking for.
     */
    String extend() default "";

    /**
     * The label used as a title on the page we are forwarding to. This label
     * will be searched on the 'bundle' resource file. If no 'bundle' property
     * was specified, this label will be searched on the default module's
     * resource file.
     * 
     * If no match is found, the title will be used as plain text.
     */
    String title() default "";

    /**
     * The bundle resource that will be used to obtain message resources (such
     * as the title)
     */
    String bundle() default "";

    // Auto-detected fields below
    String head() default "";

    String navLocal() default "";

    String navGeral() default "";

    @TileCustomPropertyName("body-context")
    String bodyContext() default "";

    String hideLanguage() default "";
}
