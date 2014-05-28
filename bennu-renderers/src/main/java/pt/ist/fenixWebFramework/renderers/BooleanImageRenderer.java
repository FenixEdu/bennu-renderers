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
package pt.ist.fenixWebFramework.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlImage;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

/**
 * The graphic output renderer for a boolean value. The value is used to apply
 * the defined icon for such boolean value.
 * 
 * @author djfsi
 */
public class BooleanImageRenderer extends OutputRenderer {

    private String trueIconPath;
    private String falseIconPath;
    private Boolean contextRelative;
    private Boolean nullAsFalse;

    public String getFalseIconPath() {
        return this.falseIconPath;
    }

    /**
     * The icon to be shown when presenting a <code>false</code> value.
     * 
     * @property
     */
    public void setFalseIconPath(String falseIconPath) {
        this.falseIconPath = falseIconPath;
    }

    public String getTrueIconPath() {
        return this.trueIconPath;
    }

    /**
     * The icon to be shown when presenting the <code>true</code> value.
     * 
     * @property
     */
    public void setTrueIconPath(String trueIconPath) {
        this.trueIconPath = trueIconPath;
    }

    public Boolean isContextRelative() {
        return this.contextRelative;
    }

    /**
     * This identifies the nature of the icon file path as being either context
     * relative or not.
     * 
     * @property
     */
    public void setContextRelative(Boolean contextRelative) {
        this.contextRelative = contextRelative;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                Boolean booleanValue = (Boolean) object;

                if (booleanValue == null) {
                    if (!isNullAsFalse()) {
                        return new HtmlText();
                    }
                    booleanValue = false;
                }

                StringBuilder pathBuilder = new StringBuilder();
                if (contextRelative) {
                    pathBuilder.append(RenderUtils.getContextRelativePath(""));
                }

                pathBuilder.append(getIconPath(booleanValue));
                String fullPath = pathBuilder.toString();

                HtmlImage img = new HtmlImage();
                img.setSource(fullPath);

                return img;
            }

            private String getIconPath(Boolean booleanValue) {
                return booleanValue ? getTrueIconPath() : getFalseIconPath();
            }

        };
    }

    public void setNullAsFalse(Boolean nullAsFalse) {
        this.nullAsFalse = nullAsFalse;
    }

    public Boolean isNullAsFalse() {
        return nullAsFalse;
    }
}
