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
/* 
* @(#)BaseAction.java 
* 
* Copyright 2009 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package org.fenixedu.bennu.struts.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jvstm.cps.ConsistencyException;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;

import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.io.ByteStreams;

/**
 * 
 * @author João Antunes
 * @author João Neves
 * @author Pedro Santos
 * @author Anil Kassamali
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public abstract class BaseAction extends DispatchAction {

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        request.setAttribute("bennu", Bennu.getInstance());
        return super.execute(mapping, form, request, response);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getAttribute(final HttpServletRequest request, final String attributeName) {
        final T t = (T) request.getAttribute(attributeName);
        return t == null ? (T) request.getParameter(attributeName) : t;
    }

    protected <T extends DomainObject> T getDomainObject(final HttpServletRequest request, final String name) {
        final String parameter = request.getParameter(name);
        return FenixFramework.getDomainObject(parameter != null ? parameter : (String) request.getAttribute(name));
    }

    protected <T> T getRenderedObject() {
        final IViewState viewState = RenderUtils.getViewState();
        return getRenderedObject(viewState);
    }

    protected <T> T getRenderedObject(final String id) {
        final IViewState viewState = RenderUtils.getViewState(id);
        return getRenderedObject(viewState);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getRenderedObject(final IViewState viewState) {
        if (viewState != null) {
            MetaObject metaObject = viewState.getMetaObject();
            if (metaObject != null) {
                return (T) metaObject.getObject();
            }
        }
        return null;
    }

    protected byte[] consumeInputStream(final InputStream inputStream) throws IOException {
        return ByteStreams.toByteArray(inputStream);
    }

    protected ActionForward download(final HttpServletResponse response, final String filename, final byte[] bytes,
            final String contentType) throws IOException {
        try (final OutputStream outputStream = response.getOutputStream()) {
            response.setContentType(contentType);
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            response.setContentLength(bytes.length);
            outputStream.write(bytes);
            outputStream.flush();
            return null;
        }
    }

    protected ActionForward download(final HttpServletResponse response, final String filename, InputStream stream,
            final String contentType) throws IOException {
        try (OutputStream outputStream = response.getOutputStream()) {
            response.setContentType(contentType);
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            int byteCount = (int) ByteStreams.copy(stream, outputStream);
            response.setContentLength(byteCount);
            outputStream.flush();
            return null;
        } finally {
            stream.close();
        }
    }

    protected void addLocalizedMessage(final HttpServletRequest request, final String localizedMessage) {
        final ActionMessages messages = getMessages(request);
        ActionMessage actionMessage = new ActionMessage(localizedMessage, false);
        messages.add("message", actionMessage);
        saveMessages(request, messages);
    }

    protected void addLocalizedSuccessMessage(final HttpServletRequest request, final String localizedMessage) {
        final ActionMessages messages = getMessages(request);
        ActionMessage actionMessage = new ActionMessage(localizedMessage, false);
        messages.add("messageSuccess", actionMessage);
        saveMessages(request, messages);
    }

    protected void addLocalizedWarningMessage(final HttpServletRequest request, final String localizedMessage) {
        final ActionMessages messages = getMessages(request);
        ActionMessage actionMessage = new ActionMessage(localizedMessage, false);
        messages.add("messageWarning", actionMessage);
        saveMessages(request, messages);
    }

    protected void addMessage(final HttpServletRequest request, final String key, final String... args) {
        addMessage(request, "message", key, args);
    }

    protected void addMessage(final HttpServletRequest request, final String property, final String key, final String... args) {
        final ActionMessages messages = getMessages(request);
        messages.add(property, new ActionMessage(key, args));
        saveMessages(request, messages);
    }

    protected void setAttribute(final HttpServletRequest request, final String attributeName, final Object attributeValue) {
        if (request != null) {
            request.setAttribute(attributeName, attributeValue);
        }
    }

    protected ActionForward redirect(final HttpServletRequest request, final String url) {
        final String digest =
                GenericChecksumRewriter.calculateChecksum(request.getContextPath() + url, request.getSession(false));
        final char seperator = url.indexOf('?') >= 0 ? '&' : '?';
        final String urlWithChecksum = url + seperator + GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME + '=' + digest;
        return new ActionForward(urlWithChecksum, true);
    }

    protected void displayConsistencyException(ConsistencyException exc, HttpServletRequest request) {
        if (exc.getLocalizedMessage() != null) {
            addLocalizedMessage(request, exc.getLocalizedMessage());
        } else {
            exc.printStackTrace();
            addLocalizedMessage(request, BundleUtil.getString("resources/MyorgResources", "error.ConsistencyException"));
        }
    }

    protected ActionForward forward(String path) {
        return new ActionForward(path);
    }

}
