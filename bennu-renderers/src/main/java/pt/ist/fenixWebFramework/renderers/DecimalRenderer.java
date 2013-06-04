package pt.ist.fenixWebFramework.renderers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * This renderer provides a generic presentation for a decimal number. The
 * number is formatted according to the format property
 * 
 * @author naat
 * @author jdnf
 */
public class DecimalRenderer extends OutputRenderer {

    private String format;

    private String negativeStyle;

    private String positiveStyle;

    private char decimalSeparator;

    private char groupingSeparator;

    private String currencySymbol;

    private static final String DEFAULT_FORMAT = "######0.00";

    public DecimalRenderer() {
        setFormat(DEFAULT_FORMAT);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormat(getFormat()).getDecimalFormatSymbols();
        setDecimalSeparator(decimalFormatSymbols.getDecimalSeparator());
        setGroupingSeparator(decimalFormatSymbols.getGroupingSeparator());
        setCurrencySymbol(decimalFormatSymbols.getCurrencySymbol());
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                HtmlText htmlText = new HtmlText(getDecimalFormat().format(object));
                if (object != null && ((Number) object).doubleValue() < 0) {
                    setStyle(getNegativeStyle());
                } else {
                    setStyle(getPositiveStyle());
                }
                return htmlText;
            }

            private DecimalFormat getDecimalFormat() {
                DecimalFormat decimalFormat = new DecimalFormat(getFormat());
                DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
                decimalFormatSymbols.setDecimalSeparator(getDecimalSeparator());
                decimalFormatSymbols.setMonetaryDecimalSeparator(decimalFormatSymbols.getDecimalSeparator());
                decimalFormatSymbols.setGroupingSeparator(getGroupingSeparator());
                decimalFormatSymbols.setCurrencySymbol(getCurrencySymbol());
                decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
                return decimalFormat;
            }

        };
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getNegativeStyle() {
        return negativeStyle;
    }

    public void setNegativeStyle(String negativeStyle) {
        this.negativeStyle = negativeStyle;
    }

    public String getPositiveStyle() {
        return positiveStyle;
    }

    public void setPositiveStyle(String positiveStyle) {
        this.positiveStyle = positiveStyle;
    }

    public char getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(char decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public char getGroupingSeparator() {
        return groupingSeparator;
    }

    public void setGroupingSeparator(char groupingSeparator) {
        this.groupingSeparator = groupingSeparator;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

}
