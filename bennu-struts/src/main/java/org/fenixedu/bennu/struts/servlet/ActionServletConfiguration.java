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
package org.fenixedu.bennu.struts.servlet;

import java.util.Collection;

public class ActionServletConfiguration {

    public ResourceConfig resources;
    public Collection<ModuleConfiguration> modules;
    public Collection<ExceptionHandler> exceptionHandlers;

    public static class ResourceConfig {
        public Collection<String> bundles;
        public String defaultBundle;
    }

    public static class ModuleConfiguration {
        public String name;
        public String configFile;
    }

    public static class ExceptionHandler {
        public String handler;
        public Collection<ExceptionHandlerMapping> mappings;
    }

    public static class ExceptionHandlerMapping {
        public String type;
        public String key;
    }

}
