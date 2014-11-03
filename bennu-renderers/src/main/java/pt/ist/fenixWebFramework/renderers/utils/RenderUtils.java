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
package pt.ist.fenixWebFramework.renderers.utils;

import java.io.IOException;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.fenixedu.commons.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixWebFramework.rendererExtensions.util.IPresentableEnum;
import pt.ist.fenixWebFramework.renderers.components.state.ComponentLifeCycle;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.components.state.LifeCycleConstants;
import pt.ist.fenixWebFramework.renderers.plugin.RenderersRequestProcessorImpl;

// TODO Review much of this class
public class RenderUtils {
    private static final Logger logger = LoggerFactory.getLogger(RenderUtils.class);

    private static RenderersBundleResolver resolver = ResourceBundleMessageSource::new;

    private static ModuleResolver moduleResolver = new ModuleResolver() {

        @Override
        public String maybeResolveModule(HttpServletRequest request) {
            return null;
        }

        @Override
        public String maybeResolveActionMapping(String mapping, PageContext pageCtx) {
            return ((HttpServletRequest) pageCtx.getRequest()).getServletPath();
        }
    };

    public static void setModuleResolver(ModuleResolver res) {
        moduleResolver = Objects.requireNonNull(res);
    }

    public static void setBundleResolver(RenderersBundleResolver res) {
        resolver = Objects.requireNonNull(res);
    }

    public static String RESOURCE_LABEL_PREFIX = "label";

    /**
     * public static String getSlotLabel(Class objectType, String slotName,
     * String key) { return getSlotLabel(objectType, slotName, null, key); }
     */

    public static String getSlotLabel(Class objectType, String slotName, String bundle, String key, String... args) {
        String label = null;

        if (key != null) {
            label = RenderUtils.getResourceString(bundle, key, args);
        }

        if (label != null) {
            return label;
        } else if (key != null) {
            logger.warn("Key specified for slot '{}' does not exist: {}", slotName, key);
        }

        label = readClassResourceString(bundle, objectType, slotName, args);

        if (label != null) {
            return label;
        }

        label = RenderUtils.getResourceString(bundle, RenderUtils.RESOURCE_LABEL_PREFIX + "." + slotName, args);

        if (label != null) {
            return label;
        }

        label = RenderUtils.getResourceString(bundle, slotName, args);

        if (label != null) {
            return label;
        }

        if (slotName.contains(".")) {
            label = RenderUtils.getResourceString(bundle, slotName.substring(slotName.lastIndexOf(".") + 1), args);
        }

        if (label != null) {
            return label;
        }

        return slotName;
    }

    static private String readClassResourceString(String bundle, Class objectType, String slotName, String... args) {

        Class clazzIter = objectType;
        String label = null;

        while (clazzIter != null && !Object.class.equals(clazzIter)) {
            label =
                    RenderUtils.getResourceString(bundle, RenderUtils.RESOURCE_LABEL_PREFIX + "." + clazzIter.getName() + "."
                            + slotName);
            if (label != null) {
                return label;
            }

            clazzIter = clazzIter.getSuperclass();
        }

        return null;
    }

    public static String getResourceString(String key) {
        return getResourceString(null, key);
    }

    public static String getEnumString(Enum enumerate) {
        return getEnumString(enumerate, null);
    }

    public static String getEnumString(Enum enumerate, String bundle) {
        Enum e = enumerate;
        String description = null;

        Class enumClass = e.getClass();

        if (IPresentableEnum.class.isAssignableFrom(enumClass)) {
            return ((IPresentableEnum) enumerate).getLocalizedName();
        }

        if (bundle == null) {
            bundle = "ENUMERATION_RESOURCES";
        }

        if (!enumClass.isEnum() && Enum.class.isAssignableFrom(enumClass)) {
            enumClass = enumClass.getEnclosingClass();
        }

        String fullPrefix = enumClass.getName();

        description = getEnumStringFromFields(e);

        if (description == null) {
            description = RenderUtils.getResourceString(bundle, fullPrefix + "." + e.name());
        }

        if (description == null) {
            String simplePrefix = enumClass.getSimpleName();
            description = RenderUtils.getResourceString(bundle, simplePrefix + "." + e.name());
        }

        if (description == null) {
            description = RenderUtils.getResourceString(bundle, e.toString());
        }

        if (description == null) {
            description = RenderUtils.getResourceString(e.toString());
        }

        if (description == null) {
            description = e.toString();
        }

        return description;
    }

