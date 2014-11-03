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
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.contexts.InputContext;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;

public interface IViewState extends Serializable {

    public String getId();

    public boolean isPostBack();

    public void setPostBack(boolean isPostBack);

    public boolean isCanceled();

    public void cancel();

    public HtmlComponent getComponent();

    public void setComponent(HtmlComponent component);

    public boolean isVisible();

    public void setVisible(boolean isVisible);

    public void setValid(boolean isValid);

    public boolean skipUpdate();

    public void setSkipUpdate(boolean skipUpdate);

    public boolean skipValidation();

    public void setSkipValidation(boolean skipValidation);

    public boolean isValid();

    public void setUpdateComponentTree(boolean updateTree);

    public boolean getUpdateComponentTree();

    public void addDestination(String name, ViewDestination destination);

    public ViewDestination getDestination(String name);

    public void setInputDestination(ViewDestination destination);

    public ViewDestination getInputDestination();

    public void setCurrentDestination(String name);

    public void setCurrentDestination(ViewDestination destination);

    public ViewDestination getCurrentDestination();

    public void setMetaObject(MetaObject object);

    public MetaObject getMetaObject();

    public HttpServletRequest getRequest();

    public void setRequest(HttpServletRequest request);

    public User getUser();

    public void setUser(User user);

    public String getLayout();

    public void setLayout(String layout);

    public Properties getProperties();

    public void setProperties(Properties properties);

    public void setContext(InputContext context);

    public InputContext getContext();

    public void setLocalAttribute(String name, Object value);

    public void setAttribute(String name, Object value);

    public Object getLocalAttribute(String name);

    public Object getAttribute(String name);

    public void removeLocalAttribute(String name);

    public void removeAttribute(String name);

    public void addHiddenSlot(HiddenSlot slot);

    public List<HiddenSlot> getHiddenSlots();

    public List<Message> setMessages(List<Message> messages);

    public List<Message> getMessages();

    public void addMessage(Message message);
}