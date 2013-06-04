package pt.ist.fenixWebFramework.rendererExtensions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class DateTimeInputRendererWithPicker extends DateTimeInputRenderer {

    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new DateTimeLayoutWithPicker();
    }

    public class DateTimeLayoutWithPicker extends DateTimeLayout {

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            HtmlComponent originalComponent = super.createComponent(object, type);

            MetaSlotKey key = (MetaSlotKey) getInputContext().getMetaObject().getKey();

            HtmlInlineContainer container = getCalendarScript(HtmlComponent.getValidIdOrName(key.toString() + "_date"));

            HtmlBlockContainer component = new HtmlBlockContainer();
            component.addChild(originalComponent);
            component.addChild(container);

            return component;
        }

        protected HtmlInlineContainer getCalendarScript(String inputId) {
            HtmlInlineContainer container = new HtmlInlineContainer();

            String scriptText =
                    "$(function() { $(\"input[name='" + RenderUtils.escapeId(inputId)
                            + "']\").datepicker({showOn: 'button', buttonImage: '" + getImage()
                            + "', buttonImageOnly: true, firstDay: 1, currentText: '"
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
            SimpleDateFormat format = new SimpleDateFormat(getDateFormat(), locale);

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

    }
}
