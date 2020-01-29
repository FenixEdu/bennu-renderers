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
package pt.ist.fenixWebFramework.renderers.validators;

import pt.ist.fenixWebFramework.renderers.components.HtmlInputFile;
import pt.ist.fenixWebFramework.renderers.plugin.RenderersRequestProcessorImpl;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;

import java.io.IOException;

/**
 * This validator can be used to validate uploaded files. It allows you to
 * restrict the file maximum size, extension, and type.
 * 
 * @author cfgi
 */
public class FileValidator extends HtmlValidator {

    private boolean required;

    private String maxSize;
    private String acceptedExtensions;
    private String acceptedTypes;

    private String typeMessage;
    private String extensionMessage;
    private String sizeMessage;

    private String bundle;

    private Object[] arguments;

    public FileValidator() {
        super();
        setTypeMessage("renderers.validator.file.type");
        setExtensionMessage("renderers.validator.file.extension");
        setSizeMessage("renderers.validator.file.size");
    }

    public FileValidator(HtmlChainValidator htmlChainValidator) {
        super(htmlChainValidator);

        setTypeMessage("renderers.validator.file.type");
        setExtensionMessage("renderers.validator.file.extension");
        setSizeMessage("renderers.validator.file.size");
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getAcceptedExtensions() {
        return this.acceptedExtensions;
    }

    /**
     * The list file extensions allowed separated by a comma. For example <code>"gif,jpg,png"</code>. If the extension is
     * specified then it has
     * priority over the accepted types. If the file has no extension then the
     * accepted types are used if provided.
     */
    public void setAcceptedExtensions(String acceptedExtensions) {
        this.acceptedExtensions = acceptedExtensions.toLowerCase();
    }

    public String getAcceptedTypes() {
        return this.acceptedTypes != null ? this.acceptedTypes.toLowerCase() : null;
    }

    /**
     * List of accepted types separated by a comma. For example <code>"image/gif,image/jpg,image/png"</code>. You can also use
     * wildcards like <code>"image/*"</code>.
     */
    public void setAcceptedTypes(String acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }

    public String getMaxSize() {
        return this.maxSize;
    }

    /**
     * The maximum size allowed for files. You can specify the units using the
     * suffix <code>"MB"</code>, <code>"KB"</code>, or <code>"B"</code>.
     * If no units are given the value is considered to be in bytes.
     */
    public void setMaxSize(String maxSize) {
        this.maxSize = maxSize;
    }

    public String getExtensionMessage() {
        return extensionMessage;
    }

    public void setExtensionMessage(String extensionMessage) {
        this.extensionMessage = extensionMessage;
    }

    public String getSizeMessage() {
        return sizeMessage;
    }

    public void setSizeMessage(String sizeMessage) {
        this.sizeMessage = sizeMessage;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
    }

    @Override
    public String getBundle() {
        return bundle;
    }

    @Override
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    private long convertedMaxSize() {
        String maxSize = getMaxSize().trim().toLowerCase();
        long value;
        long multiplier = 1;

        if (maxSize.matches("[0-9]+(m|k)?b")) {
            int index;

            index = maxSize.indexOf("mb");
            if (index != -1) {
                multiplier = 1024 * 1024;
            } else {
                index = maxSize.indexOf("kb");
                if (index != -1) {
                    multiplier = 1024;
                } else {
                    index = maxSize.indexOf("b");
                }
            }

            value = Long.parseLong(maxSize.substring(0, index));
        } else {
            value = Long.parseLong(maxSize);
        }

        return value * multiplier;
    }

    @Override
    protected String getResourceMessage(String message) {
        if (this.arguments == null || this.arguments.length == 0) {
            return RenderUtils.getResourceString(getBundle(), message);
        } else {
            return RenderUtils.getFormatedResourceString(getBundle(), message, this.arguments);
        }
    }

    @Override
    public void performValidation() {
        HtmlInputFile fileField = (HtmlInputFile) getComponent();

        UploadedFile file = RenderersRequestProcessorImpl.getUploadedFile(fileField.getName());
        if (file == null && isRequired()) {
            setInvalid("renderers.validator.required");
            return;
        }

        if (file == null) {
            return;
        }

        if (getMaxSize() != null) {
            long size = file.getSize();
            long maxSize = convertedMaxSize();

            if (size > maxSize) {
                setInvalid(getSizeMessage(), maxSize, getMaxSize());
                return;
            }
        }

        if (getAcceptedExtensions() != null) {
            String fileName = file.getName();
            int index = fileName.lastIndexOf(".");

            if (index != -1) {
                String extension = fileName.substring(index + 1).toLowerCase();

                if (!getAcceptedExtensions().contains(extension)) {
                    setInvalid(getExtensionMessage(), getAcceptedExtensions(), extension);
                    return;
                } else {
                    setValid(true);
                }
            }
        }

        if (getAcceptedTypes() != null) {
            Tika tika = new Tika();
            String contentType;
            try {
                contentType = tika.detect(file.getInputStream(), Metadata.CONTENT_TYPE);
                if (!matchesMimeType(contentType.toLowerCase())) {
                    String typesFormated = getAcceptedTypes().replace(",", ", ");
                    setInvalid(getTypeMessage(), typesFormated, file.getContentType());
                    return;
                }
            } catch (IOException e) {
                setValid(false);
            }
        }

        setValid(true);
    }

    private boolean matchesMimeType(String contentType) {
        String[] acceptedTypes = getAcceptedTypes().split(",");

        for (String acceptedType : acceptedTypes) {
            String accepted = acceptedType;

            if (accepted.contains("*")) {
                accepted = accepted.replace("*", ".*");
            }

            if (contentType.matches(accepted)) {
                return true;
            }
        }

        return false;
    }

    private void setInvalid(String message, Object... args) {
        setMessage(message);
        this.arguments = args;

        setValid(false);
    }
}
