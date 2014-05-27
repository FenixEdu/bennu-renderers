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
