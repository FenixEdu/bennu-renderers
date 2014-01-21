package pt.ist.fenixWebFramework.servlets.filters.contentRewrite;

import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import com.google.common.hash.Hashing;

public final class GenericChecksumRewriter {

    public static final String CHECKSUM_ATTRIBUTE_NAME = "_request_checksum_";

    public static final String NO_CHECKSUM_PREFIX = "<!-- NO_CHECKSUM -->";

    private static final int LENGTH_OF_NO_CHECKSUM_PREFIX = NO_CHECKSUM_PREFIX.length();

    private static final char[] OPEN_A = "<a ".toCharArray();
    private static final char[] OPEN_FORM = "<form ".toCharArray();
    private static final char[] OPEN_IMG = "<img ".toCharArray();
    private static final char[] OPEN_AREA = "<area ".toCharArray();

    private static final char[] CLOSE = ">".toCharArray();
    private static final char[] PREFIX_JAVASCRIPT = "javascript:".toCharArray();
    private static final char[] PREFIX_MAILTO = "mailto:".toCharArray();
    private static final char[] PREFIX_HTTP = "http://".toCharArray();
    private static final char[] PREFIX_HTTPS = "https://".toCharArray();
    private static final char[] CARDINAL = "#".toCharArray();
    private static final char[] QUESTION_MARK = "?".toCharArray();

    private final String sessionSecret;

    public GenericChecksumRewriter(HttpSession session) {
        this.sessionSecret = RenderersSessionSecret.computeSecretFromSession(session);
    }

    private String calculateChecksum(final StringBuilder source, final int start, final int end) {
        return calculateChecksum(source.substring(start, end), sessionSecret);
    }

    private static boolean isRelevantPart(final String part) {
        return part.length() > 0 && !part.startsWith(CHECKSUM_ATTRIBUTE_NAME) && !part.startsWith("page=")
                && !part.startsWith("org.apache.struts.action.LOCALE") && !part.startsWith("javax.servlet.request.")
                && !part.startsWith("ok");
    }

