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
package pt.ist.fenixWebFramework.renderers.taglib;

import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.PropertyUtils;
import org.fenixedu.bennu.core.security.Authenticate;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.components.state.ViewDestination;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public abstract class BaseRenderObjectTag extends TagSupport {

    private String name;

    private String scope;

    private String property;

    private String type;

    private String layout;

    private String schema;

    private Schema anonymousSchema;

    private Properties properties;

    private String sortBy;

    private Map<String, ViewDestination> destinations;

    public BaseRenderObjectTag() {
        super();

        this.destinations = new Hashtable<String, ViewDestination>();
    }

    @Override
    public void release() {
        super.release();

        this.name = null;
        this.scope = null;
        this.property = null;
        this.type = null;
        this.layout = null;
        this.schema = null;
        this.properties = null;
        this.destinations = new Hashtable<String, ViewDestination>();
    }

    public Schema getAnonymousSchema() {
        return anonymousSchema;
    }

    public void setAnonymousSchema(Schema anonymousSchema) {
        this.anonymousSchema = anonymousSchema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLayout() {
        if (layout == null) {
            return null;
        }

        if (layout.equals("")) {
            return null;
        }

        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public void setTemplate(String template) {
        // TODO: cfgi, assign to a different field to respect the TagLib spec
        this.layout = "template";
        addRenderProperty("template", template);
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSchema() {
        if (schema == null) {
            return null;
        }

        if (schema.equals("")) {
            return null;
        }

        return schema;
    }

    public Properties getRenderProperties() {
        if (this.properties == null) {
            this.properties = new Properties();
        }

        return this.properties;
    }

    public void addRenderProperty(String name, String value) {
        getRenderProperties().setProperty(name, value);
    }

    public static int getScopeByName(String scope) throws JspException {
        switch (scope.toLowerCase()) {
        case "request":
            return PageContext.REQUEST_SCOPE;
        case "page":
            return PageContext.PAGE_SCOPE;
        case "session":
            return PageContext.SESSION_SCOPE;
        case "application":
            return PageContext.APPLICATION_SCOPE;
        default:
            throw new IllegalArgumentException("Cannot find page scope: " + scope);
        }
    }

    protected Object getTargetObject() throws JspException {
        Object object = getTargetObjectByName();

        return getTargetObjectByProperty(object);
    }

    protected Object getTargetObjectByProperty(Object object) {
        if (object != null && getProperty() != null) {
            try {
                return PropertyUtils.getProperty(object, getProperty());
            } catch (Exception e) {
                throw new RuntimeException("object '" + object + "' does not have property '" + getProperty() + "'", e);
            }
        }

        return object;
    }

    protected Object getTargetObjectByName() throws JspException {
        if (getName() != null) {
            if (getScope() != null && getScope().length() > 0) {
                return pageContext.getAttribute(getName(), getScopeByName(getScope()));
            } else {
                return pageContext.findAttribute(getName());
            }
        }

        return null;
    }

    @Override
    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        Object object = getTargetObject();

        if (object == null && !isNullAccepted()) {
            throw new RuntimeException("cannot present the null value, name='" + getName() + "' property='" + getProperty()
                    + "' scope='" + getScope() + "'");
        }

        // TODO: cfgi, verify if this is more usefull than problematic
        if (object instanceof List) {
            object = sortCollection((Collection) object);
        }

        String layout = getLayout();

        Properties properties = getRenderProperties();

        PresentationContext context = createPresentationContext(object, layout, getRealSchema(), properties);
        context.getViewState().setUser(Authenticate.getUser());

        HtmlComponent component = renderObject(context, object);

        try {
            drawComponent(context, component);
        } catch (IOException e) {
            e.printStackTrace();
            throw new JspException("failed to render component", e);
        }

        release(); // force release
        return EVAL_PAGE;
    }

    protected Schema getRealSchema() {
        Schema anonymousSchema = getAnonymousSchema();
        return anonymousSchema != null ? anonymousSchema : RenderKit.getInstance().findSchema(getSchema());
    }

    protected boolean isNullAccepted() {
        return getType() != null;
    }

    protected Collection sortCollection(Collection collection) {
        if (getSortBy() != null) {
            return RenderUtils.sortCollectionWithCriteria(collection, getSortBy());
        } else {
            return collection;
        }
    }

    protected abstract PresentationContext createPresentationContext(Object object, String layout, Schema schema,
            Properties properties);

    protected abstract HtmlComponent renderObject(PresentationContext context, Object object) throws JspException;

    protected void drawComponent(PresentationContext context, HtmlComponent component) throws IOException, JspException {
        component.draw(pageContext);
    }

    protected ViewDestination normalizeDestination(ViewDestination destination, String currentPath, String module) {
        if (destination.getModule() == null) {
            destination.setModule(module);
        }

        if (destination.getPath() == null) {
            destination.setPath(currentPath);
        }

        return destination;
    }

    public void addDestination(String name, String path, String module, boolean redirect) {
        this.destinations.put(name, new ViewDestination(path, module, redirect));
    }

    public Map<String, ViewDestination> getDestinations() {
        return this.destinations;
    }

    protected void setViewStateDestinations(IViewState viewState) {
        viewState.setInputDestination(getInputDestination());

        String currentPath = getCurrentPath();
        String module = RenderUtils.getModule((HttpServletRequest) pageContext.getRequest());

        for (String name : getDestinations().keySet()) {
            ViewDestination destination = getDestinations().get(name);

            viewState.addDestination(name, normalizeDestination(destination, currentPath, module));
        }
    }

    protected ViewDestination getInputDestination() {
        String currentPath = getCurrentPath();
        String module = RenderUtils.getModule((HttpServletRequest) pageContext.getRequest());

        return new ViewDestination(currentPath, module, false);
    }

    protected String getCurrentPath() {
        String mapping = RenderUtils.getCurrentActionMappingURL(pageContext);

        String currentPath;
        String contextPath = ((HttpServletRequest) pageContext.getRequest()).getContextPath();

        if (mapping != null) {
            currentPath = mapping;
        } else {
            currentPath = ((HttpServletRequest) pageContext.getRequest()).getServletPath();
        }

        if (currentPath.startsWith(contextPath)) {
            currentPath = currentPath.substring(contextPath.length());
        }

        String module = RenderUtils.getModule((HttpServletRequest) pageContext.getRequest());
        if (module != null && currentPath.startsWith(module)) {
            currentPath = currentPath.substring(module.length());
        }

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        if (request.getQueryString() != null) {
            currentPath = currentPath + "?" + request.getQueryString();
        }

        return currentPath;
    }

    public static Object lookup(PageContext pageContext, String name, String property, String scope) throws JspException {
        Object bean = scope == null ? pageContext.findAttribute(name) : pageContext.getAttribute(name, getScopeByName(scope));
        if (bean == null || property == null) {
            return bean;
        }
        try {
            return PropertyUtils.getProperty(bean, property);
        } catch (Exception e) {
            throw new JspException("Error while getting property '" + property + "' of " + bean, e);
        }
    }

}
