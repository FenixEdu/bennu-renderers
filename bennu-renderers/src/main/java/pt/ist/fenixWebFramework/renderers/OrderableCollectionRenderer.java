package pt.ist.fenixWebFramework.renderers;

import java.util.Collection;

import pt.ist.fenixWebFramework.renderers.components.HtmlActionLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlImage;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.state.ViewDestination;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * This renderer provides a tabular presentation for a collection that allows
 * rows of the table to be sorted by clicking the table headers. The behaviour
 * of this renderer is very similar to the one of {@link pt.ist.fenixWebFramework.renderers.CollectionRenderer}.
 * 
 * <p>
 * Example:
 * <table border="1">
 * <thead>
 * <th><a href="#">Name</a></th>
 * <th>V <a href="#">Age</a></th>
 * </thead>
 * <tr>
 * <td>Name A</td>
 * <td>20</td>
 * </tr>
 * <tr>
 * <td>Name C</td>
 * <td>21</td>
 * </tr>
 * <tr>
 * <td>Name B</td>
 * <td>22</td>
 * </tr>
 * </table>
 * 
 * @author cfgi
 */
public class OrderableCollectionRenderer extends CollectionRenderer {

    static private final String AL_ON_CLICK = "document.getElementById('%s').%s.value='%s=%s';";

    private String sortFormId;

    private boolean sortActionLink = false;

    private String sortUrl;

    private String sortParameter;

    private String ascendingClasses;

    private String descendingClasses;

    private String ascendingImage;

    private String descendingImage;

    private boolean contextRelative;

    private String sortableSlots;

    private boolean sortIgnored;

    public boolean isSortIgnored() {
        return sortIgnored;
    }

    /**
     * 
     * @property
     */
    public void setSortIgnored(boolean sortIgnored) {
        this.sortIgnored = sortIgnored;
    }

    public OrderableCollectionRenderer() {
        setContextRelative(true);
    }

    public String getAscendingClasses() {
        return this.ascendingClasses;
    }

    public String getDescendingClasses() {
        return this.descendingClasses;
    }

    public String getAscendingImage() {
        return this.ascendingImage;
    }

    public String getDescendingImage() {
        return this.descendingImage;
    }

    public boolean isContextRelative() {
        return this.contextRelative;
    }

    public String getSortFormId() {
        return sortFormId;
    }

    /**
     * Represents the form id used when generated link is an action link.
     * 
     * @property
     */
    public void setSortFormId(String sortFormId) {
        this.sortFormId = sortFormId;
    }

    public boolean isSortActionLink() {
        return sortActionLink;
    }

    /**
     * If <code>true</code>, generate link as an action link. Must also set sort
     * form id property value, that indicates which form to submit.
     * 
     * @property
     */
    public void setSortActionLink(boolean sortActionLink) {
        this.sortActionLink = sortActionLink;
    }

    public String getSortUrl() {
        return this.sortUrl;
    }

    /**
     * Indicates the url used to sort collection. Must be setted if action link
     * is not used.
     * 
     * @property
     */
    public void setSortUrl(String sortUrl) {
        this.sortUrl = sortUrl;
    }

    public String getSortParameter() {
        return this.sortParameter;
    }

    /**
     * Specify the parameter value used to sort collection
     * 
     * @property
     */
    public void setSortParameter(String sortParameter) {
        this.sortParameter = sortParameter;
    }

    /**
     * The classes to use in a header when the corresponding column is ordered
     * in <strong>ascending</strong> mode. This property can be used to use a
     * custom style that denotes an ascending ordering.
     * 
     * @property
     */
    public void setAscendingClasses(String ascendingClasses) {
        this.ascendingClasses = ascendingClasses;
    }

    /**
     * The classes to use in a header when the corresponding column is ordered
     * in <strong>descending</strong> mode. This property can be used to use a
     * custom style that denotes an descending ordering.
     * 
     * @property
     */
    public void setDescendingClasses(String descendingClasses) {
        this.descendingClasses = descendingClasses;
    }

    /**
     * If this property is specified an image will be placed to the left of the
     * header title. This image will be used when the header is clicked and the
     * ordering is <strong>ascending</strong>.
     * 
     * @property
     */
    public void setAscendingImage(String ascendingImage) {
        this.ascendingImage = ascendingImage;
    }

    /**
     * If this property is specified an image will be placed to the left of the
     * header title. This image will be used when the header is clicked and the
     * ordering is <strong>descending</strong>.
     * 
     * @property
     */
    public void setDescendingImage(String descendingImage) {
        this.descendingImage = descendingImage;
    }

