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

import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.util.PropertyMessageResources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;

public class UTF8PropertyMessageResources extends PropertyMessageResources {

    public UTF8PropertyMessageResources(MessageResourcesFactory factory, String config) {
        super(factory, config);
    }

    public UTF8PropertyMessageResources(MessageResourcesFactory factory, String config, boolean returnNull) {
        super(factory, config, returnNull);
    }

    @Override
    protected synchronized void loadLocale(String localeKey) {
        if (log.isTraceEnabled()) {
            log.trace("loadLocale(" + localeKey + ")");
        }

        // Have we already attempted to load messages for this locale?
        if (locales.get(localeKey) != null) {
            return;
        }

        locales.put(localeKey, localeKey);

        // Set up to load the property resource for this locale key, if we can
        String name = config.replace('.', '/');
        if (localeKey.length() > 0) {
            name += "_" + localeKey;
        }

        name += ".properties";
        InputStream is = null;
        Properties props = new Properties();

        // Load the specified property resource
        if (log.isTraceEnabled()) {
            log.trace("  Loading resource '" + name + "'");
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }

        is = classLoader.getResourceAsStream(name);
        if (is != null) {
            try {
                props.load(new InputStreamReader(is, "UTF-8"));

            } catch (IOException e) {
                log.error("loadLocale()", e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("loadLocale()", e);
                }
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("  Loading resource completed");
        }

        // Copy the corresponding values into our cache
        if (props.size() < 1) {
            return;
        }

        synchronized (messages) {
            Iterator names = props.keySet().iterator();
            while (names.hasNext()) {
                String key = (String) names.next();
                if (log.isTraceEnabled()) {
                    log.trace("  Saving message key '" + messageKey(localeKey, key));
                }
                messages.put(messageKey(localeKey, key), props.getProperty(key));
            }
        }
    }
}
