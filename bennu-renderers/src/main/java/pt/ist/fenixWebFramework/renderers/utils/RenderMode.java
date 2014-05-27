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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public class RenderMode {
    private static Map<String, RenderMode> modes = new Hashtable<String, RenderMode>();

    static {
        addMode("output");
        addMode("input");
    }

    private String name;

    public RenderMode(String name) {
        this.name = name;
    }

    /**
     * @throws NullPointerException if name is null
     */
    public static RenderMode getMode(String name) {
        return RenderMode.modes.get(name.toLowerCase());
    }

    public static void addMode(String name) {
        RenderMode.modes.put(name, new RenderMode(name));
    }

    public static Collection<RenderMode> getAllModes() {
        return RenderMode.modes.values();
    }

    @Override
    public String toString() {
        return "RenderMode[" + this.name + "]";
    }
}
