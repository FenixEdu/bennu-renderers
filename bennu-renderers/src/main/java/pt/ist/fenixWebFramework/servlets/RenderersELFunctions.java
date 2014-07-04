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
package pt.ist.fenixWebFramework.servlets;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.i18n.BundleUtil;

import pt.ist.fenixWebFramework.renderers.plugin.RenderersRequestProcessorImpl;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

/**
 * This class contains a series of utility methods, to be used as Expression Language functions.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class RenderersELFunctions {

    /**
     * Injects the context path and request checksum in the given path.
     * 
     * @param targetUrl
     *            The URL to point to. Must not include the context path
     * @return
     *         The final URL, with the context path and request checksum.
     */
    public static String checksumForRequest(String targetUrl) {
        HttpServletRequest request = RenderersRequestProcessorImpl.getCurrentRequest();
        String ctxPath = request.getContextPath();
        return ctxPath + GenericChecksumRewriter.injectChecksumInUrl(ctxPath, targetUrl, request.getSession(false));
    }

    /**
     * Returns the message defined in the given bundle, with the given key.
     * 
     * This method simply delegates to {@link BundleUtil}.getString()
     * 
     * @param bundle
     *            The bundle to look up the resource
     * @param key
     *            The resource key
     * @return
     *         The localized resource
     */
    public static String message(String bundle, String key) {
        return BundleUtil.getString(bundle, key);
    }

}
