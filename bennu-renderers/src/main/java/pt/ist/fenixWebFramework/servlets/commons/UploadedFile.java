package pt.ist.fenixWebFramework.servlets.commons;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a file that was uploaded by the user in a form. You can retrieve all uploaded files
 * by calling {@link pt.ist.fenixWebFramework.renderers.plugin.RenderersRequestProcessor#getAllUploadedFiles()}.
 * 
 * @author cfgi
 */
public interface UploadedFile {

    public String getName();

    public String getContentType();

    public long getSize();

    public InputStream getInputStream() throws IOException;

    public byte[] getFileData() throws FileNotFoundException, IOException;

}
