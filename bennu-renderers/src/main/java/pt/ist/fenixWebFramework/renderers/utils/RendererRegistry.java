package pt.ist.fenixWebFramework.renderers.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import pt.ist.fenixWebFramework.renderers.exceptions.NoRendererException;

import com.google.common.base.Predicate;

public class RendererRegistry {

    private ClassHierarchyTable<Map<String, RendererDescription>> renderersTable;

    public RendererRegistry() {
        super();

        this.renderersTable = new ClassHierarchyTable<Map<String, RendererDescription>>();
    }

    public void registerRenderer(Class type, String layout, Class renderer, Properties defaultProperties) {
        Map<String, RendererDescription> layoutsTable = this.renderersTable.getUnspecific(type);

        if (layoutsTable == null) {
            this.renderersTable.put(type, new HashMap<String, RendererDescription>());
            layoutsTable = this.renderersTable.getUnspecific(type);
        }

        layoutsTable.put(layout, new RendererDescription(renderer, defaultProperties));
    }

    public RendererDescription getRenderDescription(Class objectType, final String layout) {
        Map<String, RendererDescription> layoutsTable =
                renderersTable.get(objectType, new Predicate<Map<String, RendererDescription>>() {
                    @Override
                    public boolean apply(Map<String, RendererDescription> table) {
                        return table.get(layout) != null;
                    }
                });

        if (layoutsTable == null) {
            throw new NoRendererException(objectType, layout);
        }

        return layoutsTable.get(layout);
    }

    public RendererDescription getExactRenderDescription(Class objectType, String layout) {
        Map<String, RendererDescription> layoutsTable = renderersTable.getUnspecific(objectType);

        if (layoutsTable == null) {
            throw new NoRendererException(objectType, layout);
        }

        return layoutsTable.get(layout);
    }
}
