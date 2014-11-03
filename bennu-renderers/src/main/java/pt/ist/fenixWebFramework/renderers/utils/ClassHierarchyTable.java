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
package pt.ist.fenixWebFramework.renderers.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Predicate;

public class ClassHierarchyTable<T> extends ConcurrentHashMap<Class, T> {

    private final List<Class> classSort;

    public ClassHierarchyTable() {
        super();

        this.classSort = new ArrayList<Class>();
    }

    @Override
    public T get(Object key) {
        return get(key, t -> true);
    }

    public T get(Object key, Predicate<T> predicate) {
        Class objectType = (Class) key;

        for (Class<? extends Object> type : this.classSort) {
            if (type.isAssignableFrom(objectType) && predicate.apply(super.get(type))) {
                return super.get(type);
            }
        }

        return null;
    }

    public T getUnspecific(Class key) {
        return super.get(key);
    }

    @Override
    public T put(Class key, T value) {
        addType(key);

        return super.put(key, value);
    }

    public int addType(Class type) {
        int index = findIndex(type);

        addType(type, index);
        return index;
    }

    public int findIndex(Class type) {
        int index = 0;

        for (Iterator iter = this.classSort.iterator(); iter.hasNext(); index++) {
            Class<? extends Object> element = (Class<? extends Object>) iter.next();

            // read this as "element is after type in list"
            if (element.isAssignableFrom(type)) {
                break;
            }
        }

        return index;
    }

    private void addType(Class type, int index) {
        // no elements? then just insert
        if (this.classSort.size() == 0) {
            this.classSort.add(type);
            return;
        }

        // insert at end is always ok
        if (index == this.classSort.size()) {
            this.classSort.add(index, type);
            return;
        }

        // avoid duplicates
        if (!this.classSort.get(index).equals(type)) {
            this.classSort.add(index, type);
        }
    }
}
