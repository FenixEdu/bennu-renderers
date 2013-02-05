package pt.ist.fenixWebFramework.renderers;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeSet;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.utils.RendererPropertyUtils;

/**
 * This renderer allows a format string to be used conditionally, depending on
 * the rendered object's value
 * 
 * @author jcrn
 */
public class ConditionalFormatRenderer extends OutputRenderer {

    private final Map<String, ConditionalFormat> conditionalFormats;

    public ConditionalFormatRenderer() {
        conditionalFormats = new Hashtable<String, ConditionalFormat>();
    }

    /**
     * The format is associated with a condition that indicates if this format
     * can be used for this slot.
     * 
     * The condition is defined by specifying two parameters:
     * 
     * - useFormatIf (must be TRUE)
     * 
     * - useFormatIfNot (must be FALSE)
     * 
     * If both parameters are specified, the condition is true if useFormatIf is
     * TRUE _AND_ useFormatIfNot is FALSE.
     * 
     * If none of the parameters are specified, the condition is always TRUE.
     * 
     * When more than one format can be used for this slot, the one with the
     * lowest order is used.
     */
    public static class ConditionalFormat {

        public static final Comparator<ConditionalFormat> COMPARATOR_BY_ORDER = new Comparator<ConditionalFormat>() {
            @Override
            public int compare(ConditionalFormat o1, ConditionalFormat o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        };

        private String format;
        private String useFormatIf;
        private String useFormatIfNot;
        private Integer order = 0;
        private Boolean escaped = Boolean.FALSE;
        private Boolean useParent = Boolean.FALSE;

        /**
         * The format is simple string with a few rules to indicate where slot
         * values will be placed. An example string is
         * 
         * <pre>
         * &quot;${name} is ${age} years old&quot;
         * </pre>
         * 
         * Assuming that we are taking about an object that represents a person
         * and has a <code>getName()</code> and <code>getAge()</code> methods
         * this format would produce something like: <code>"Jane Doe is 20 years old"</code>
         * <p>
         * Any literal text will be present in the output. Slot values can be including by surrounding the slot name in
         * <code>${&lt;slot.name&gt;}</code>. Additionally you can indicate how to format each value. This is specially usefull if
         * you want to present dates.
         * 
         * <pre>
         * "${name} was born in a ${birthdate,%ta}
         * </pre>
         * 
         * Could present something like <code>"Jane Doe was born in a Sunday"</code> . You can use use the same syntax that is
         * accepted by # {@link String#format(java.lang.String, java.lang.Object[])}.
         * 
         * @property
         */
        public void setFormat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

        public void setUseFormatIf(String useFormatIf) {
            this.useFormatIf = useFormatIf;
        }

        public String getUseFormatIf() {
            return useFormatIf;
        }

        public void setUseFormatIfNot(String useFormatIfNot) {
            this.useFormatIfNot = useFormatIfNot;
        }

        public String getUseFormatIfNot() {
            return useFormatIfNot;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public Integer getOrder() {
            return order;
        }

        public void setEscaped(Boolean escaped) {
            this.escaped = escaped;
        }

        public Boolean isEscaped() {
            return escaped;
        }

        public void setUseParent(Boolean useParent) {
            this.useParent = useParent;
        }

        public Boolean isUseParent() {
            return useParent;
        }
    }

    public ConditionalFormat getConditionalFormat(String name) {
        ConditionalFormat format = conditionalFormats.get(name);
        if (format == null) {
            format = new ConditionalFormat();
            conditionalFormats.put(name, format);
        }
        return format;
    }

    public void setFormat(String name, String format) {
        getConditionalFormat(name).setFormat(format);
    }

    public void setUseFormatIf(String name, String useFormatIf) {
        getConditionalFormat(name).setUseFormatIf(useFormatIf);
    }

    public void setUseFormatIfNot(String name, String useFormatIfNot) {
        getConditionalFormat(name).setUseFormatIfNot(useFormatIfNot);
    }

    public void setOrder(String name, String order) {
        getConditionalFormat(name).setOrder(Integer.valueOf(order));
    }

    public void setEscaped(String name, String escaped) {
        getConditionalFormat(name).setEscaped(Boolean.valueOf(escaped));
    }

    public void setUseParent(String name, String useParent) {
        getConditionalFormat(name).setUseParent(Boolean.valueOf(useParent));
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new FormatLayout();
    }

    private class FormatLayout extends Layout {

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            if (object == null) {
                return new HtmlText();
            }

            TreeSet<ConditionalFormat> orderedFormats = new TreeSet<ConditionalFormat>(ConditionalFormat.COMPARATOR_BY_ORDER);
            orderedFormats.addAll(conditionalFormats.values());

            for (ConditionalFormat format : orderedFormats) {
                Object usedObject = getTargetObject(object, format);

                Boolean useFormatIfResult = null;
                Boolean useFormatIfNotResult = null;
                try {
                    if (format.getUseFormatIf() != null) {
                        useFormatIfResult =
                                (Boolean) RendererPropertyUtils.getProperty(usedObject, format.getUseFormatIf(), false);
                    }
                    if (format.getUseFormatIfNot() != null) {
                        useFormatIfNotResult =
                                (Boolean) RendererPropertyUtils.getProperty(usedObject, format.getUseFormatIfNot(), false);
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }

                if ((useFormatIfResult == null && useFormatIfNotResult == null)
                        || ((useFormatIfResult == null || useFormatIfResult) && (useFormatIfNotResult == null || !useFormatIfNotResult))) {
                    String formatedObject = RenderUtils.getFormattedProperties(format.getFormat(), usedObject);
                    return new HtmlText(formatedObject, format.isEscaped());
                }
            }

            // If no format condition is true
            return new HtmlText(RenderUtils.getFormattedProperties(null, object));
        }

        private Object getTargetObject(Object object, ConditionalFormat format) {
            if (format.isUseParent()) {
                PresentationContext parentContext = getContext().getParentContext();
                return (parentContext == null) ? null : parentContext.getMetaObject().getObject();
            } else {
                return object;
            }
        }
    }
}
