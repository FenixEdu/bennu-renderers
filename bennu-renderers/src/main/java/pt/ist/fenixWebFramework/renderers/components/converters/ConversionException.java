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
package pt.ist.fenixWebFramework.renderers.components.converters;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class ConversionException extends RuntimeException {

    private boolean key = false;
    private Object[] arguments;

    public ConversionException() {
        super();
    }

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(String message, boolean key, Object... arguments) {
        super(message);

        this.key = key;
        this.arguments = arguments;
    }

    public ConversionException(String message, Throwable cause, boolean key, Object... arguments) {
        super(message, cause);

        this.key = key;
        this.arguments = arguments;
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getLocalizedMessage() {
        if (this.key) {
            return RenderUtils.getFormatedResourceString(getMessage(), this.arguments);
        } else {
            return super.getLocalizedMessage();
        }
    }
}
