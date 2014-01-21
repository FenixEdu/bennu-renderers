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
