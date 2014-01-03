package pt.ist.fenixWebFramework.servlets.filters.contentRewrite;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriterFilter.RequestRewriterFactory;

public class ResponseWrapper extends HttpServletResponseWrapper {

    protected final HttpServletResponse httpServletResponse;

    protected BufferedFacadServletOutputStream bufferedFacadServletOutputStream = null;

    protected BufferedFacadPrintWriter bufferedFacadPrintWriter = null;

    public ResponseWrapper(final HttpServletResponse httpServletResponse) throws IOException {
        super(httpServletResponse);
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (bufferedFacadServletOutputStream == null) {
            bufferedFacadServletOutputStream = new BufferedFacadServletOutputStream(httpServletResponse.getOutputStream());
        }
        return bufferedFacadServletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (bufferedFacadPrintWriter == null) {
            bufferedFacadPrintWriter = new BufferedFacadPrintWriter(httpServletResponse.getWriter());
        }
        return bufferedFacadPrintWriter;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (bufferedFacadServletOutputStream != null) {
            bufferedFacadServletOutputStream.flush();
        }
        if (bufferedFacadPrintWriter != null) {
            bufferedFacadPrintWriter.flush();
        }
    }

    public void writeRealResponse(final HttpServletRequest request, final Collection<RequestRewriterFactory> requestRewriters)
            throws IOException {
        if (bufferedFacadServletOutputStream != null) {
            bufferedFacadServletOutputStream.writeRealResponse();
        }
        if (bufferedFacadPrintWriter != null) {
            bufferedFacadPrintWriter.writeRealResponse(request, requestRewriters);
        }
    }

    public String getContent() {
        if (bufferedFacadServletOutputStream != null) {
            return bufferedFacadServletOutputStream.getContent();
        }
        if (bufferedFacadPrintWriter != null) {
            return bufferedFacadPrintWriter.getContent();
        }
        return "";
    }

    @Override
    public void resetBuffer() {
        // super.resetBuffer();
        if (bufferedFacadServletOutputStream != null) {
            bufferedFacadServletOutputStream.resetBuffer();
        }
        if (bufferedFacadPrintWriter != null) {
            bufferedFacadPrintWriter.resetBuffer();
        }
    }

}
