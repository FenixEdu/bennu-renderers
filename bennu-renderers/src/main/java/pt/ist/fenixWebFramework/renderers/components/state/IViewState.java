package pt.ist.fenixWebFramework.renderers.components.state;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
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

    public void setContext(PresentationContext context);

    public PresentationContext getContext();

    public Class getContextClass();

    public void setContextClass(Class contextClass);

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