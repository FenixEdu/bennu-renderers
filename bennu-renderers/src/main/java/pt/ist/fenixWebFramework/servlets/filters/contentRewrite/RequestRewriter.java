package pt.ist.fenixWebFramework.servlets.filters.contentRewrite;

import javax.servlet.http.HttpServletRequest;

public abstract class RequestRewriter {
    protected final HttpServletRequest httpServletRequest;

    public static final String HAS_CONTEXT_PREFIX_STRING = "HAS_CONTEXT";

    public static final String BLOCK_HAS_CONTEXT_STRING = "BLOCK_HAS_CONTEXT";

    public static final String BLOCK_END_HAS_CONTEXT_STRING = "END_BLOCK_HAS_CONTEXT";

    public static final String HAS_CONTEXT_PREFIX = "<!-- " + HAS_CONTEXT_PREFIX_STRING + " -->";

    public static final char[] BLOCK_HAS_CONTEXT_PREFIX = ("<!-- " + BLOCK_HAS_CONTEXT_STRING + " -->").toCharArray();

    public static final char[] END_BLOCK_HAS_CONTEXT_PREFIX = ("<!-- " + BLOCK_END_HAS_CONTEXT_STRING + " -->").toCharArray();

    protected static final char[] OPEN_A = "<a ".toCharArray();
    protected static final char[] OPEN_FORM = "<form ".toCharArray();
    protected static final char[] OPEN_IMG = "<img ".toCharArray();
    protected static final char[] OPEN_AREA = "<area ".toCharArray();

    protected static final int LENGTH_OF_HAS_CONTENT_PREFIX = HAS_CONTEXT_PREFIX.length();

    public final static String HAS_CONTEXT_PREFIX_NO_CHECKSUM_PREFIX = HAS_CONTEXT_PREFIX
            + GenericChecksumRewriter.NO_CHECKSUM_PREFIX;

    private static final int LENGTH_OF_HAS_CONTEXT_PREFIX_NO_CHECKSUM_PREFIX = HAS_CONTEXT_PREFIX_NO_CHECKSUM_PREFIX.length();

    protected final String contextPath;

    public RequestRewriter(final HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
        contextPath = getContextPath(httpServletRequest);
    }

    protected static final char[] CLOSE = ">".toCharArray();
    protected static final char[] PREFIX_JAVASCRIPT = "javascript:".toCharArray();
    protected static final char[] PREFIX_MAILTO = "mailto:".toCharArray();
    protected static final char[] PREFIX_HTTP = "http://".toCharArray();
    protected static final char[] PREFIX_HTTPS = "https://".toCharArray();
    protected static final char[] CARDINAL = "#".toCharArray();
    protected static final char[] QUESTION_MARK = "?".toCharArray();

    protected abstract String getContextPath(final HttpServletRequest httpServletRequest);

    protected abstract String getContextAttributeName();

