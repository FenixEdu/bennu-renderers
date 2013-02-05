package pt.ist.fenixWebFramework.rendererExtensions;

import java.util.Collection;

import pt.ist.fenixWebFramework.renderers.MenuOptionListRenderer;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

/**
 * Fenix extension to the {@link pt.ist.fenixWebFramework.renderers.MenuOptionListRenderer}.
 * 
 * {@inheritDoc}
 * 
 * @author cfgi
 */
public class InputMenuOptionListRenderer extends MenuOptionListRenderer {
    private String filterClass;

    public String getFilterClass() {
        return this.filterClass;
    }

    /**
     * This property allows you to indicate a {@linkplain DataFilter data
     * filter} that will remove values, from the collection returned by data
     * provider, not valid in a specific context.
     * 
     * @property
     */
    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    // HACK: duplicated code, id=inputChoices.selectPossibilitiesAndConverter
    @Override
    protected Converter getConverter() {
        return super.getConverter();
    }

    // HACK: duplicated code, id=inputChoices.selectPossibilitiesAndConverter
    @Override
    protected Collection getPossibleObjects() {
        return super.getPossibleObjects();
    }

}
