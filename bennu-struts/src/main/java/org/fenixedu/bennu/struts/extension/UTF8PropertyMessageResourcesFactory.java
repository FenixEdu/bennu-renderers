/**
 * Copyright © 2018 Instituto Superior Técnico
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

package org.fenixedu.bennu.struts.extension;

import org.apache.struts.config.MessageResourcesConfig;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.util.PropertyMessageResources;
import org.apache.struts.util.PropertyMessageResourcesFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;

public class UTF8PropertyMessageResourcesFactory extends MessageResourcesFactory {

    public static void enforce() {
        MessageResourcesFactory.setFactoryClass(UTF8PropertyMessageResourcesFactory.class.getName());
    }

    @Override
    public MessageResources createResources(String config) {
        return new UTF8PropertyMessageResources(this, config, this.returnNull);
    }

    @Override
    public MessageResourcesConfig getConfig() {
        MessageResourcesConfig messageResourcesConfig = new MessageResourcesConfig();
        messageResourcesConfig.setFactory(this.getClass().getName());
        return messageResourcesConfig;
    }
}
