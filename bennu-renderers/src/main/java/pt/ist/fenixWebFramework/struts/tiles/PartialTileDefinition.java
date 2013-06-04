package pt.ist.fenixWebFramework.struts.tiles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.tiles.ComponentDefinition;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixWebFramework.struts.annotations.Tile;
import pt.ist.fenixWebFramework.struts.annotations.TileCustomPropertyName;

public class PartialTileDefinition {

    private static final List<String> MANUALLY_HANDLED_TILE_METHODS = Arrays.asList("extend", "title", "bundle");

    private static final String DEFAULT_EXTEND = "defaultExtend";

    private static final Set<Method> tileMethods = new HashSet<Method>();

    private final Map<String, String> attributeValues = new HashMap<String, String>();
    private final Map<String, String> attributeDefaults = new HashMap<String, String>();

    private final String path;
    private final String extend;
    private final String title;
    private final String customBundleName;
    private final String defaultBundleName;

    private static final String RESOURCES_PREFIX = "resources.";
    private static final String RESOURCES_SUFFIX = "Resources";

    public static void init() {
        for (Method tileMethod : Tile.class.getDeclaredMethods()) {
            if (!skipMethod(tileMethod)) {
                tileMethods.add(tileMethod);
            }
        }
    }

    private static boolean skipMethod(Method method) {
        return (!method.getReturnType().equals(String.class)) || (MANUALLY_HANDLED_TILE_METHODS.contains(method.getName()));
    }

    public PartialTileDefinition(Forward forward, Forwards forwards, Mapping mapping, String defaultBundleName) {
        Tile localTile = forward.tileProperties();
        Tile globalTile = forwards.tileProperties();
        path = forward.path();
        extend = getDefinedValue(localTile.extend(), globalTile.extend(), getDefaultValue("extend"));
        title = getDefinedValue(localTile.title(), globalTile.title(), getDefaultValue("title"));
        customBundleName = getDefinedValue(localTile.bundle(), globalTile.bundle(), getDefaultValue("bundle"));
        this.defaultBundleName = (defaultBundleName == null) ? mapping.module() : defaultBundleName;
        for (Method tileMethod : tileMethods) {
            TileCustomPropertyName tileCustomProperty = tileMethod.getAnnotation(TileCustomPropertyName.class);
            String tileMethodName = (tileCustomProperty != null) ? tileCustomProperty.value() : tileMethod.getName();
            String localValue = invokeAnnotationMethod(tileMethod, localTile);
            String globalValue = invokeAnnotationMethod(tileMethod, globalTile);
            attributeValues.put(tileMethodName, getDefinedValue(localValue, globalValue, (String) tileMethod.getDefaultValue()));
            attributeDefaults.put(tileMethodName, (String) tileMethod.getDefaultValue());
        }
    }

    public PartialTileDefinition(String path) {
        this.path = path;
        extend = "";
        title = "";
        customBundleName = "";
        defaultBundleName = "";
    }

    private static String getDefinedValue(String localValue, String globalValue, String defaultValue) {
        if ((localValue == null) || (localValue.equals(defaultValue))) {
            return globalValue;
        }
        return localValue;
    }

    private static String getDefaultValue(String methodName) {
        try {
            return (String) Tile.class.getDeclaredMethod(methodName).getDefaultValue();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private static String invokeAnnotationMethod(Method method, Object annotation) {
        try {
            return (String) method.invoke(annotation, new Object[0]);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String getExtend() {
        return extend;
    }

    public boolean hasExtend() {
        return !((extend == null) || (extend.isEmpty()));
    }

    public boolean hasTitle() {
        return !((title == null) || (title.isEmpty()));
    }

    public boolean hasCustomBundleName() {
        return !((customBundleName == null) || (customBundleName.isEmpty()));
    }

    public String getName() {
        StringBuilder name = new StringBuilder();
        name.append(path);
        for (Entry<String, String> attribute : attributeValues.entrySet()) {
            if (!attribute.getValue().equals(attributeDefaults.get(attribute.getKey()))) {
                name.append("&").append(attribute.getKey()).append("=").append(attribute.getValue());
            }
        }
        if (hasTitle()) {
            name.append("&title=").append(title);
        }
        if (hasCustomBundleName()) {
            name.append("&bundle=").append(customBundleName);
        }
        if (hasExtend()) {
            name.append("&extend=").append(extend);
            return name.toString();
        } else {
            name.append("&").append(DEFAULT_EXTEND);
            return name.toString();
        }

    }

    public void updateComponentDefinition(ComponentDefinition componentDefinition, Locale locale) {
        componentDefinition.putAttribute("body", path);
        if (!title.isEmpty()) {
            try {
                componentDefinition.putAttribute("title", searchBundle(locale).getString(title));
            } catch (MissingResourceException ex) {
                componentDefinition.putAttribute("title", title);
            }
        }

        for (Entry<String, String> attribute : attributeValues.entrySet()) {
            if (!attribute.getValue().equals(attributeDefaults.get(attribute.getKey()))) {
                componentDefinition.putAttribute(attribute.getKey(), attribute.getValue());
            }
        }
    }

    private ResourceBundle searchBundle(Locale locale) throws MissingResourceException {
        if (hasCustomBundleName()) {
            return searchBundleByName(customBundleName, locale);
        } else {
            return searchBundleByName(defaultBundleName, locale);
        }
    }

    private static ResourceBundle searchBundleByName(String bundleName, Locale locale) {
        bundleName = StringUtils.removeStartIgnoreCase(bundleName, RESOURCES_PREFIX);
        bundleName = StringUtils.removeEndIgnoreCase(bundleName, RESOURCES_SUFFIX);
        bundleName = StringUtils.capitalize(bundleName);
        try {
            return getBundle(bundleName, locale);
        } catch (MissingResourceException e) {
            try {
                return getBundle(StringUtils.capitalize(bundleName.toLowerCase()), locale);
            } catch (MissingResourceException ex) {
                return getBundle(bundleName.toUpperCase(), locale);
            }
        }
    }

    private static ResourceBundle getBundle(String simpleName, Locale locale) {
        try {
            return ResourceBundle.getBundle(RESOURCES_PREFIX + simpleName + RESOURCES_SUFFIX, locale);
        } catch (MissingResourceException ex) {
            return ResourceBundle.getBundle(RESOURCES_PREFIX + simpleName, locale);
        }
    }
}
