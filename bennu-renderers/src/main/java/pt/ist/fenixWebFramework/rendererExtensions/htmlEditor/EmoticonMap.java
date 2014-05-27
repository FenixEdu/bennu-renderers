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
package pt.ist.fenixWebFramework.rendererExtensions.htmlEditor;

import java.util.HashMap;
import java.util.Map;

public class EmoticonMap {
    private static Map<String, String> emoticons;

    static {
        emoticons = new HashMap<String, String>();

        emoticons.put("cool", "B-)");
        emoticons.put("cry", ":'-(");
        emoticons.put("embarassed", ":-$");
        emoticons.put("foot-in-mouth", ":-!");
        emoticons.put("frown", ":-(");
        emoticons.put("innocent", "O:-)");
        emoticons.put("kiss", ":-*");
        emoticons.put("laughing", ":-D");
        emoticons.put("money-mouth", ":-$");
        emoticons.put("sealed", ":-x");
        emoticons.put("suprised", ":-o");
        emoticons.put("tongue-out", ":-P");
        emoticons.put("undecided", ":-/");
        emoticons.put("wink", ";-)");
        emoticons.put("yell", ":-O");
    }

    public static String getEmoticon(String name) {
        return emoticons.get(name);
    }
}