    private static String calculateChecksum(final TreeSet<String> strings, String sessionSecret) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String string : strings) {
            stringBuilder.append(string);
        }

        if (sessionSecret != null) {
            stringBuilder.append(sessionSecret);
        }
        final String checksum = new String(Hashing.sha1().hashBytes(stringBuilder.toString().getBytes()).toString());
        // System.out.println("Generating checksum for: " +
        // stringBuilder.toString() + " --> " + checksum);
        return checksum;
    }

    public static String calculateChecksum(final String requestString, HttpSession session) {
        return calculateChecksum(requestString, RenderersSessionSecret.computeSecretFromSession(session));
    }

    private static String calculateChecksum(final String requestString, String sessionSecret) {
        final int indexLastCardinal = requestString.lastIndexOf('#');
        final String string = indexLastCardinal >= 0 ? requestString.substring(0, indexLastCardinal) : requestString;
        final String[] parts = string.split("\\?|&amp;|&");

        final TreeSet<String> strings = new TreeSet<String>();
        for (final String part : parts) {
            if (isRelevantPart(part)) {
                final int indexOfEquals = part.indexOf('=');
                if (indexOfEquals >= 0) {
                    strings.add(part.substring(0, indexOfEquals));
                    strings.add(part.substring(indexOfEquals + 1, part.length()));
                } else {
                    strings.add(part);
                }
            }
        }
        return calculateChecksum(strings, sessionSecret);
    }

    public static String injectChecksumInUrl(final String contextPath, final String url, HttpSession session) {
        String checksum = CHECKSUM_ATTRIBUTE_NAME + "=" + calculateChecksum(contextPath + url, session);
        return url + "&" + checksum;
    }

    public StringBuilder rewrite(StringBuilder source) {
        int iOffset = 0;

        final StringBuilder response = new StringBuilder();

        while (true) {

            final int indexOfAopen = indexOf(source, OPEN_A, iOffset);
            final int indexOfFormOpen = indexOf(source, OPEN_FORM, iOffset);
            final int indexOfImgOpen = indexOf(source, OPEN_IMG, iOffset);
            final int indexOfAreaOpen = indexOf(source, OPEN_AREA, iOffset);
            if (indexOfAopen >= 0 && (indexOfFormOpen < 0 || indexOfAopen < indexOfFormOpen)
                    && (indexOfImgOpen < 0 || indexOfAopen < indexOfImgOpen)
                    && (indexOfAreaOpen < 0 || indexOfAopen < indexOfAreaOpen)) {
                if (!isPrefixed(source, indexOfAopen)) {
                    final int indexOfAclose = indexOf(source, CLOSE, indexOfAopen);
                    if (indexOfAclose >= 0) {
                        final int indexOfHrefBodyStart = findHrefBodyStart(source, indexOfAopen, indexOfAclose);
                        if (indexOfHrefBodyStart >= 0) {
                            final char hrefBodyStartChar = source.charAt(indexOfHrefBodyStart - 1);
                            final int indexOfHrefBodyEnd = findHrefBodyEnd(source, indexOfHrefBodyStart, hrefBodyStartChar);
                            if (indexOfHrefBodyEnd >= 0) {

                                int indexOfJavaScript = indexOf(source, PREFIX_JAVASCRIPT, indexOfHrefBodyStart);
                                int indexOfMailto = indexOf(source, PREFIX_MAILTO, indexOfHrefBodyStart);
                                int indexOfHttp = indexOf(source, PREFIX_HTTP, indexOfHrefBodyStart);
                                int indexOfHttps = indexOf(source, PREFIX_HTTPS, indexOfHrefBodyStart);
                                if ((indexOfJavaScript < 0 || indexOfJavaScript > indexOfHrefBodyEnd)
                                        && (indexOfMailto < 0 || indexOfMailto > indexOfHrefBodyEnd)
                                        && (indexOfHttp < 0 || indexOfHttp > indexOfHrefBodyEnd)
                                        && (indexOfHttps < 0 || indexOfHttps > indexOfHrefBodyEnd)) {

                                    final int indexOfCardinal = indexOf(source, CARDINAL, indexOfHrefBodyStart);
                                    boolean hasCardinal =
                                            indexOfCardinal > indexOfHrefBodyStart && indexOfCardinal < indexOfHrefBodyEnd;
                                    if (hasCardinal) {
                                        response.append(source, iOffset, indexOfCardinal);
                                    } else {
                                        response.append(source, iOffset, indexOfHrefBodyEnd);
                                    }

                                    final String checksum = calculateChecksum(source, indexOfHrefBodyStart, indexOfHrefBodyEnd);
                                    final int indexOfQmark = indexOf(source, QUESTION_MARK, indexOfHrefBodyStart);
                                    if (indexOfQmark == -1 || indexOfQmark > indexOfHrefBodyEnd) {
                                        response.append('?');
                                    } else {
                                        response.append("&amp;");
                                    }
                                    response.append(CHECKSUM_ATTRIBUTE_NAME);
                                    response.append("=");
                                    response.append(checksum);

                                    if (hasCardinal) {
                                        response.append(source, indexOfCardinal, indexOfHrefBodyEnd);
                                    }

                                    final int nextChar = indexOfAclose + 1;
                                    response.append(source, indexOfHrefBodyEnd, nextChar);
                                    // rewrite(response, source, nextChar);
                                    // return;
                                    iOffset = nextChar;
                                    continue;
                                } else {
                                    final int nextIndex;

                                    if (indexOfJavaScript < 0) {
                                        indexOfJavaScript = Integer.MAX_VALUE;
                                    }
                                    if (indexOfMailto < 0) {
                                        indexOfMailto = Integer.MAX_VALUE;
                                    }
                                    if (indexOfHttp < 0) {
                                        indexOfHttp = Integer.MAX_VALUE;
                                    }
                                    if (indexOfHttps < 0) {
                                        indexOfHttps = Integer.MAX_VALUE;
                                    }

                                    nextIndex = min(indexOfJavaScript, indexOfMailto, indexOfHttp, indexOfHttps);

                                    response.append(source, iOffset, nextIndex);
                                    iOffset = nextIndex;
                                    continue;
                                }
                            }
                        } else {
                            iOffset = continueToNextToken(response, source, iOffset, indexOfAopen);
                            continue;
                        }
                    }
                } else {
                    iOffset = continueToNextToken(response, source, iOffset, indexOfAopen);
                    continue;
                }
            } else if (indexOfFormOpen >= 0 && (indexOfImgOpen < 0 || indexOfFormOpen < indexOfImgOpen)
                    && (indexOfAreaOpen < 0 || indexOfFormOpen < indexOfAreaOpen)) {
                if (!isPrefixed(source, indexOfFormOpen)) {
                    final int indexOfFormClose = indexOf(source, CLOSE, indexOfFormOpen);
                    if (indexOfFormClose >= 0) {
                        final int indexOfFormActionBodyStart = findFormActionBodyStart(source, indexOfFormOpen, indexOfFormClose);
                        if (indexOfFormActionBodyStart >= 0) {
                            final int indexOfFormActionBodyEnd = findFormActionBodyEnd(source, indexOfFormActionBodyStart);
                            if (indexOfFormActionBodyEnd >= 0) {
                                final int nextChar = indexOfFormClose + 1;
                                response.append(source, iOffset, nextChar);
                                final String checksum =
                                        calculateChecksum(source, indexOfFormActionBodyStart, indexOfFormActionBodyEnd);
                                response.append("<input type=\"hidden\" name=\"");
                                response.append(CHECKSUM_ATTRIBUTE_NAME);
                                response.append("\" value=\"");
                                response.append(checksum);
                                response.append("\"/>");
                                // rewrite(response, source, nextChar);
                                // return;
                                iOffset = nextChar;
                                continue;
                            }
                        }
                    }
                } else {
                    iOffset = continueToNextToken(response, source, iOffset, indexOfFormOpen);
                    continue;
                }
            } else if (indexOfImgOpen >= 0 && (indexOfAreaOpen < 0 || indexOfImgOpen < indexOfAreaOpen)) {
                if (!isPrefixed(source, indexOfImgOpen)) {
                    final int indexOfImgClose = indexOf(source, CLOSE, indexOfImgOpen);
                    if (indexOfImgClose >= 0) {
                        final int indexOfSrcBodyStart = findSrcBodyStart(source, indexOfImgOpen, indexOfImgClose);
                        if (indexOfSrcBodyStart >= 0) {
                            final int indexOfSrcBodyEnd = findSrcBodyEnd(source, indexOfSrcBodyStart);
                            if (indexOfSrcBodyEnd >= 0) {
                                response.append(source, iOffset, indexOfSrcBodyEnd);

                                final String checksum = calculateChecksum(source, indexOfSrcBodyStart, indexOfSrcBodyEnd);
                                final int indexOfQmark = indexOf(source, QUESTION_MARK, indexOfSrcBodyStart);
                                if (indexOfQmark == -1 || indexOfQmark > indexOfSrcBodyEnd) {
                                    response.append('?');
                                } else {
                                    response.append("&amp;");
                                }
                                response.append(CHECKSUM_ATTRIBUTE_NAME);
                                response.append("=");
                                response.append(checksum);

                                final int nextChar = indexOfImgClose + 1;
                                response.append(source, indexOfSrcBodyEnd, nextChar);
                                // rewrite(response, source, nextChar);
                                // return;
                                iOffset = nextChar;
                                continue;
                            }
                        }
                    }
                } else {
                    iOffset = continueToNextToken(response, source, iOffset, indexOfImgOpen);
                    continue;
                }
            } else if (indexOfAreaOpen >= 0) {
                if (!isPrefixed(source, indexOfAreaOpen)) {
                    final int indexOfAreaClose = indexOf(source, CLOSE, indexOfAreaOpen);
                    if (indexOfAreaClose >= 0) {
                        final int indexOfHrefBodyStart = findHrefBodyStart(source, indexOfAreaOpen, indexOfAreaClose);
                        if (indexOfHrefBodyStart >= 0) {
                            final char hrefBodyStartChar = source.charAt(indexOfHrefBodyStart - 1);
                            final int indexOfHrefBodyEnd = findHrefBodyEnd(source, indexOfHrefBodyStart, hrefBodyStartChar);
                            if (indexOfHrefBodyEnd >= 0) {
                                final int indexOfCardinal = indexOf(source, CARDINAL, indexOfHrefBodyStart);
                                boolean hasCardinal =
                                        indexOfCardinal > indexOfHrefBodyStart && indexOfCardinal < indexOfHrefBodyEnd;
                                if (hasCardinal) {
                                    response.append(source, iOffset, indexOfCardinal);
                                } else {
                                    response.append(source, iOffset, indexOfHrefBodyEnd);
                                }

                                final String checksum = calculateChecksum(source, indexOfHrefBodyStart, indexOfHrefBodyEnd);
                                final int indexOfQmark = indexOf(source, QUESTION_MARK, indexOfHrefBodyStart);
                                if (indexOfQmark == -1 || indexOfQmark > indexOfHrefBodyEnd) {
                                    response.append('?');
                                } else {
                                    response.append("&amp;");
                                }
                                response.append(CHECKSUM_ATTRIBUTE_NAME);
                                response.append("=");
                                response.append(checksum);

                                if (hasCardinal) {
                                    response.append(source, indexOfCardinal, indexOfHrefBodyEnd);
                                }

                                final int nextChar = indexOfAreaClose + 1;
                                response.append(source, indexOfHrefBodyEnd, nextChar);
                                // rewrite(response, source, nextChar);
                                // return;
                                iOffset = nextChar;
                                continue;
                            }
                        }
                    }
                } else {
                    iOffset = continueToNextToken(response, source, iOffset, indexOfAreaOpen);
                    continue;
                }
            }
            response.append(source, iOffset, source.length());
            break;
        }

        return response;
    }

    private int min(final int... indexs) {
        int result = Integer.MAX_VALUE;
        for (int i : indexs) {
            result = Math.min(result, i);
        }
        return result;
    }

    private boolean isPrefixed(final StringBuilder source, final int indexOfTagOpen) {
        return (indexOfTagOpen >= LENGTH_OF_NO_CHECKSUM_PREFIX && match(source, indexOfTagOpen - LENGTH_OF_NO_CHECKSUM_PREFIX,
                indexOfTagOpen, NO_CHECKSUM_PREFIX));
    }

    private boolean match(final StringBuilder source, final int iStart, int iEnd, final String string) {
        if (iEnd - iStart != string.length()) {
            return false;
        }
        for (int i = 0; i < string.length(); i++) {
            if (source.charAt(iStart + i) != string.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private int continueToNextToken(final StringBuilder response, final StringBuilder source, final int iOffset,
            final int indexOfTag) {
        final int nextOffset = indexOfTag + 1;
        response.append(source, iOffset, nextOffset);
        return nextOffset;
    }

    private static int indexOf(final StringBuilder source, final char[] target, final int fromIndex) {
        // return source.indexOf(target, fromIndex);
        return indexOf(source, 0, source.length(), target, 0, target.length, fromIndex);
    }

    private static int indexOf(final StringBuilder source, final int sourceOffset, final int sourceCount, final char[] target,
            final int targetOffset, final int targetCount, int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source.charAt(i) != first) {
                while (++i <= max && source.charAt(i) != first) {
                    ;
                }
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source.charAt(j) == target[k]; j++, k++) {
                    ;
                }

                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }

    private int findFormActionBodyEnd(final StringBuilder source, final int offset) {
        int i = offset;
        for (char c = source.charAt(i); c != '"' && c != '\'' && c != ' ' && c != '>'; c = source.charAt(i)) {
            if (++i == source.length()) {
                return -1;
            }
        }
        return i;
    }

    private int findFormActionBodyStart(final StringBuilder source, final int offset, final int limit) {
        final int indexOfHref = source.indexOf("action=", offset);
        if (indexOfHref >= limit) {
            return -1;
        }
        final int nextChar = indexOfHref + 7;
        return source.charAt(nextChar) == '"' || source.charAt(nextChar) == '\'' ? nextChar + 1 : nextChar;
    }

    private int findHrefBodyEnd(final StringBuilder source, final int offset, final char hrefBodyStartChar) {
        int i = offset;
        if (hrefBodyStartChar == '=') {
            for (char c = source.charAt(i); c != ' ' && c != '>'; c = source.charAt(i)) {
                if (++i == source.length()) {
                    return -1;
                }
            }
        } else {
            for (char c = source.charAt(i); c != hrefBodyStartChar; c = source.charAt(i)) {
                if (++i == source.length()) {
                    return -1;
                }
            }
        }
        return i;
    }

    private int findSrcBodyEnd(final StringBuilder source, final int offset) {
        int i = offset;
        char delimiter = source.charAt(offset - 1);
        if (delimiter == '"' || delimiter == '\'') {
            for (char c = source.charAt(i); c != delimiter; c = source.charAt(i)) {
                if (++i == source.length()) {
                    return -1;
                }
            }
        } else {
            for (char c = source.charAt(i); c != ' ' && c != '>'; c = source.charAt(i)) {
                if (++i == source.length()) {
                    return -1;
                }
            }
        }
        return i;
    }

    private int findHrefBodyStart(final StringBuilder source, final int offset, int limit) {
        final int indexOfHref = source.indexOf("href=", offset);
        if (indexOfHref >= limit) {
            return -1;
        }
        final int nextChar = indexOfHref + 5;
        return source.charAt(nextChar) == '"' || source.charAt(nextChar) == '\'' ? nextChar + 1 : nextChar;
    }

    private int findSrcBodyStart(final StringBuilder source, final int offset, int limit) {
        final int indexOfHref = source.indexOf("src=", offset);
        if (indexOfHref >= limit) {
            return -1;
        }
        final int nextChar = indexOfHref + 5;
        return source.charAt(nextChar) == '"' || source.charAt(nextChar) == '\'' ? nextChar + 1 : nextChar;
    }

}
