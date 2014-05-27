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

import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

public class BufferedFacadPrintWriter extends PrintWriter {

    final StringBuilder stringBuilder = new StringBuilder();

    final PrintWriter printWriter;

    public BufferedFacadPrintWriter(final PrintWriter printWriter) {
        super(printWriter);
        this.printWriter = printWriter;
    }

    @Override
    public void write(final char[] cbuf) {
        stringBuilder.append(cbuf);
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) {
        stringBuilder.append(cbuf, off, len);
    }

    @Override
    public void write(final int c) {
        stringBuilder.append((char) c);
    }

    @Override
    public void write(final String str) {
        stringBuilder.append(str);
    }

    @Override
    public void write(final String str, final int off, final int len) {
        stringBuilder.append(str, off, len);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    public void writeRealResponse(HttpSession session) {
        printWriter.write(new GenericChecksumRewriter(session).rewrite(this.stringBuilder.toString()));
        printWriter.flush();
        printWriter.close();
    }

    @Deprecated
    public String getContent() {
        return stringBuilder.toString();
    }

    public void resetBuffer() {
        stringBuilder.setLength(0);
    }

}
