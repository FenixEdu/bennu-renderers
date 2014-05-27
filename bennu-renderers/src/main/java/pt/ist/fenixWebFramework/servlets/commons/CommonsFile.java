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
