/**
 * 
 */
package pt.ist.fenixWebFramework.rendererExtensions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.Predicate;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * @author Joao Carvalho (joao.pedro.carvalho@ist.utl.pt)
 * 
 */
public class IntervalAsDatesInputRendererWithPicker extends IntervalAsDatesInputRenderer {

    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new IntervalLayoutWithPicker(new SimpleDateFormat(getDateFormat(), getLocale()));
    }

    public class IntervalLayoutWithPicker extends IntervalLayout {

        /**
         * @param simpleDateFormat
         */
        public IntervalLayoutWithPicker(DateFormat simpleDateFormat) {
            super(simpleDateFormat, getStartLabel(), getEndLabel());
        }

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            HtmlComponent originalComponent = super.createComponent(object, type);

            List<HtmlComponent> components = originalComponent.getChildren();

            for (HtmlComponent component : components) {
                if (component instanceof HtmlContainer) {

                    HtmlTextInput field = (HtmlTextInput) component.getChild(new Predicate() {

                        @Override
                        public boolean evaluate(Object field) {
                            return (field instanceof HtmlTextInput);
                        }
                    });

                    ((HtmlContainer) component).addChild(getCalendarScript(field.getName()));

                }
            }

            return originalComponent;
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