    static final private String[] fields = { "description" };

    static private String getEnumStringFromFields(final Enum oneEnum) {
        for (final String field : fields) {
            final String toInspect;
            try {
                toInspect = BeanUtils.getProperty(oneEnum, field);
            } catch (Exception e) {
                continue;
            }

            if (toInspect != null) {
                return toInspect;
            }
        }

        return null;
    }

    public static String getResourceString(String bundle, String key) {
        return getResourceString(bundle, key, null);
    }

    public static String getResourceString(String bundle, String key, Object[] args) {
        {
            RenderersMessageSource resources = getMessageResources(bundle);

            Optional<String> message = resources.getMessage(I18N.getLocale(), key);
            if (message.isPresent()) {
                return formatMessage(message.get(), args);
            }
        }

        {
            RenderersMessageSource rendererResources = getMessageResources("resources.RendererResources");

            Optional<String> message = rendererResources.getMessage(I18N.getLocale(), key);
            if (message.isPresent()) {
                return formatMessage(message.get(), args);
            }
        }

        return null;
    }

    private static final String formatMessage(String message, Object[] args) {
        if (args == null) {
            return message;
        }
        for (int i = 0; i < args.length; i++) {
            message =
                    message.replaceAll("\\{" + i + "\\}",
                            args[i] == null ? "" : Matcher.quoteReplacement(Objects.toString(args[i])));
        }
        return message;
    }

    private static Locale getLocale() {
        return I18N.getLocale();
    }

    public static RenderersMessageSource getMessageResources(String bundle) {
        return resolver.resolveBundle(bundle);
    }

    public static String getFormatedResourceString(String key, Object... args) {
        String text = getResourceString(key);

        if (text == null) {
            return key;
        }

        MessageFormat format = new MessageFormat(text);
        return format.format(args);
    }

    public static String getFormatedResourceString(String bundle, String key, Object... args) {
        String text = getResourceString(bundle, key);

        if (text == null) {
            return key;
        }

        MessageFormat format = new MessageFormat(text);
        return format.format(args);
    }

    public static String getFormattedProperties(String format, Object object) {
        // "${a.b} - ${a.c} - ${b,-4.5tY}"
        // String.format("%s - %s - %-4.5tY", object.getA().getB(),
        // object.getA().getC(), object.getB())

        // TODO: use a separator different than ',' because the comma can be
        // used as a flag in the format

        List<Object> args = new ArrayList<Object>();
        StringBuilder builder = new StringBuilder();

        if (format != null) {
            int lastIndex = 0, index;

            while ((index = format.indexOf("${", lastIndex)) != -1) {
                int end = format.indexOf("}", index + 2);

                if (end == -1) {
                    throw new RuntimeException("'" + format + "':unmatched group at pos " + index);
                }

                builder.append(format.substring(lastIndex, index));
                lastIndex = end + 1;

                if (end - index == 2) {
                    builder.append("%s");
                    args.add(object);
                } else {
                    String spec = format.substring(index + 2, end);
                    String[] parts = spec.split(",");

                    String property = parts[0];

                    if (parts.length > 1) {
                        builder.append("%" + parts[1]);
                    } else {
                        builder.append("%s");
                    }

                    try {
                        Object value = PropertyUtils.getProperty(object, property);
                        args.add(value);

                    } catch (Exception e) {

                        try {
                            Object value = findPropertyFromRequest(property);
                            args.add(value);
                        } catch (RuntimeException rt) {
                            throw rt;
                        } catch (Exception e1) {
                            throw new RuntimeException("could not retrieve property '" + property + "' for object " + object, e);
                        }
                    }
                }
            }

            builder.append(format.substring(lastIndex));
        }

        return String.format(getLocale(), builder.toString(), args.toArray());
    }

    private static Object findPropertyFromRequest(String property) throws Exception {

        final HttpServletRequest currentRequest = RenderersRequestProcessorImpl.getCurrentRequest();
        if (currentRequest != null) {
            final int indexOfDot = property.indexOf('.');
            final String objectName = indexOfDot != -1 ? property.substring(0, indexOfDot) : property;
            final String propertyName = indexOfDot != -1 ? property.substring(indexOfDot + 1) : null;

            Enumeration attributeNames = currentRequest.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                if (attributeNames.nextElement().equals(objectName)) {
                    final Object objectFromRequest = currentRequest.getAttribute(objectName);
                    if (objectFromRequest != null && propertyName != null) {
                        try {
                            return PropertyUtils.getProperty(objectFromRequest, propertyName);
                        } catch (Exception e1) {
                            throw new RuntimeException("could not retrieve property '" + propertyName + "' from request object "
                                    + objectName, e1);
                        }
                    }
                    return objectFromRequest;
                }
            }
        }

