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
package pt.ist.fenixWebFramework.servlets.filters.contentRewrite;

import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import com.google.common.hash.Hashing;

public final class GenericChecksumRewriter {

    public static final String CHECKSUM_ATTRIBUTE_NAME = "_request_checksum_";

    public static final String NO_CHECKSUM_PREFIX = "<!-- NO_CHECKSUM -->";

    private static final int LENGTH_OF_NO_CHECKSUM_PREFIX = NO_CHECKSUM_PREFIX.length();

    private static final String OPEN_A = "<a ";
    private static final String OPEN_FORM = "<form ";

    private static final String PREFIX_JAVASCRIPT = "javascript:";
    private static final String PREFIX_MAILTO = "mailto:";
    private static final String PREFIX_HTTP = "http://";
    private static final String PREFIX_HTTPS = "https://";

    private static final char CLOSE = '>';
    private static final char CARDINAL = '#';
    private static final char QUESTION_MARK = '?';

    private final String sessionSecret;

    public GenericChecksumRewriter(HttpSession session) {
        this.sessionSecret = RenderersSessionSecret.computeSecretFromSession(session);
    }

    private String calculateChecksum(final String source, final int start, final int end) {
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

    public String rewrite(String source) {
        int iOffset = 0;

        final StringBuilder response = new StringBuilder();

        while (true) {

            final int indexOfAopen = source.indexOf(OPEN_A, iOffset);
            final int indexOfFormOpen = source.indexOf(OPEN_FORM, iOffset);
            if (indexOfAopen >= 0 && (indexOfFormOpen < 0 || indexOfAopen < indexOfFormOpen)) {
                if (!isPrefixed(source, indexOfAopen)) {
                    final int indexOfAclose = source.indexOf(CLOSE, indexOfAopen);
                    if (indexOfAclose >= 0) {
                        final int indexOfHrefBodyStart = findHrefBodyStart(source, indexOfAopen, indexOfAclose);
                        if (indexOfHrefBodyStart >= 0) {
                            final char hrefBodyStartChar = source.charAt(indexOfHrefBodyStart - 1);
                            final int indexOfHrefBodyEnd = findHrefBodyEnd(source, indexOfHrefBodyStart, hrefBodyStartChar);
                            if (indexOfHrefBodyEnd >= 0) {

                                int indexOfJavaScript = source.indexOf(PREFIX_JAVASCRIPT, indexOfHrefBodyStart);
                                int indexOfMailto = source.indexOf(PREFIX_MAILTO, indexOfHrefBodyStart);
                                int indexOfHttp = source.indexOf(PREFIX_HTTP, indexOfHrefBodyStart);
                                int indexOfHttps = source.indexOf(PREFIX_HTTPS, indexOfHrefBodyStart);
                                if ((indexOfJavaScript < 0 || indexOfJavaScript > indexOfHrefBodyEnd)
                                        && (indexOfMailto < 0 || indexOfMailto > indexOfHrefBodyEnd)
                                        && (indexOfHttp < 0 || indexOfHttp > indexOfHrefBodyEnd)
                                        && (indexOfHttps < 0 || indexOfHttps > indexOfHrefBodyEnd)) {

                                    final int indexOfCardinal = source.indexOf(CARDINAL, indexOfHrefBodyStart);

                                    // For hash-based URLs
                                    if (indexOfCardinal == indexOfHrefBodyStart) {
                                        iOffset = continueToNextToken(response, source, iOffset, indexOfAclose);
                                        continue;
                                    }

                                    boolean hasCardinal =
                                            indexOfCardinal > indexOfHrefBodyStart && indexOfCardinal < indexOfHrefBodyEnd;
                                    if (hasCardinal) {
                                        response.append(source, iOffset, indexOfCardinal);
                                    } else {
                                        response.append(source, iOffset, indexOfHrefBodyEnd);
                                    }

                                    final String checksum = calculateChecksum(source, indexOfHrefBodyStart, indexOfHrefBodyEnd);
                                    final int indexOfQmark = source.indexOf(QUESTION_MARK, indexOfHrefBodyStart);
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

                                    nextIndex =
                                            Math.min(Math.min(indexOfJavaScript, indexOfMailto),
                                                    Math.min(indexOfHttps, indexOfHttp));

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
            } else if (indexOfFormOpen >= 0) {
                if (!isPrefixed(source, indexOfFormOpen)) {
                    final int indexOfFormClose = source.indexOf(CLOSE, indexOfFormOpen);
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
                        } else {
                            iOffset = continueToNextToken(response, source, iOffset, indexOfFormOpen);
                            continue;
                        }
                    }
                } else {
                    iOffset = continueToNextToken(response, source, iOffset, indexOfFormOpen);
                    continue;
                }
            }
            response.append(source, iOffset, source.length());
            break;
        }

        return response.toString();
    }

    private boolean isPrefixed(final String source, final int indexOfTagOpen) {
        return NO_CHECKSUM_PREFIX.regionMatches(0, source, indexOfTagOpen - LENGTH_OF_NO_CHECKSUM_PREFIX,
                LENGTH_OF_NO_CHECKSUM_PREFIX);
    }

    private int continueToNextToken(final StringBuilder response, final String source, final int iOffset, final int indexOfTag) {
        final int nextOffset = indexOfTag + 1;
        response.append(source, iOffset, nextOffset);
        return nextOffset;
    }

    private int findFormActionBodyEnd(final String source, final int offset) {
        int i = offset;
        for (char c = source.charAt(i); c != '"' && c != '\'' && c != ' ' && c != '>'; c = source.charAt(i)) {
            if (++i == source.length()) {
                return -1;
            }
        }
        return i;
    }

    private int findFormActionBodyStart(final String source, final int offset, final int limit) {
        final int indexOfHref = source.indexOf("action=", offset);
        if (indexOfHref < 0 || indexOfHref >= limit) {
            return -1;
        }
        final int nextChar = indexOfHref + 7;
        return source.charAt(nextChar) == '"' || source.charAt(nextChar) == '\'' ? nextChar + 1 : nextChar;
    }

    private int findHrefBodyEnd(final String source, final int offset, final char hrefBodyStartChar) {
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

    private int findHrefBodyStart(final String source, final int offset, int limit) {
        final int indexOfHref = source.indexOf("href=", offset);
        if (indexOfHref < 0 || indexOfHref >= limit) {
            return -1;
        }
        final int nextChar = indexOfHref + 5;
        return source.charAt(nextChar) == '"' || source.charAt(nextChar) == '\'' ? nextChar + 1 : nextChar;
    }

}
