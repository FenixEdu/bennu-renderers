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
package pt.ist.fenixWebFramework;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

public class RenderersConfigurationManager {
    @ConfigurationManager(description = "Bennu Renderers Configuration")
    public interface ConfigurationProperties {
        @ConfigurationProperty(key = "filterRequestWithDigest",
                description = "Specifies if a checksum is generated and verified for each URL to avoid url tampering attacks.",
                defaultValue = "true")
        public Boolean filterRequestWithDigest();

        @ConfigurationProperty(
                key = "tamperingRedirect",
                description = "Redirect link to use when a URL tampering is detected. Required when 'filterRequestWithDigest' is true",
                defaultValue = "/")
        public String tamperingRedirect();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }
}