    public StringBuilder rewrite(final StringBuilder source) {
        int iOffset = 0;
        if (contextPath == null || contextPath.length() == 0) {
            return source;
        }

        final StringBuilder response = new StringBuilder();

        while (true) {
            final int indexOfAopen = indexOf(source, OPEN_A, iOffset);
            final int indexOfFormOpen = indexOf(source, OPEN_FORM, iOffset);
            final int indexOfImgOpen = indexOf(source, OPEN_IMG, iOffset);
            final int indexOfAreaOpen = indexOf(source, OPEN_AREA, iOffset);
            final int indexOfBlockHasContextopen = indexOf(source, BLOCK_HAS_CONTEXT_PREFIX, iOffset);

            if (firstIsMinValue(indexOfAopen, indexOfFormOpen, indexOfImgOpen, indexOfAreaOpen, indexOfBlockHasContextopen)) {
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

                                    final int indexOfQmark = indexOf(source, QUESTION_MARK, indexOfHrefBodyStart);
                                    if (indexOfQmark == -1 || indexOfQmark > indexOfHrefBodyEnd) {
                                        response.append('?');
                                    } else {
                                        response.append("&amp;");
                                    }
                                    appendContextParameter(response);

                                    if (hasCardinal) {
                                        response.append(source, indexOfCardinal, indexOfHrefBodyEnd);
                                    }

                                    iOffset = continueToNextToken(response, source, indexOfHrefBodyEnd, indexOfAclose);
                                    continue;
                                }
                            }
                        }
                    }
                }
                iOffset = continueToNextToken(response, source, iOffset, indexOfAopen);
                continue;
            } else if (firstIsMinValue(indexOfFormOpen, indexOfAopen, indexOfImgOpen, indexOfAreaOpen, indexOfBlockHasContextopen)) {
                if (!isPrefixed(source, indexOfFormOpen)) {
                    final int indexOfFormClose = indexOf(source, CLOSE, indexOfFormOpen);
                    if (indexOfFormClose >= 0) {
                        final int indexOfFormActionBodyStart = findFormActionBodyStart(source, indexOfFormOpen, indexOfFormClose);
                        if (indexOfFormActionBodyStart >= 0) {
                            final int indexOfFormActionBodyEnd = findFormActionBodyEnd(source, indexOfFormActionBodyStart);
                            if (indexOfFormActionBodyEnd >= 0) {
                                iOffset = continueToNextToken(response, source, iOffset, indexOfFormClose);
                                appendContextAttribute(response);
                                continue;
                            }
                        }
                    }
                }
                iOffset = continueToNextToken(response, source, iOffset, indexOfFormOpen);
                continue;
            } else if (firstIsMinValue(indexOfImgOpen, indexOfAopen, indexOfFormOpen, indexOfAreaOpen, indexOfBlockHasContextopen)) {
                if (!isPrefixed(source, indexOfImgOpen)) {
                    final int indexOfImgClose = indexOf(source, CLOSE, indexOfImgOpen);
                    if (indexOfImgClose >= 0) {
                        final int indexOfSrcBodyStart = findSrcBodyStart(source, indexOfImgOpen, indexOfImgClose);
                        if (indexOfSrcBodyStart >= 0) {
                            final int indexOfSrcBodyEnd = findSrcBodyEnd(source, indexOfSrcBodyStart);
                            if (indexOfSrcBodyEnd >= 0) {
                                response.append(source, iOffset, indexOfSrcBodyEnd);

                                final int indexOfQmark = indexOf(source, QUESTION_MARK, indexOfSrcBodyStart);
                                if (indexOfQmark == -1 || indexOfQmark > indexOfSrcBodyEnd) {
                                    response.append('?');
                                } else {
                                    response.append("&amp;");
                                }
                                appendContextParameter(response);

                                iOffset = continueToNextToken(response, source, indexOfSrcBodyEnd, indexOfImgClose);
                                continue;
                            }
                        }
                    }
                }
                iOffset = continueToNextToken(response, source, iOffset, indexOfImgOpen);
                continue;
            } else if (firstIsMinValue(indexOfAreaOpen, indexOfAopen, indexOfFormOpen, indexOfImgOpen, indexOfBlockHasContextopen)) {
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

                                final int indexOfQmark = indexOf(source, QUESTION_MARK, indexOfHrefBodyStart);
                                if (indexOfQmark == -1 || indexOfQmark > indexOfHrefBodyEnd) {
                                    response.append('?');
                                } else {
                                    response.append("&amp;");
                                }
                                appendContextParameter(response);

                                if (hasCardinal) {
                                    response.append(source, indexOfCardinal, indexOfHrefBodyEnd);
                                }

                                iOffset = continueToNextToken(response, source, indexOfHrefBodyEnd, indexOfAreaClose);
                                continue;
                            }
                        }
                    }
                }
                iOffset = continueToNextToken(response, source, iOffset, indexOfAreaOpen);
                continue;
            } else if (firstIsMinValue(indexOfBlockHasContextopen, indexOfAopen, indexOfFormOpen, indexOfImgOpen, indexOfAreaOpen)) {
                final int indexOfEndBlockHasContextOpen =
                        indexOf(source, END_BLOCK_HAS_CONTEXT_PREFIX, indexOfBlockHasContextopen);
                if (indexOfEndBlockHasContextOpen == -1) {
                    iOffset = indexOfBlockHasContextopen + BLOCK_HAS_CONTEXT_PREFIX.length;
                } else {
                    response.append(source, iOffset, indexOfEndBlockHasContextOpen);
                    iOffset = indexOfEndBlockHasContextOpen;
                }
                continue;
            } else {
                response.append(source, iOffset, source.length());
                break;
            }
        }

        return response;
    }

    private void appendContextParameter(final StringBuilder response) {
        response.append(getContextAttributeName());
        response.append("=");
        response.append(contextPath);
    }

    private void appendContextAttribute(final StringBuilder response) {
        response.append("<input type=\"hidden\" name=\"");
        response.append(getContextAttributeName());
        response.append("\" value=\"");
        response.append(contextPath);
        response.append("\"/>");
    }

    protected int continueToNextToken(final StringBuilder response, final StringBuilder source, final int iOffset,
            final int indexOfTag) {
        final int nextOffset = indexOfTag + 1;
        response.append(source, iOffset, nextOffset);
        return nextOffset;
    }

    protected boolean match(final StringBuilder source, final int iStart, int iEnd, final String string) {
        final int length = string.length();
        if (iEnd - iStart != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (source.charAt(iStart + i) != string.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    protected boolean firstIsMinValue(final int index, final int... indexes) {
        if (index >= 0) {
            for (final int otherIndex : indexes) {
                if (otherIndex >= 0 && otherIndex < index) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected boolean isPrefixed(final StringBuilder source, final int indexOfTagOpen) {
        return (indexOfTagOpen >= LENGTH_OF_HAS_CONTENT_PREFIX && match(source, indexOfTagOpen - LENGTH_OF_HAS_CONTENT_PREFIX,
                indexOfTagOpen, HAS_CONTEXT_PREFIX))
                || (indexOfTagOpen >= LENGTH_OF_HAS_CONTEXT_PREFIX_NO_CHECKSUM_PREFIX && match(source, indexOfTagOpen
                        - LENGTH_OF_HAS_CONTEXT_PREFIX_NO_CHECKSUM_PREFIX, indexOfTagOpen, HAS_CONTEXT_PREFIX_NO_CHECKSUM_PREFIX));
    }

    protected int findHrefBodyEnd(final StringBuilder source, final int offset, final char hrefBodyStartChar) {
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

    protected int findSrcBodyEnd(final StringBuilder source, final int offset) {
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

    protected int findHrefBodyStart(final StringBuilder source, final int offset, int limit) {
        final int indexOfHref = source.indexOf("href=", offset);
        if (indexOfHref >= limit) {
            return -1;
        }
        final int nextChar = indexOfHref + 5;
        return source.charAt(nextChar) == '"' || source.charAt(nextChar) == '\'' ? nextChar + 1 : nextChar;
    }

    protected int findSrcBodyStart(final StringBuilder source, final int offset, int limit) {
        final int indexOfHref = source.indexOf("src=", offset);
        if (indexOfHref >= limit) {
            return -1;
        }
        final int nextChar = indexOfHref + 5;
        return source.charAt(nextChar) == '"' || source.charAt(nextChar) == '\'' ? nextChar + 1 : nextChar;
    }

    protected int findFormActionBodyEnd(final StringBuilder source, final int offset) {
        int i = offset;
        for (char c = source.charAt(i); c != '"' && c != '\'' && c != ' ' && c != '>'; c = source.charAt(i)) {
            if (++i == source.length()) {
                return -1;
            }
        }
        return i;
    }

    protected int findFormActionBodyStart(final StringBuilder source, final int offset, final int limit) {
        final int indexOfHref = source.indexOf("action=", offset);
        if (indexOfHref >= limit) {
            return -1;
        }
        final int nextChar = indexOfHref + 7;
        return source.charAt(nextChar) == '"' || source.charAt(nextChar) == '\'' ? nextChar + 1 : nextChar;
    }

    protected static int indexOf(final StringBuilder source, final char[] target) {
        // return source.indexOf(target);
        return indexOf(source, target, 0);
    }

    protected static int indexOf(final StringBuilder source, final char[] target, final int fromIndex) {
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
}