    /**
     * This property specifies whether the image url given in {@link #setAscendingImage(String) ascendingImage} or
     * {@link #setDescendingImage(String) descendingImage} is relative to the
     * application context or not.
     * 
     * @property
     */
    public void setContextRelative(boolean contextRelative) {
        this.contextRelative = contextRelative;
    }

    public String getSortableSlots() {
        return sortableSlots;
    }

    /**
     * Colon separated values of slot names. If this property is not specified
     * then all slots are considered sortable. If this property is sent then
     * only the slots indicated are sortable.
     * <p>
     * Example: <code>"a, b, c"</code>
     * 
     * @property
     */
    public void setSortableSlots(String sortableSlots) {
        this.sortableSlots = sortableSlots;
    }

    private String getImagePath(String path) {
        if (isContextRelative()) {
            return getContext().getViewState().getRequest().getContextPath() + path;
        } else {
            return path;
        }
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        Collection sortedCollection =
                (isSortIgnored()) ? (Collection) object : RenderUtils
                        .sortCollectionWithCriteria((Collection) object, getSortBy());

        return new OrderedCollectionTabularLayout(sortedCollection);
    }

    protected class OrderedCollectionTabularLayout extends CollectionTabularLayout {

        public OrderedCollectionTabularLayout(Collection object) {
            super(object);
        }

        @Override
        protected HtmlComponent getHeaderComponent(int columnIndex) {
            if (columnIndex == 0 && isCheckable()) {
                return new HtmlText();
            } else if (columnIndex < getNumberOfColumns() - getNumberOfLinkColumns()) {
                HtmlComponent component = super.getHeaderComponent(columnIndex);
                String slotName = getObject(0).getSlots().get(columnIndex - (isCheckable() ? 1 : 0)).getName();

                if (!isSortable(slotName)) {
                    return component;
                }

                if (isSortActionLink()) {
                    return buildActionLink(component, slotName);
                }

                HtmlLink link = new HtmlLink();

                if (getSortUrl() != null) {
                    link.setUrl(getSortUrl());
                } else {
                    ViewDestination destination = getContext().getViewState().getInputDestination();

                    link.setModule(destination.getModule());
                    link.setUrl(destination.getPath());
                }

                link.setBody(component);

                if (getSortBy() != null && getSortBy().contains(slotName)) {
                    if (getSortBy().contains("=desc")) {
                        link.setParameter(getSortParameter(), slotName + "=ascending");
                        component = wrapComponent(link, false);
                    } else {
                        link.setParameter(getSortParameter(), slotName + "=descending");
                        component = wrapComponent(link, true);
                    }
                } else {
                    link.setParameter(getSortParameter(), slotName + "=ascending");
                    component = wrapComponent(link, false);
                }

                return component;
            } else {
                return new HtmlText();
            }
        }

        private HtmlComponent buildActionLink(final HtmlComponent component, final String slotName) {
            final HtmlActionLink link = new HtmlActionLink();
            link.setBody(component);

            final HtmlComponent result;
            if (getSortBy() != null && getSortBy().contains(slotName)) {
                if (getSortBy().contains("=desc")) {
                    link.setOnClick(String.format(AL_ON_CLICK, getSortFormId(), getSortParameter(), slotName, "asc"));
                    result = wrapComponent(link, false);
                } else {
                    link.setOnClick(String.format(AL_ON_CLICK, getSortFormId(), getSortParameter(), slotName, "desc"));
                    result = wrapComponent(link, true);
                }
            } else {
                link.setOnClick(String.format(AL_ON_CLICK, getSortFormId(), getSortParameter(), slotName, "asc"));
                result = wrapComponent(link, false);
            }

            return result;
        }

        private boolean isSortable(String slotName) {
            String sortableSlots = getSortableSlots();

            if (sortableSlots == null) {
                return true;
            }

            String[] slots = sortableSlots.split(",");
            for (String slot : slots) {
                String trimmed = slot.trim();

                if (trimmed.length() == 0) {
                    continue;
                }

                if (trimmed.equals(slotName)) {
                    return true;
                }
            }

            return false;
        }

        private HtmlComponent wrapComponent(HtmlComponent component, boolean ascending) {
            String image = null;

            if (ascending) {
                if (getAscendingClasses() != null) {
                    component.setClasses(getAscendingClasses());
                }

                image = getAscendingImage();
            } else {
                if (getDescendingClasses() != null) {
                    component.setClasses(getDescendingClasses());
                }

                image = getDescendingImage();
            }

            if (image == null) {
                return component;
            }

            HtmlContainer container = new HtmlInlineContainer();

            HtmlImage htmlImage = new HtmlImage();
            htmlImage.setSource(getImagePath(image));

            container.addChild(htmlImage);
            container.addChild(component);

            return container;
        }

    }

}