        throw new Exception("could not retrieve property '" + property + "' from request object ");
    }

    public static void setProperties(Object target, Properties properties) {
        if (properties == null) {
            return;
        }

        for (Object property : properties.keySet()) {
            String propertyName = null;

            try {
                propertyName = (String) property;

                if (PropertyUtils.isWriteable(target, propertyName)) {
                    BeanUtils.copyProperty(target, propertyName, properties.getProperty(propertyName));
                } else {
                    // even so try to write it because
                    // PropertyUtils.isWriteable() does not work for mapped
                    // items
                    try {
                        PropertyUtils.setProperty(target, propertyName, properties.getProperty(propertyName));
                    } catch (Exception e) {
                        logger.trace(
                                "The object " + target + " does not support property '" + propertyName + "': Not writeable!", e);
                    }
                }
            } catch (Exception e) {
                logger.warn("The object " + target + " does not support property '" + propertyName + "'", e);
            } // IllegalAccessException, InvocationTargetException,
              // NoSuchMethodException
        }
    }

    public static String getModuleRelativePath(HttpServletRequest request, String path) {
        String module = moduleResolver.maybeResolveModule(request);

        String returnPath;

        if (module != null) {
            returnPath = module + path;
        } else {
            returnPath = path;
        }

        return getContextRelativePath(request, returnPath);
    }

    public static String getModuleRelativePath(String path) {
        return getModuleRelativePath(RenderersRequestProcessorImpl.getCurrentRequest(), path);
    }

    public static String getContextRelativePath(HttpServletRequest request, String path) {
        String contextPath = request.getContextPath();

        return contextPath + path;
    }

    public static String getContextRelativePath(String path) {
        return getContextRelativePath(RenderersRequestProcessorImpl.getCurrentRequest(), path);
    }

    /**
     * Sorts a collection according to criteria encoded as a string. The
     * criteria as the form
     * 
     * <pre>
     *  criteria := single(,single)*
     *  single := slot(=order)?
     *  order := asc(ending)?|desc(ending)?
     *  slot := &lt;a slot name&gt;
     * </pre>
     * 
     * So if you want to order a list of persons alfabetically by name and from
     * the youngest to the oldes we could use <code>"name=asc,dateOfBirth=desc"</code>.
     * 
     * @param criteria
     *            the used criteria
     * @return the a new collection with the elements sorted by the criteria
     */
    public static <T> List<T> sortCollectionWithCriteria(Collection<? extends T> collection, String criteria) {
        if (collection == null) {
            return null;
        }

        List<T> result = new ArrayList<T>(collection);

        if (criteria == null) {
            return result;
        }

        String[] singleCriterias = criteria.split(",");

        if (singleCriterias.length == 0) {
            return result;
        }

        Comparator<Object> comparator = null; // TODO: change to a comparator
        // chain

        for (String singleCriteria2 : singleCriterias) {
            String singleCriteria = singleCriteria2.trim();

            if (singleCriteria.length() > 0) {
                String slot;
                String order;

                int orderIndex = singleCriteria.indexOf("=");
                if (orderIndex != -1) {
                    slot = singleCriteria.substring(0, orderIndex);
                    order = singleCriteria.substring(orderIndex + 1);
                } else {
                    slot = singleCriteria;
                    order = null;
                }

                boolean ascending = order == null || order.startsWith("asc");
                comparator = createCompositeComparator(comparator, slot, ascending);
            }
        }

        if (comparator != null) {
            Collections.sort(result, comparator);
        }

        return result;
    }

    /**
     * 
     * @param <T>
     * @param collection
     * @param criteria
     * @return
     */
    public static <T> List<T> sortCollectionWithCriteria(T[] collection, String criteria) {
        if (collection == null) {
            return null;
        }
        List<T> result = Arrays.asList(collection);
        return sortCollectionWithCriteria(result, criteria);
    }

    private static Comparator<Object> createCompositeComparator(final Comparator<Object> comparator, final String slot,
            final boolean ascending) {
        return new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                int slotComparison;

                if (comparator != null) {
                    slotComparison = comparator.compare(o1, o2);;
                } else {
                    slotComparison = 0;
                }

                if (slotComparison != 0) {
                    return slotComparison;
                }

                if (ascending) {
                    slotComparison = compareSlots(o1, o2, slot);
                } else {
                    slotComparison = compareSlots(o2, o1, slot);
                }

                return slotComparison;
            }

            private int compareSlots(Object o1, Object o2, String slot) {
                try {
                    Object slotObj1 = PropertyUtils.getProperty(o1, slot);
                    Object slotObj2 = PropertyUtils.getProperty(o2, slot);
                    if (slotObj1 == null && slotObj2 == null) {
                        return 0;
                    } else if (slotObj1 == null) {
                        return 1;
                    } else if (slotObj2 == null) {
                        return -1;
                    } else if (String.class.isAssignableFrom(PropertyUtils.getPropertyType(o1, slot))) {
                        return new BeanComparator(slot, Collator.getInstance()).compare(o1, o2);
                    } else {
                        return new BeanComparator(slot).compare(o1, o2);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

        };
    }

    //
    // ViewState related accessors to be used in actions
    // TODO: check the use of the methods for potential problems with the
    // renderers' common lifecycle
    //

    /**
     * Obtains the renderer's view state processed and contained in the current
     * request.
     */
    public static IViewState getViewState() {
        List<IViewState> viewStates =
                (List<IViewState>) RenderersRequestProcessorImpl.getCurrentRequest().getAttribute(
                        LifeCycleConstants.VIEWSTATE_PARAM_NAME);

        if (viewStates != null && viewStates.size() > 0) {
            return viewStates.get(0);
        } else {
            return null;
        }
    }

    /**
     * Obtains the renderer's view state processed and contained in the current
     * request that has the given id.
     */
    public static IViewState getViewState(String id) {
        List<IViewState> viewStates =
                (List<IViewState>) RenderersRequestProcessorImpl.getCurrentRequest().getAttribute(
                        LifeCycleConstants.VIEWSTATE_PARAM_NAME);

        if (viewStates != null) {
            for (IViewState state : viewStates) {
                if (id.equals(state.getId())) {
                    return state;
                }

            }
        }

        return null;
    }

    /**
     * Updates the current request to contain a custom view state. This methods
     * is intended to be used with a view state obtained from it's serialized
     * form and not directly with {@link #getViewState()}. After calling the
     * method the request can be forwarded to the location the original view
     * state was intended.
     */
    public static void setViewState(IViewState viewState) throws InstantiationException, IllegalAccessException, IOException,
            ClassNotFoundException {
        HttpServletRequest currentRequest = RenderersRequestProcessorImpl.getCurrentRequest();

        List<IViewState> viewStates = new ArrayList<IViewState>();
        viewStates.add(viewState);

        ComponentLifeCycle.getInstance().restoreComponent(viewState);
        ComponentLifeCycle.getInstance().prepareDestination(viewStates, currentRequest);
    }

    /**
     * Removes the renderer's view state from the current request.
     */
    public static void invalidateViewState() {
        RenderersRequestProcessorImpl.getCurrentRequest().setAttribute(LifeCycleConstants.VIEWSTATE_PARAM_NAME, null);
    }

    public static boolean invalidateViewState(String id) {
        List<IViewState> viewStates =
                (List<IViewState>) RenderersRequestProcessorImpl.getCurrentRequest().getAttribute(
                        LifeCycleConstants.VIEWSTATE_PARAM_NAME);

        if (viewStates == null) {
            return false;
        }

        for (Iterator<IViewState> iter = viewStates.iterator(); iter.hasNext();) {
            IViewState viewState = iter.next();

            if (id.equals(viewState.getId())) {
                iter.remove();
                return true;
            }
        }

        return false;
    }

    public static String escapeId(String id) {
        return id.replace(".", "\\\\.").replaceAll(":", "\\\\\\\\:");
    }

    public static String getModule(HttpServletRequest request) {
        return moduleResolver.maybeResolveModule(request);
    }

    public static String getActionMappingURL(String mapping, PageContext pageCtx) {
        return moduleResolver.maybeResolveActionMapping(mapping, pageCtx);
    }

    public static String getCurrentActionMappingURL(PageContext pageCtx) {
        return moduleResolver.maybeResolveActionMapping(null, pageCtx);
    }
}