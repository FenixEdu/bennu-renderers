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
package pt.ist.fenixWebFramework.renderers.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.common.base.Strings;

public class DateValidator extends HtmlValidator {

    private String dateFormat;

    /**
     * Required constructor.
     */
    public DateValidator() {
        super();
        setDateFormat("dd/MM/yyyy");
        setKey(true);
    }

    public DateValidator(HtmlChainValidator htmlChainValidator) {
        this(htmlChainValidator, "dd/MM/yyyy");
    }

    public DateValidator(HtmlChainValidator htmlChainValidator, String dateFormat) {
        super(htmlChainValidator);

        setDateFormat(dateFormat);
        setKey(true);

    }

    public String getDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public void performValidation() {

        String text = getComponent().getValue();

        if (!Strings.isNullOrEmpty(text)) {
            setValid(isValid(text, getDateFormat()));
        }

        if (!isValid()) {
            setMessage("renderers.validator.date");
        }
    }

    public boolean isValid(String value, String datePattern) {
        if (value == null || datePattern == null || datePattern.length() <= 0) {
            return false;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        formatter.setLenient(false);

        try {
            formatter.parse(value);
        } catch (ParseException e) {
            return false;
        }

        return datePattern.length() == value.length();
    }
}
