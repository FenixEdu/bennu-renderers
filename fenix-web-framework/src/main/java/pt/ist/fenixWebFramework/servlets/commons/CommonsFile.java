package pt.ist.fenixWebFramework.servlets.commons;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;

/**
 * This is a wrapper around a {@link org.apache.commons.fileupload.FileItem file item} from Commons Upload.
 * 
 * @author cfgi
 */
public class CommonsFile implements UploadedFile {

    private FileItem commonsFile;

    public CommonsFile(FileItem commonsFile) {
        super();

        this.commonsFile = commonsFile;
    }

    @Override
    public String getName() {
        return this.commonsFile.getName();
    }

    @Override
    public String getContentType() {
        return this.commonsFile.getContentType();
    }

    @Override
    public long getSize() {
        return this.commonsFile.getSize();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.commonsFile.getInputStream();
    }

    @Override
    public byte[] getFileData() throws FileNotFoundException, IOException {
        return commonsFile.get();
    }

}
