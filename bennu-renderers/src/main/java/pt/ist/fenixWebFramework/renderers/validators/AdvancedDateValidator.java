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
package pt.ist.fenixWebFramework.renderers.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.base.Predicate;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
public class AdvancedDateValidator extends DateValidator {

    private String validationPeriod;

    public AdvancedDateValidator() {
        super();
    }

    public AdvancedDateValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);
    }

    public AdvancedDateValidator(HtmlChainValidator htmlChainValidator, String dateFormat) {
        super(htmlChainValidator, dateFormat);
    }

    @Override
    public void performValidation() {
        super.performValidation();

        if (isValid()) {
            try {
                DateTime dateTime =
                        new DateTime(new SimpleDateFormat(getDateFormat()).parse(getComponent().getValue()).getTime());
                setValid(getValidationPeriodType().evaluateDate(dateTime));
            } catch (ParseException e) {
                setValid(false);
                e.printStackTrace();
            }
        }

    }

    public String getValidationPeriod() {
        return validationPeriod;
    }

    public void setValidationPeriod(String validationPeriod) {
        this.validationPeriod = validationPeriod;
        setMessage("renderers.validator.advancedDate." + getValidationPeriod());
    }

    public ValidationPeriodType getValidationPeriodType() {
        if (this.validationPeriod != null) {
            return ValidationPeriodType.valueOf(getValidationPeriod().toUpperCase());
        }
        return null;
    }

    private static Predicate<DateTime> pastPredicate = new Predicate<DateTime>() {
        @Override
        public boolean apply(DateTime dateTime) {
            return dateTime.isBeforeNow();
        }
    };

    private static Predicate<DateTime> pastOrTodayPredicate = new Predicate<DateTime>() {
        @Override
        public boolean apply(DateTime dateTime) {
            return dateTime.isBeforeNow() || dateTime.toLocalDate().isEqual(new LocalDate());
        }
    };

    private static Predicate<DateTime> futurePredicate = new Predicate<DateTime>() {
        @Override
        public boolean apply(DateTime dateTime) {
            return dateTime.isAfterNow();
        }
    };

    private enum ValidationPeriodType {

        PAST(pastPredicate), PASTORTODAY(pastOrTodayPredicate), FUTURE(futurePredicate);

        private Predicate<DateTime> predicate;

        private ValidationPeriodType(Predicate<DateTime> predicate) {
            this.predicate = predicate;
        }

        protected boolean evaluateDate(DateTime dateTime) {
            return this.predicate.apply(dateTime);
        }

    }

}
