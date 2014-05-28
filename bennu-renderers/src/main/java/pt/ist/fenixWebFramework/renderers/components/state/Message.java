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
package pt.ist.fenixWebFramework.renderers.components.state;

import java.io.Serializable;

public class Message implements Serializable {

    public static enum Type {
        GLOBAL, VALIDATION, CONVERSION
    };

    private Type type;
    private String message;

    protected Message(Type type, String message) {
        super();

        setType(type);
        setMessage(message);
    }

    public Message(String message) {
        this(Type.GLOBAL, message);
    }

    public String getMessage() {
        return this.message;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    public Type getType() {
        return this.type;
    }

    protected void setType(Type type) {
        this.type = type;
    }

}
