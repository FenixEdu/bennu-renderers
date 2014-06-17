package org.fenixedu.bennu.renderers.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Locale.Builder;

import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import pt.ist.fenixWebFramework.rendererExtensions.MultiLanguageStringRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;

@RunWith(JUnit4.class)
public class MLSRendererTest {

    private static final Locale en = new Builder().setLanguage("en").setRegion("GB").build();
    private static final Locale pt = new Builder().setLanguage("pt").setRegion("PT").build();

    private static final Locale ptBasic = new Builder().setLanguage("pt").build();

    private MultiLanguageStringRenderer renderer;

    private static final LocalizedString str = new LocalizedString(pt, "Olá").with(en, "Hello");
    private static final LocalizedString englishStr = new LocalizedString(en, "Hello");

    @Before
    public void setup() {
        renderer = new MultiLanguageStringRenderer();
        renderer.setLanguageShown(false);
    }

    @Test
    public void testMLSPT() {
        I18N.setLocale(pt);
        HtmlComponent component = renderer.render(str, LocalizedString.class);
        assertEquals(HtmlText.class, component.getClass());
        assertEquals("Olá", ((HtmlText) component).getText());
    }

    @Test
    public void testMLSEN() {
        I18N.setLocale(en);
        HtmlComponent component = renderer.render(str, LocalizedString.class);
        assertTrue(component instanceof HtmlText);
        assertEquals("Hello", ((HtmlText) component).getText());
    }

    @Test
    public void testMLSFuzzy() {
        I18N.setLocale(ptBasic);
        HtmlComponent component = renderer.render(str, LocalizedString.class);
        assertTrue(component instanceof HtmlText);
        assertEquals("Olá", ((HtmlText) component).getText());
    }

    @Test
    public void testDifferentLanguage() {
        I18N.setLocale(ptBasic);
        HtmlComponent component = renderer.render(englishStr, LocalizedString.class);
        assertEquals(2, component.getChildren().size());
        assertTrue(component.getChildren().get(0) instanceof HtmlText);
        assertTrue(component.getChildren().get(1) instanceof HtmlText);

        assertEquals("Hello", ((HtmlText) component.getChildren().get(0)).getText());
        assertEquals(" (inglês)", ((HtmlText) component.getChildren().get(1)).getText());
    }

    @Test
    public void testEmptyString() {
        HtmlComponent component = renderer.render(new LocalizedString(), LocalizedString.class);
        assertEquals(HtmlText.class, component.getClass());
        HtmlText text = (HtmlText) component;
        assertEquals("", text.getText());
    }

}
