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
/**
 * 
 */
package pt.ist.fenixWebFramework.rendererExtensions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.fenixedu.commons.i18n.I18N;
import org.joda.time.Interval;

import pt.ist.fenixWebFramework.rendererExtensions.DateTimeInputRenderer.DateTimeConverter;
import pt.ist.fenixWebFramework.renderers.InputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlParagraphContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlController;
import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.converters.DateConverter;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;

/**
 * Input Renderer for the Joda Interval class.
 * 
 * @author Joao Carvalho (joao.pedro.carvalho@ist.utl.pt)
 * 
 */
@SuppressWarnings("rawtypes")
public class IntervalAsDatesInputRenderer extends InputRenderer {

    private String startLabel;
    private String endLabel;

    private boolean multiLine = true;

    public boolean isMultiLine() {
        return multiLine;
    }

    public void setMultiLine(boolean multiLine) {
        this.multiLine = multiLine;
    }

    public String getStartLabel() {
        return startLabel;
    }

    public void setStartLabel(String startLabel) {
        this.startLabel = startLabel;
    }

    public String getEndLabel() {
        return endLabel;
    }

    public void setEndLabel(String endLabel) {
        this.endLabel = endLabel;
    }

    // Format in which the date will be introduced/presented
    private String dateFormat;

    // Maximum size for the introduced date
    private String dateSize;
    private Integer dateMaxLength = 10;

    public String getDateFormat() {
        return this.dateFormat == null ? DateConverter.DEFAULT_FORMAT : dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Integer getDateMaxLength() {
        return this.dateMaxLength;
    }

    public void setDateMaxLength(Integer dateMaxLength) {
        this.dateMaxLength = dateMaxLength;
    }

    public String getDateSize() {
        return this.dateSize;
    }

    public void setDateSize(String dateSize) {
        this.dateSize = dateSize;
    }

    protected Locale getLocale() {
        return I18N.getLocale();
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new IntervalLayout(new SimpleDateFormat(getDateFormat(), getLocale()), startLabel, endLabel);
    }

    /*
     * Layout for the renderer
     */

    public class IntervalLayout extends Layout {
        private final DateFormat dateFormat;
        private final String startLabel, endLabel;
        private final boolean multiLine = isMultiLine();

        public IntervalLayout(DateFormat simpleDateFormat, String startLabel, String endLabel) {
            this.dateFormat = simpleDateFormat;
            this.startLabel = startLabel;
            this.endLabel = endLabel;
        }

        private HtmlContainer getContainer() {
            return multiLine ? new HtmlParagraphContainer() : new HtmlInlineContainer();
        }

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            Interval interval = (Interval) object;

            MetaSlotKey key = (MetaSlotKey) getInputContext().getMetaObject().getKey();

            HtmlContainer parentContainer = new HtmlInlineContainer();

            // Hidden field that will contain the intermediate data between the
            // Controller and the Converter
            HtmlHiddenField hiddenField = new HtmlHiddenField();
            hiddenField.setTargetSlot(key);
            parentContainer.addChild(hiddenField);

            HtmlContainer startContainer = getContainer();

            if (this.startLabel != null) {
                startContainer.addChild(new HtmlText(this.startLabel));
            }

            HtmlTextInput startDateField = new HtmlTextInput();
            startDateField.setName(key.toString() + "_StartDate");
            startDateField.setSize(getDateSize());
            startDateField.setMaxLength(getDateMaxLength());
            startContainer.addChild(startDateField);

            startContainer.addChild(new HtmlText(getFormatLabel()));

            HtmlContainer endContainer = getContainer();

            if (this.endLabel != null) {
                endContainer.addChild(new HtmlText(this.endLabel));
            }

            HtmlTextInput endDateField = new HtmlTextInput();
            endDateField.setName(key.toString() + "_EndDate");
            endDateField.setSize(getDateSize());
            endDateField.setMaxLength(getDateMaxLength());
            endContainer.addChild(endDateField);

            endContainer.addChild(new HtmlText(getFormatLabel()));

            parentContainer.addChild(startContainer);

            parentContainer.addChild(endContainer);

            if (interval != null) {
                startDateField.setValue(dateFormat.format(interval.getStart().toDate()));
                endDateField.setValue(dateFormat.format(interval.getEnd().toDate()));
            }

            hiddenField.setConverter(new IntervalConverter());
            endDateField.setController(new IntervalController(hiddenField, dateFormat, startDateField, endDateField));

            return parentContainer;
        }

        protected String getFormatLabel() {
            return getDateFormat();
        }
    }

    /*
     * Takes the string representing the interval, and creates the corresponding
     * interval. The format is as defined in IntervalController
     */

    @SuppressWarnings("serial")
    public static class IntervalConverter extends Converter {

        public static final String INVALID = "invalid";

        @Override
        public Interval convert(Class type, Object value) {
            if (value == null || String.valueOf(value).length() == 0) {
                return null;
            }
            try {
                if (value.equals(INVALID)) {
                    throw new ConversionException("fenix.renderers.converter.interval.convert", true, value);
                }

                String[] fields = String.valueOf(value).split(";");

                return new Interval(Long.parseLong(fields[0]), Long.parseLong(fields[1]));
            } catch (Exception e) {
                throw new ConversionException("fenix.renderers.converter.interval.convert", e, true, value);
            }
        }
    }

    /*
     * Takes the start and end date, and puts the data in the hidden field, in
     * the format {startMillis};{endMillis}
     */

    @SuppressWarnings("serial")
    private class IntervalController extends HtmlController {

        private final HtmlHiddenField valueField;
        private final DateFormat dateFormat;
        private final HtmlTextInput startDateField;
        private final HtmlTextInput endDateField;

        public IntervalController(HtmlHiddenField hiddenField, DateFormat dateFormat, HtmlTextInput startDateField,
                HtmlTextInput endDateField) {
            this.valueField = hiddenField;
            this.dateFormat = dateFormat;
            this.startDateField = startDateField;
            this.endDateField = endDateField;
        }

        @Override
        public void execute(IViewState viewState) {
            final String startValue = startDateField.getValue();
            final String endValue = endDateField.getValue();
            if ((startValue == null || startValue.isEmpty()) && (endValue == null || endValue.isEmpty())) {
                this.valueField.setValue(null);
            } else {
                try {
                    Date startDate = dateFormat.parse(startValue);
                    Date endDate = dateFormat.parse(endValue);

                    String value = String.format("%s;%s", startDate.getTime(), endDate.getTime());
                    this.valueField.setValue(value);
                } catch (Exception e) {
                    this.valueField.setValue(DateTimeConverter.INVALID);
                }
            }
        }

    }

}
