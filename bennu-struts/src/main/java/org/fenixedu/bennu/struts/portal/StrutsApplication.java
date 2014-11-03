/**
 * Copyright © 2008 Instituto Superior Técnico
 *
 * This file is part of Bennu Renderers Framework.
 *
 * Bennu Renderers Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Renderers Framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Renderers Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.struts.portal;

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
