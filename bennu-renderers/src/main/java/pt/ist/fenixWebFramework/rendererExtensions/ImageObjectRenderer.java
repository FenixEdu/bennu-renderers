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
package pt.ist.fenixWebFramework.rendererExtensions;

import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlImage;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

public class ImageObjectRenderer extends OutputRenderer {

    private String imageFormat;

    private boolean useParent;

    private boolean moduleRelative;

    private boolean contextRelative;

    public boolean isContextRelative() {
        return contextRelative;
    }

    public void setContextRelative(boolean contextRelative) {
        this.contextRelative = contextRelative;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public boolean isModuleRelative() {
        return moduleRelative;
    }

    public void setModuleRelative(boolean moduleRelative) {
        this.moduleRelative = moduleRelative;
    }

    public boolean isUseParent() {
        return useParent;
    }

    public void setUseParent(boolean useParent) {
        this.useParent = useParent;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new ImageObjectLayout();
    }

    public class ImageObjectLayout extends Layout {

        @Override
        public HtmlComponent createComponent(Object object, Class type) {
            HtmlImage image = new HtmlImage();
            String link = RenderUtils.getFormattedProperties(getImageFormat(), getTargetObject(object));
            if (isModuleRelative()) {
                link = RenderUtils.getModuleRelativePath("") + link;
            } else if (isContextRelative()) {
                link = RenderUtils.getContextRelativePath("") + link;
            }
            if (link.contains(".do")) {
                String checksum =
                        GenericChecksumRewriter.calculateChecksum(link, getContext().getViewState().getRequest()
                                .getSession(false));
                link = link + (link.contains("?") ? '&' : '?') + GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME + "=" + checksum;
            }
            image.setSource(link);
            return image;
        }

        protected Object getTargetObject(Object object) {
            if (isUseParent()) {
                if (getContext().getParentContext() != null) {
                    return getContext().getParentContext().getMetaObject().getObject();
                } else {
                    return null;
                }
            } else {
                return object;
            }
        }
    }
}
