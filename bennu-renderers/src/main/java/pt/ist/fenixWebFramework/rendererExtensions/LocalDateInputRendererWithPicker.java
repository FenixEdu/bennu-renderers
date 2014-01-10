package pt.ist.fenixWebFramework.rendererExtensions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalDate;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class LocalDateInputRendererWithPicker extends DateInputRendererWithPicker {

    public LocalDateInputRendererWithPicker() {
        super();

    }

    @Override
    public HtmlComponent render(Object object, Class type) {
        Date date = object == null ? null : ((LocalDate) object).toDateMidnight().toDate();
        return super.render(date, Date.class);
    }

    @Override
    protected Converter getDateConverter(SimpleDateFormat dateFormat) {
        final Converter dateConverter = super.getDateConverter(dateFormat);

        return new Converter() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object convert(Class type, Object value) {
                Date date = (Date) dateConverter.convert(type, value);

                return date == null ? null : new LocalDate(date);
            }

        };
    }

    @Override
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

}
