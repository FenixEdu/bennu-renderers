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
package pt.ist.fenixWebFramework.rendererExtensions.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ObjectChange {

    public final ObjectKey key;

    public final String slot;

    public final Object value;

    public final Method setter;

    public final Constructor constructor;

    public final Object[] values;

    public ObjectChange(ObjectKey key, String slot, Object value) {
        this.key = key;
        this.slot = slot;
        this.value = value;

        this.setter = null;
        this.constructor = null;
        this.values = null;
    }

    public ObjectChange(ObjectKey key, Method setter, Object[] values) {
        this.key = key;
        this.setter = setter;
        this.values = values;

        this.slot = null;
        this.constructor = null;
        this.value = null;
    }

    public ObjectChange(ObjectKey key, Constructor constructor, Object[] values) {
        this.key = key;
        this.constructor = constructor;
        this.values = values;

        this.slot = null;
        this.setter = null;
        this.value = null;
    }
}