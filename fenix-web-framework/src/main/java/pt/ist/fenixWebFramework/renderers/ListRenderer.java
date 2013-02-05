package pt.ist.fenixWebFramework.renderers;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import pt.ist.fenixWebFramework.rendererExtensions.CssStyler;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlList;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.layouts.ListLayout;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.utils.RendererPropertyUtils;

/**
 * This renderer provides a basic presentation for a {@link java.util.List}.
 * There is a direct translation from the list to an html list. As such, each
 * object in the list will be presented in a list item.
 * 
 * <p>
 * Example:
 * <ul>
 * <li><em>&lt;object A presentation&gt;</em></li>
 * <li><em>&lt;object B presentation&gt;</em></li>
 * <li><em>&lt;object C presentation&gt;</em></li>
 * </ul>
 * 
 * @author cfgi
 */
public class ListRenderer extends OutputRenderer {
    private String eachClasses;

    private String eachStyle;

    private String eachSchema;

    private String eachLayout;

    private boolean ordered;

    private String sortBy;

    private String nullLabel;

    private final Map<String, CssStyler> css;

    public ListRenderer() {
        super();

        this.ordered = false;
        this.css = new Hashtable<String, CssStyler>();

    }

    /**
     * The css classes to apply in each object's presentation.
     * 
     * @property
     */
    public void setEachClasses(String classes) {
        this.eachClasses = classes;
    }

    public String getEachClasses() {
        return this.eachClasses;
    }

    /**
     * The style to apply to each object's presentation.
     * 
     * @property
     */
    public void setEachStyle(String style) {
        this.eachStyle = style;
    }

    public String getEachStyle() {
        return this.eachStyle;
    }

    public String getEachLayout() {
        return eachLayout;
    }

    /**
     * The layout to be used when presenting each sub object.
     * 
     * @property
     */
    public void setEachLayout(String eachLayout) {
        this.eachLayout = eachLayout;
    }

    public String getEachSchema() {
        return eachSchema;
    }

    /**
     * The schema to be used in each sub object presentation.
     * 
     * @property
     */
    public void setEachSchema(String eachSchema) {
        this.eachSchema = eachSchema;
    }

    public boolean isOrdered() {
        return this.ordered;
    }

    /**
     * Selects if the generated list is a ordered list or a simple unordered
     * list. By default the list is unordered.
     * 
     * @property
     */
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    /**
     * Allows you to choose the order in wich the elements will be presented.-
     * See {@link pt.ist.fenixWebFramework.renderers.utils.RenderUtils#sortCollectionWithCriteria(Collection, String)} for more
     * details.
     * 
     * @property
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getNullLabel() {
        return nullLabel;
    }

    /**
     * String which will be presented when element is null.
     * 
     * @property
     */
    public void setNullLabel(String nullLabel) {
        this.nullLabel = nullLabel;
    }

    public CssStyler getCssStyler(String name) {
        CssStyler css = this.css.get(name);
        if (css == null) {
            css = new CssStyler();
            this.css.put(name, css);
        }
        return css;
    }

    public String getUseCssIf(String name) {
        return getCssStyler(name).getUseIf();
    }

    public void setUseCssIf(String name, String property) {
        getCssStyler(name).setUseIf(property);
    }

    public String getUseCssIfNot(String name) {
        return getCssStyler(name).getUseIfNot();
    }

    public void setUseCssIfNot(String name, String property) {
        getCssStyler(name).setUseIfNot(property);
    }

    public String getConditionalCss(String name) {
        return getCssStyler(name).getStyleClass();
    }

    public void setConditionalCss(String name, String style) {
        getCssStyler(name).setStyleClass(style);
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        Collection sortedCollection = RenderUtils.sortCollectionWithCriteria((Collection) object, getSortBy());

        return new ListRendererLayout(sortedCollection);
    }

    class ListRendererLayout extends ListLayout {

        private final Iterator iterator;

        public ListRendererLayout(Collection collection) {
            iterator = collection == null ? null : collection.iterator();
        }

        @Override
        protected HtmlComponent getContainer() {
            HtmlList list = (HtmlList) super.getContainer();
            list.setOrdered(isOrdered());

            return list;
        }

        @Override
        protected boolean hasMoreComponents() {
            return this.iterator != null && this.iterator.hasNext();
        }

        @Override
        protected HtmlComponent getNextComponent() {
            Object object = this.iterator.next();

            if (getNullLabel() != null && object == null) {
                return new HtmlText(getNullLabel());
            }

            Schema schema = RenderKit.getInstance().findSchema(getEachSchema());
            String layout = getEachLayout();

            HtmlComponent component = renderValue(object, schema, layout);
            applyStyles(object, component);
            return component;
        }

        protected void applyStyles(Object object, HtmlComponent component) {
            for (String style : css.keySet()) {
                CssStyler cssStyler = css.get(style);
                if (cssStyler != null) {
                    applyConditionalClasses(object, component, cssStyler);
                }
            }
        }

        protected void applyConditionalClasses(Object object, HtmlComponent component, CssStyler cssStyler) {
            Boolean useIf =
                    ((cssStyler.getUseIf() != null) && (Boolean) RendererPropertyUtils.getProperty(object, cssStyler.getUseIf(),
                            false));
            Boolean useIfNot =
                    ((cssStyler.getUseIfNot() != null) && !(Boolean) RendererPropertyUtils.getProperty(object,
                            cssStyler.getUseIfNot(), false));
            if (useIf || useIfNot) {
                component.setClasses(cssStyler.getStyleClass());
            }
        }
    }
}
