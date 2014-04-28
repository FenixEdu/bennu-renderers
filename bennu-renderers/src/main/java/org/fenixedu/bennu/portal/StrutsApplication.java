package org.fenixedu.bennu.portal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.struts.action.Action;

/**
 * Declares an {@link Application} backed by Struts.
 * 
 * This annotation may be applied to any type, however, when applied
 * to a sub-class of {@link Action}, it will act as syntatic sugar, automatically
 * turning the Action into a "start page", i.e., an invisible functionality pointing
 * to this action.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StrutsApplication {

    String path();

    String bundle();

    String titleKey();

    String descriptionKey() default RenderersAnnotationProcessor.DELEGATE;

    String accessGroup() default "anyone";

    /*
     * Hint that is used to group Applications in the management UI
     */
    String hint() default "Struts";

}
