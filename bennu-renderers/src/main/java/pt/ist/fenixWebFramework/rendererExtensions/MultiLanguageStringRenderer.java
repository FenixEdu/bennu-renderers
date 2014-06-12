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
package pt.ist.fenixWebFramework.rendererExtensions;

import java.util.Locale;
import java.util.Locale.Builder;

import org.fenixedu.commons.i18n.I18N;

import pt.ist.fenixWebFramework.renderers.StringRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

import com.google.common.base.Strings;

/**
 * This renderer provides a standard way of presenting a {@link MultiLanguageString}. The <tt>MultiLanguageString</tt> is
 * presented as a simple string. The string to be presented
 * is determined by the logic in {@link MultiLanguageString#getContent()}. Additionally you
 * can override the language in which the content is to be displayed with the {@link #setLanguage(String) language} property. In
 * this case the content to be presented
 * will be determined by {@link MultiLanguageString#getContent(Locale)}
 * 
 * @author cfgi
 * @author cgmp
 */
public class MultiLanguageStringRenderer extends StringRenderer {

    private String language;
    private boolean forceShowLanguage;
    private boolean languageShown;
    private boolean inline;
    private String languageClasses;

    public MultiLanguageStringRenderer() {
        super();

        setLanguageShown(true);
        setInline(true);
        setShowLanguageForced(false);
    }

    public String getLanguage() {
        return language;
    }

    /**
     * Allows you to override the language in wich the <tt>MultiLanguageString</tt> content
     * will be presented.
     * 
     * @property
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isInline() {
        return this.inline;
    }

    /**
     * Allows you to choose if a span or a div will be generated around the multi language string.
     * This can be usefull if the multi-language string contains much information or html code.
     * 
     * @property
     */
    public void setInline(boolean inline) {
        this.inline = inline;
    }

    public boolean isLanguageShown() {
        return this.languageShown;
    }

    /**
     * Whenever a multi-language string is shown in a language that is not what the user requested
     * an annotation is added to shown in wich language the text is in. This property allows you
     * to override that behaviour.
     * 
     * @property
     */
    public void setLanguageShown(boolean languageShown) {
        this.languageShown = languageShown;
    }

    public String getLanguageClasses() {
        return this.languageClasses;
    }

    /**
     * Choose the css class to apply to the annotation showing the value's
     * language when it isn't in the requested language.
     * 
     * @property
     */
    public void setLanguageClasses(String languageClasses) {
        this.languageClasses = languageClasses;
    }

    public boolean isShowLanguageForced() {
        return this.forceShowLanguage;
    }

    /**
     * Force the diplay of the language of the text even when showing text in
     * the language requested by the user.
     * 
     * @property
     */
    public void setShowLanguageForced(boolean forceShowLanguage) {
        this.forceShowLanguage = forceShowLanguage;
    }

    @Override
    protected HtmlComponent renderComponent(Layout layout, Object object, Class type) {
        if (object == null) {
            return super.renderComponent(layout, null, type);
        }

        final MultiLanguageString mlString = (MultiLanguageString) object;
        final String value = getRenderedText(mlString);

        final HtmlComponent component = super.renderComponent(layout, value, type);

        final Locale contentLocale = getUsedLanguage(mlString);
        final String language = contentLocale.getLanguage();

        if ((language.equals(I18N.getLocale().getLanguage()) && !isShowLanguageForced() && !isLanguageShown())
                || mlString.isEmpty()) {
            return component;
        }

        component.setLanguage(contentLocale.toLanguageTag());

        HtmlContainer container = isInline() ? new HtmlInlineContainer() : new HtmlBlockContainer();
        container.addChild(component);
        container.setIndented(false);

        HtmlComponent languageComponent = contentLocale == null ? new HtmlText() : new HtmlText(contentLocale.getDisplayName(I18N.getLocale()));
        languageComponent.setClasses(getLanguageClasses());

        container.addChild(new HtmlText(" (", false));
        container.addChild(languageComponent);
        container.addChild(new HtmlText(")", false));

        return container;
    }

    private Locale getUsedLanguage(MultiLanguageString mlString) {
        final Locale locale = getLanguage() != null ? new Builder().setLanguageTag(getLanguage()).build() : I18N.getLocale();
        return getAvailableLocaleFromMls(mlString, locale);
    }

    private Locale getAvailableLocaleFromMls(final MultiLanguageString mlString, final Locale locale) {
        if (mlString.getContent(locale) != null) {
            return locale;
        }
        final Locale lessSpecific = generifyLocale(locale);
        return lessSpecific != null ? getAvailableLocaleFromMls(mlString, lessSpecific) : mlString.getContentLocale();
    }

    private Locale generifyLocale(Locale locale) {
        if (Strings.isNullOrEmpty(locale.getVariant())) {
            if (Strings.isNullOrEmpty(locale.getCountry())) {
                return null;
            }
            return new Locale(locale.getLanguage());
        }
        return new Locale(locale.getLanguage(), locale.getCountry());
    }

    protected String getRenderedText(MultiLanguageString mlString) {
        Locale locale = getUsedLanguage(mlString);
        return locale == null ? null : mlString.getContent(locale);
    }

}
