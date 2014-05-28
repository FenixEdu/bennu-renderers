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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

public class ResponseWrapper extends HttpServletResponseWrapper {

    protected BufferedFacadPrintWriter bufferedFacadPrintWriter = null;

    public ResponseWrapper(final HttpServletResponse httpServletResponse) throws IOException {
        super(httpServletResponse);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (bufferedFacadPrintWriter == null) {
            bufferedFacadPrintWriter = new BufferedFacadPrintWriter(getResponse().getWriter());
        }
        return bufferedFacadPrintWriter;
    }

    @Override
    public void flushBuffer() throws IOException {
        super.flushBuffer();
        if (bufferedFacadPrintWriter != null) {
            bufferedFacadPrintWriter.flush();
        }
    }

    public void writeRealResponse(HttpSession session) throws IOException {
        if (bufferedFacadPrintWriter != null) {
            bufferedFacadPrintWriter.writeRealResponse(session);
        }
    }

    @Deprecated
    public String getContent() {
        if (bufferedFacadPrintWriter != null) {
            return bufferedFacadPrintWriter.getContent();
        }
        return "";
    }

    @Override
    public void resetBuffer() {
        super.resetBuffer();
        if (bufferedFacadPrintWriter != null) {
            bufferedFacadPrintWriter.resetBuffer();
        }
    }

}
