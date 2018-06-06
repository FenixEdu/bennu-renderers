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
package pt.ist.fenixWebFramework.rendererExtensions.htmlEditor;

import com.google.common.base.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class JsoupSafeHtmlConverter extends Converter {

    private static final String[] MATHJAX_TAGS = { "abs", "and", "annotation", "annotation-xml", "apply", "approx", "arccos",
            "arccosh", "arccot", "arccoth", "arccsc", "arccsch", "arcsec", "arcsech", "arcsin", "arcsinh", "arctan", "arctanh",
            "arg", "bvar", "card", "cartesianproduct", "ceiling", "ci", "cn", "codomain", "complexes", "compose", "condition",
            "conjugate", "cos", "cosh", "cot", "coth", "csc", "csch", "csymbol", "curl", "declare", "degree", "determinant",
            "diff", "divergence", "divide", "domain", "domainofapplication", "emptyset", "encoding", "eq", "equivalent",
            "eulergamma", "exists", "exp", "exponentiale", "factorial", "factorof", "false", "floor", "fn", "forall", "function",
            "gcd", "geq", "grad", "gt", "ident", "image", "imaginary", "imaginaryi", "implies", "in", "infinity", "int",
            "integers", "intersect", "interval", "inverse", "lambda", "laplacian", "lcm", "leq", "limit", "list", "ln", "log",
            "logbase", "lowlimit", "lt", "m:apply", "m:mrow", "maction", "malign", "maligngroup", "malignmark", "malignscope",
            "math", "matrix", "matrixrow", "max", "mean", "median", "menclose", "merror", "mfenced", "mfrac", "mfraction",
            "mglyph", "mi", "min", "minus", "mlabeledtr", "mmultiscripts", "mn", "mo", "mode", "moment", "momentabout", "mover",
            "mpadded", "mphantom", "mprescripts", "mroot", "mrow", "ms", "mspace", "msqrt", "mstyle", "msub", "msubsup", "msup",
            "mtable", "mtd", "mtext", "mtr", "munder", "munderover", "naturalnumbers", "neq", "none", "not", "notanumber",
            "notin", "notprsubset", "notsubset", "or", "otherwise", "outerproduct", "partialdiff", "pi", "piece", "piecewice",
            "piecewise", "plus", "power", "primes", "product", "prsubset", "quotient", "rationals", "real", "reals", "reln",
            "rem", "root", "scalarproduct", "sdev", "sec", "sech", "selector", "semantics", "sep", "set", "setdiff", "sin",
            "sinh", "subset", "sum", "tan", "tanh", "tendsto", "times", "transpose", "true", "union", "uplimit", "variance",
            "vector", "vectorproduct", "xor" };

    private static final String[] MATHJAX_ATTRS = { "accent", "accentunder", "actiontype", "align", "alignmentscope", "alt",
            "axis", "background", "background-color", "base", "bevelled", "class", "close", "closure", "color", "columnalign",
            "columnalignment", "columnlines", "columnspacing", "columnspan", "columnwidth", "css-color-name", "css-fontfamily",
            "definitionURL", "denomalign", "depth", "display", "displaystyle", "edge", "encoding", "equalcolumns", "equalrows",
            "fence", "font-family", "fontfamily", "fontsize", "fontslant", "fontstyle", "fontweight", "form", "frame",
            "framespacing", "groupalign", "h-unit", "height", "href", "html-color-name", "id", "index", "integer", "largeop",
            "linebreak", "linethickness", "lquote", "lspace", "macros", "mathbackground", "mathcolor", "mathfamily", "mathsize",
            "mathslant", "mathvariant", "mathweight", "maxsize", "minlabelspacing", "minsize", "mode", "monospaced",
            "movablelimits", "movablescripts", "my:background", "my:color", "namedspace", "nargs", "notation", "numalign",
            "number", "occurrence", "open", "order", "other", "rowalign", "rowlines", "rowspacing", "rowspan", "rquote",
            "rspace", "s:schemaLocation", "schemaLocation", "scope", "scriptlevel", "scriptminsize", "scriptsizemultiplier",
            "selection", "separator", "separators", "side", "stretchy", "style", "subscriptshift", "superscriptshift",
            "symmetric", "type", "v-unit", "width", "xlink:href", "xml:space", "xmlns", "xref", "xsi:schemaLocation" };

    private static final String[] TABLE_ATTRS = { "align", "bgcolor", "border", "cellpadding", "cellspacing", "frame", "rules",
            "summary", "width" };
    private static final String[] TBODY_TR_ATTRS = { "align", "bgcolor", "char", "charoff", "valign" };
    private static final String[] IFRAME_ATTRS = { "align", "frameborder", "height", "width", "style", "src", "name",
            "marginheight", "marginwidth" };
    private static final String[] TH_TD_ATTRS = { "abbr", "align", "axis", "bgcolor", "char", "charoff", "colspan", "height",
            "nowrap", "rowspan", "scope", "valign", "width" };

    private static final String[] URL_SCHEMES = new String[] { "http", "https" };
    private static final String[] URL_VALID_AUTHORITIES_REGEXPS = new String[] { ".*google.com" };

    private static Whitelist whitelistSimple;

    private static Whitelist whiteListMathJax;

    static {
        whitelistSimple = Whitelist.relaxed().addTags("span").addAttributes(":all", "style")
        .addAttributes("iframe", IFRAME_ATTRS).addProtocols("iframe", "src", "http", "https")
        .addAttributes("table", TABLE_ATTRS)
        .addAttributes("tr", TBODY_TR_ATTRS).addAttributes("tbody", TBODY_TR_ATTRS)
        .addAttributes("th", TH_TD_ATTRS).addAttributes("td", TH_TD_ATTRS)
        .addProtocols("img", "src", "data")
        .preserveRelativeLinks(true);

        whiteListMathJax = whitelistSimple.addTags(MATHJAX_TAGS);

        for (String elem : MATHJAX_TAGS) {
            whiteListMathJax.addAttributes(elem, MATHJAX_ATTRS);
        }
    }

    private final Whitelist whitelist;

    public JsoupSafeHtmlConverter() {
        this(Boolean.TRUE);
    }

    public JsoupSafeHtmlConverter(final boolean mathJaxEnabled) {
        whitelist = mathJaxEnabled ? whiteListMathJax : whitelistSimple;
    }

    @Override
    public Object convert(Class type, Object value) {
        String htmlText = (String) value;

        if (Strings.isNullOrEmpty(htmlText)) {
            return null;
        }

        Document dirty = Jsoup.parseBodyFragment(htmlText);

        Cleaner cleaner = new Cleaner(whitelist);
        Document clean = cleaner.clean(dirty);
        cleanInvalidIframes(clean);
        clean.outputSettings().charset("ASCII");
        return clean.body().html();
    }

    private void cleanInvalidIframes(Document clean) {
        for (Element iframe : clean.getElementsByTag("iframe")) {
            String src = iframe.attr("src");
            if (!validUrl(src)) {
                iframe.remove();
            }
        }
    }

    private boolean validUrl(String src) {
        if (Strings.isNullOrEmpty(src)) {
            return false;
        }
        try {
            URL url = new URL(src);

            if (!Arrays.asList(URL_SCHEMES).contains(url.getProtocol())) {
                return false;
            }

            for (String authRegexp : URL_VALID_AUTHORITIES_REGEXPS) {
                if (!url.getAuthority().matches(authRegexp)) {
                    return false;
                }
            }

            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

}