package pt.ist.fenixWebFramework.rendererExtensions;

import java.util.Collection;

import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyConverter;
import pt.ist.fenixWebFramework.renderers.CheckBoxOptionListRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.converters.EnumArrayConverter;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixframework.DomainObject;

/**
 * This is the Fenix extension to the
 * {@link pt.ist.fenixWebFramework.renderers.CheckBoxOptionListRenderer}.
 * 
 * {@inheritDoc}
 * 
 * @author cfgi
 */
public class InputCheckBoxListRenderer extends CheckBoxOptionListRenderer {
    private String choiceType;

    private String filterClass;

    private String emptyMessageKey;

    private String emptyMessageBundle;

    private String emptyMessageClasses;

    /**
     * This property allows you to configure the css classes used in the empty
     * message
     * 
     * @property
     */

    public String getEmptyMessageClasses() {
	return emptyMessageClasses;
    }

    public void setEmptyMessageClasses(String emptyMessageClasses) {
	this.emptyMessageClasses = emptyMessageClasses;
    }

    /**
     * This property allows you to configure the bundle that the empty message
     * key uses
     * 
     * @property
     */
    public String getEmptyMessageBundle() {
	return emptyMessageBundle;
    }

    public void setEmptyMessageBundle(String emptyMessageBundle) {
	this.emptyMessageBundle = emptyMessageBundle;
    }

    /**
     * This property allows you to configure a display message in case the
     * object list is empty
     * 
     * @property
     */
    public String getEmptyMessageKey() {
	return emptyMessageKey;
    }

    public void setEmptyMessageKey(String emptyMessageKey) {
	this.emptyMessageKey = emptyMessageKey;
    }

    public String getChoiceType() {
	return this.choiceType;
    }

    /**
     * This property is an abbreviation for a data provider that provides all
     * objects of a given type. The class named given must be a
     * {@link net.sourceforge.fenixedu.domain.DomainObject} beacuse this
     * renderers tries to read all objects using the
     * <tt>ReadAllDomainObjects</tt> service.
     * 
     * @property
     */
    public void setChoiceType(String choiceType) {
	this.choiceType = choiceType;
    }

    public String getFilterClass() {
	return this.filterClass;
    }

    /**
     * Since all objects of a given type are selected with
     * {@link #setChoiceType(String) choiceType}, this property allows you to
     * specify a {@link DataFilter data filter} that filters objects that are
     * not allowed from the collection created by the provider.
     * 
     * @property
     */
    public void setFilterClass(String filterClass) {
	this.filterClass = filterClass;
    }

    // HACK: duplicated code, id=inputChoices.selectPossibilitiesAndConverter
    @Override
    protected Converter getConverter() {

	if (getProviderClass() != null) {
	    return super.getConverter();
	} else {
	    try {
		Class choiceTypeClass = Class.forName(getChoiceType());

		if (DomainObject.class.isAssignableFrom(choiceTypeClass)) {
		    return new DomainObjectKeyConverter();
		} else if (Enum.class.isAssignableFrom(choiceTypeClass)) {
		    return new EnumArrayConverter(choiceTypeClass);
		} else {
		    return null;
		}
	    } catch (ClassNotFoundException e) {
		throw new RuntimeException("could not retrieve class named '" + getChoiceType() + "'");
	    }
	}
    }

    // HACK: duplicated code, id=inputChoices.selectPossibilitiesAndConverter
    @Override
    protected Collection getPossibleObjects() {

	if (getProviderClass() != null) {
	    return super.getPossibleObjects();
	} else {
	    Object object = getInputContext().getParentContext().getMetaObject().getObject();

	    String choiceType = getChoiceType();
	    String filterClassName = getFilterClass();

	    // try {
	    if (true)
		throw new Error("no.mechanism.available.for.reading.all.domain.objects.of.a.givin.type");
	    return null;
	    // Collection allChoices = readAllChoicesByType(choiceType);
	    //
	    // if (getFilterClass() != null) {
	    // Class filterClass = Class.forName(filterClassName);
	    // DataFilter filter = (DataFilter) filterClass.newInstance();
	    //
	    // List result = new ArrayList();
	    // for (Object choice : allChoices) {
	    // if (filter.acccepts(object, choice)) {
	    // result.add(object);
	    // }
	    // }
	    //
	    // return RenderUtils.sortCollectionWithCriteria(result,
	    // getSortBy());
	    // } else {
	    // return RenderUtils.sortCollectionWithCriteria(allChoices,
	    // getSortBy());
	    // }
	    // } catch (Exception e) {
	    // throw new RuntimeException("could not filter choices", e);
	    // }
	}

    }

    @Override
    public Layout getLayout(Object object, Class type) {
	return new InputCheckBoxLayoutWithEmptyMessage();
    }

    class InputCheckBoxLayoutWithEmptyMessage extends CheckBoxListLayout {

	private boolean empty;

	@Override
	public HtmlComponent createComponent(Object object, Class type) {
	    Collection collection = (Collection) object;
	    HtmlComponent component;

	    if (getEmptyMessageKey() != null && collection.isEmpty() && getPossibleObjects().isEmpty()) {
		component = new HtmlText(RenderUtils.getResourceString(getEmptyMessageBundle(), getEmptyMessageKey()));
		this.empty = true;
	    } else {
		component = super.createComponent(object, type);
		this.empty = false;
	    }
	    return component;
	}

	@Override
	public void applyStyle(HtmlComponent component) {
	    if (this.empty) {
		component.setClasses(getEmptyMessageClasses());
	    } else {
		super.applyStyle(component);
	    }

	}
    }
}