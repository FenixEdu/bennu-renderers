/* 
* @(#)ActionServletWrapper.java 
* 
* Copyright 2009 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package org.fenixedu.bennu.core.presentationTier.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ExceptionConfig;
import org.apache.struts.config.MessageResourcesConfig;
import org.apache.struts.config.ModuleConfig;
import org.fenixedu.bennu.core.presentationTier.servlets.ActionServletConfiguration.ExceptionHandler;
import org.fenixedu.bennu.core.presentationTier.servlets.ActionServletConfiguration.ExceptionHandlerMapping;
import org.fenixedu.bennu.core.presentationTier.servlets.ActionServletConfiguration.ModuleConfiguration;
import org.fenixedu.bennu.core.presentationTier.servlets.ActionServletConfiguration.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.Project;

import com.google.common.base.Joiner;
import com.google.gson.Gson;

/**
 * 
 * @author Luis Cruz
 * 
 */
@WebServlet(urlPatterns = ActionServletWrapper.URL_PATTERN, name = ActionServletWrapper.SERVLET_NAME, loadOnStartup = 1)
public class ActionServletWrapper extends ActionServlet {

    static final String URL_PATTERN = "*.do";
    static final String SERVLET_NAME = "ActionServlet";

    private static final long serialVersionUID = -6838259271932491702L;

    private static final Logger logger = LoggerFactory.getLogger(ActionServletWrapper.class);

    private final Map<String, MessageResourcesConfig> resourcesConfigurations = new HashMap<>();

    private final Collection<ExceptionConfig> exceptionConfigs = new ArrayList<>();

    private final Map<String, String> parameterMap = new HashMap<String, String>();

    private class ServletConfigWrapper implements ServletConfig {

        private final ServletConfig servletConfig;

        public ServletConfigWrapper(final ServletConfig servletConfig) {
            this.servletConfig = servletConfig;
        }

        @Override
        public String getInitParameter(final String name) {
            final String parameter = parameterMap.get(name);
            return parameter == null ? servletConfig == null ? null : servletConfig.getInitParameter(name) : parameter;
        }

        @Override
        public Enumeration getInitParameterNames() {
            return new Enumeration() {

                private final Enumeration enumeration = servletConfig == null ? null : servletConfig.getInitParameterNames();

                private final Iterator iterator = parameterMap.keySet().iterator();

                @Override
                public boolean hasMoreElements() {
                    return iterator.hasNext() || (enumeration != null && enumeration.hasMoreElements());
                }

                @Override
                public Object nextElement() {
                    return enumeration != null && enumeration.hasMoreElements() ? enumeration.nextElement() : iterator.next();
                }

            };
        }

        @Override
        public ServletContext getServletContext() {
            return servletConfig == null ? null : servletConfig.getServletContext();
        }

        @Override
        public String getServletName() {
            return servletConfig == null ? null : servletConfig.getServletName();
        }

    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        initializeParameterMapDefaults();
        initializeConfigurations();
        super.init(new ServletConfigWrapper(config));
    }

    @Override
    protected void initServlet() {
        this.servletName = SERVLET_NAME;
        this.servletMapping = URL_PATTERN;
        getServletContext().setAttribute(Globals.SERVLET_KEY, this.servletMapping);
    }

    private void initializeParameterMapDefaults() {
        parameterMap.put("config", "/WEB-INF/conf/struts-default.xml");
        //  parameterMap.put("application", "resources.ApplicationResources");

        parameterMap.put("debug", "3");
        parameterMap.put("detail", "3");
        parameterMap.put("validating", "true");
    }

    private void initializeConfigurations() throws ServletException {
        Gson gson = new Gson();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (Project artifact : FenixFramework.getProject().getProjects()) {

                String resource = capitalizeArtifactId(artifact.getName()) + "Resources";
                createMessageResourcesConfig(getMessageResourceBundleKey(resource), getMessageResourceBundleParameter(resource));

                try (InputStream stream = loader.getResourceAsStream(artifact.getName() + "/renderers.json")) {
                    if (stream != null) {
                        try (Reader reader = new InputStreamReader(stream)) {
                            ActionServletConfiguration configuration = gson.fromJson(reader, ActionServletConfiguration.class);
                            initializeResourceConfigurations(configuration.resources);
                            initializeModuleConfigurations(configuration.modules);
                            initializeExceptionHandlerConfigurations(configuration.exceptionHandlers);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    private void initializeResourceConfigurations(ResourceConfig resources) {
        if (resources != null) {
            for (String resource : resources.bundles) {
                createMessageResourcesConfig(getMessageResourceBundleKey(resource), getMessageResourceBundleParameter(resource));
            }

            if (resources.defaultBundle != null) {
                createMessageResourcesConfig(Globals.MESSAGES_KEY, getMessageResourceBundleParameter(resources.defaultBundle));
            }
        }
    }

    private void initializeModuleConfigurations(Collection<ModuleConfiguration> modules) {
        if (modules != null) {
            for (ModuleConfiguration module : modules) {
                parameterMap.put("config/" + module.name, "/WEB-INF/conf/" + module.configFile);
            }
        }
    }

    private void initializeExceptionHandlerConfigurations(Collection<ExceptionHandler> exceptionHandlers) {
        if (exceptionHandlers != null) {
            for (ExceptionHandler handler : exceptionHandlers) {
                for (ExceptionHandlerMapping mapping : handler.mappings) {
                    ExceptionConfig config = new ExceptionConfig();
                    config.setHandler(handler.handler);
                    config.setKey(mapping.key);
                    config.setType(mapping.type);
                    logger.debug("Adding exception config {}", config);
                    exceptionConfigs.add(config);
                }
            }
        }
    }

    @Override
    protected ModuleConfig initModuleConfig(String prefix, String paths) throws ServletException {
        logger.info("Initializing Struts Module '{}'", prefix);
        final ModuleConfig moduleConfig = super.initModuleConfig(prefix, paths);

        for (MessageResourcesConfig config : resourcesConfigurations.values()) {
            // If a ResourceConfig with the key already exists, do nothing, this
            // means the user configured the resources manually in struts-config.xml
            if (moduleConfig.findMessageResourcesConfig(config.getKey()) == null) {
                moduleConfig.addMessageResourcesConfig(config);
            }
        }

        for (ExceptionConfig config : exceptionConfigs) {
            moduleConfig.addExceptionConfig(config);
        }

        return moduleConfig;
    }

    private void createMessageResourcesConfig(final String key, final String parameter) {
        logger.debug("Adding Message Resource Config with key: {}, parameter: {}", key, parameter);
        final MessageResourcesConfig messageResourcesConfig = new MessageResourcesConfig();
        messageResourcesConfig.setFactory("org.apache.struts.util.PropertyMessageResourcesFactory");
        messageResourcesConfig.setKey(key);
        messageResourcesConfig.setNull(false);
        messageResourcesConfig.setParameter(parameter);

        this.resourcesConfigurations.put(key, messageResourcesConfig);
    }

    private String capitalizeArtifactId(String name) {
        List<String> parts = new ArrayList<>();
        for (String string : name.split("-")) {
            parts.add(string.substring(0, 1).toUpperCase() + string.substring(1));
        }
        return Joiner.on("").join(parts);
    }

    private String getMessageResourceBundleKey(final String resource) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < resource.length(); i++) {
            final char c = resource.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                stringBuilder.append('_');
            }
            stringBuilder.append(Character.toUpperCase(c));
        }
        return stringBuilder.toString();
    }

    private String getMessageResourceBundleParameter(final String resource) {
        return "resources." + resource;
    }

}
