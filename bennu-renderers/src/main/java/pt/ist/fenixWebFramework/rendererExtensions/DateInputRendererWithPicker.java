package pt.ist.fenixWebFramework.rendererExtensions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.collections.Predicate;

import pt.ist.fenixWebFramework.renderers.DateInputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * This renderer provides a more fashionable way of doing the input of a date
 * than the plain DateInputRenderer. The date is accepted from a text input
 * field using a certain format, but there is an auxiliary javascript calendar
 * that pops out on the click of a small image butoon The format beeing accepted
 * is shown to the right of the textfield and before the calendar trigger
 * button.
 * 
 * <p>
 * Example: <input type="text" value="01/02/3456"/> dd/MM/yyyy <input type="button" value="Cal..." align="absmiddle" size="10"/>
 * 
 * @author José Pedro Pereira - Linkare TI
 * @author Paulo Abrantes
 */
public class DateInputRendererWithPicker extends DateInputRenderer {

    private String image;

    @Override
    protected HtmlComponent createTextField(Object object, Class type) {
        HtmlContainer container = (HtmlContainer) super.createTextField(object, type);

        HtmlTextInput input = (HtmlTextInput) container.getChild(new Predicate() {

            @Override
            public boolean evaluate(Object arg0) {
                return arg0 instanceof HtmlTextInput;
            }

        });

        MetaSlotKey key = (MetaSlotKey) getContext().getMetaObject().getKey();
        input.setId(key.toString());
        container.addChild(getCalendarScript(input.getId()));

        return container;
    }

    protected String getUrl(String base) {
        HtmlLink link = new HtmlLink();
        link.setModuleRelative(false);
        link.setContextRelative(true);
        link.setUrl(base);

        String imageUrl = link.calculateUrl();
        return imageUrl;
    }

    protected HtmlInlineContainer getCalendarScript(String inputId) {
        HtmlInlineContainer container = new HtmlInlineContainer();

        String scriptText =
                "$(function() { $(\"input#" + RenderUtils.escapeId(inputId) + "\").datepicker({showOn: 'button', buttonImage: '"
                        + getImage() + "', buttonImageOnly: false, firstDay: 1, currentText: '"
                        + RenderUtils.getResourceString("RENDERER_RESOURCES", "renderers.datePicker.currentText")
                        + "', monthNames: "
                        + RenderUtils.getResourceString("RENDERER_RESOURCES", "renderers.datePicker.monthNames")
                        + ", monthNamesShort: "
                        + RenderUtils.getResourceString("RENDERER_RESOURCES", "renderers.datePicker.monthNamesShort")
                        + ", dayNamesShort: "
                        + RenderUtils.getResourceString("RENDERER_RESOURCES", "renderers.datePicker.dayNamesShort")
                        + ", dayNamesMin: "
                        + RenderUtils.getResourceString("RENDERER_RESOURCES", "renderers.datePicker.dayNamesMin")
                        + ", dateFormat: '" + getInputFormatForCalendar() + "'});});";

        HtmlScript calendarScript = new HtmlScript();
        calendarScript.setContentType("text/javascript");
        calendarScript.setScript(scriptText);
        container.addChild(calendarScript);

        return container;
    }

    protected String getInputFormatForCalendar() {
        Locale locale = getLocale();
        SimpleDateFormat format = new SimpleDateFormat(getFormat(), locale);

        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, 1999);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH, 24);

        String dateStringFormatted = format.format(c.getTime());
        dateStringFormatted = dateStringFormatted.replace("1999", "yy");
        dateStringFormatted = dateStringFormatted.replace("99", "y");
        dateStringFormatted = dateStringFormatted.replace("12", "mm");
        dateStringFormatted = dateStringFormatted.replace("24", "dd");

        return dateStringFormatted;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
