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
package pt.ist.fenixWebFramework.renderers.components;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.jsp.PageContext;

import pt.ist.fenixWebFramework.renderers.components.tags.HtmlTag;

import com.google.common.base.Predicate;

public abstract class HtmlComponent implements Serializable {

    public enum TextDirection {
        RIGTH_TO_LEFT, LEFT_TO_RIGHT;

        @Override
        public String toString() {
            if (this == RIGTH_TO_LEFT) {
                return "rtl";
            } else {
                return "ltr";
            }
        }
    };

    private boolean visible;
    private boolean indented;

    // %coreattrs
    private String id;
    private String classes;
    private String style;
    private String title;

    // %i18n
    private String language;
    private TextDirection direction;

    // %event
    private String onClick;
    private String onDblClick;
    private String onMouseDown;
    private String onMouseUp;
    private String onMouseOver;
    private String onMouseMove;
    private String onMouseOut;
    private String onKeyPress;
    private String onKeyDown;
    private String onKeyUp;

    // custom
    private final Map<String, String> custom;

    public HtmlComponent() {
        this.custom = new HashMap<String, String>();
        this.visible = true;
        this.indented = true;

    }

    public String getClasses() {
        return classes;
    }

    public void addClass(String newClass) {
        this.classes = classes == null ? newClass : classes + " " + newClass;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getId() {
        return id;
    }

    /**
     * Sets the id of the component. If the given id is not a valid id then it
     * will be transformed to obey SGML rules. As such {@link #getId()} may
     * return a value that is not equal to the one given to this method.
     * 
     * @param id
     *            the desired id
     */
    public void setId(String id) {
        this.id = getValidIdOrName(id);
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TextDirection getDirection() {
        return this.direction;
    }

    public void setDirection(TextDirection direction) {
        this.direction = direction;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isIndented() {
        return this.indented;
    }

    public void setIndented(boolean idented) {
        this.indented = idented;
    }

    public String getOnClick() {
        return this.onClick;
    }

    public void setOnClick(String onclick) {
        this.onClick = onclick;
    }

    public String getOnDblClick() {
        return this.onDblClick;
    }

    public void setOnDblClick(String ondblclick) {
        this.onDblClick = ondblclick;
    }

    public String getOnKeyDown() {
        return this.onKeyDown;
    }

    public void setOnKeyDown(String onkeydown) {
        this.onKeyDown = onkeydown;
    }

    public String getOnKeyPress() {
        return this.onKeyPress;
    }

    public void setOnKeyPress(String onkeypress) {
        this.onKeyPress = onkeypress;
    }

    public String getOnKeyUp() {
        return this.onKeyUp;
    }

    public void setOnKeyUp(String onkeyup) {
        this.onKeyUp = onkeyup;
    }

    public String getOnMouseDown() {
        return this.onMouseDown;
    }

    public void setOnMouseDown(String onmousedown) {
        this.onMouseDown = onmousedown;
    }

    public String getOnMouseMove() {
        return this.onMouseMove;
    }

    public void setOnMouseMove(String onmousemove) {
        this.onMouseMove = onmousemove;
    }

    public String getOnMouseOut() {
        return this.onMouseOut;
    }

    public void setOnMouseOut(String onmouseout) {
        this.onMouseOut = onmouseout;
    }

    public String getOnMouseOver() {
        return this.onMouseOver;
    }

    public void setOnMouseOver(String onmouseover) {
        this.onMouseOver = onmouseover;
    }

    public String getOnMouseUp() {
        return this.onMouseUp;
    }

    public void setOnMouseUp(String onmouseup) {
        this.onMouseUp = onmouseup;
    }

    public String getAttribute(String name) {
        return this.custom.get(name);
    }

    public void setAttribute(String name, String value) {
        this.custom.put(name, value);
    }

    public List<HtmlComponent> getChildren() {
        return new ArrayList<HtmlComponent>();
    }

    public void draw(Writer writer) throws IOException {
        HtmlTag tag = getOwnTag(null);
        tag.writeTag(writer);
    }

    public void draw(PageContext context) throws IOException {
        HtmlTag tag = getOwnTag(context);
        tag.writeTag(context);
    }

    public HtmlTag getOwnTag(PageContext context) {
        HtmlTag tag = new HtmlTag("div"); // generic container

        tag.setAttribute("id", getId());
        tag.setAttribute("class", getClasses());
        tag.setAttribute("style", getStyle());
        tag.setAttribute("title", getTitle());

        tag.setAttribute("lang", getLanguage());

        if (getDirection() != null) {
            tag.setAttribute("dir", getDirection().toString());
        }

        tag.setAttribute("onclick", getOnClick());
        tag.setAttribute("ondblclick", getOnDblClick());
        tag.setAttribute("onmousedown", getOnMouseDown());
        tag.setAttribute("onmouseup", getOnMouseUp());
        tag.setAttribute("onmouseover", getOnMouseOver());
        tag.setAttribute("onmousemove", getOnMouseMove());
        tag.setAttribute("onmouseout", getOnMouseOut());
        tag.setAttribute("onkeypress", getOnKeyPress());
        tag.setAttribute("onkeydown", getOnKeyDown());
        tag.setAttribute("onkeyup", getOnKeyUp());

        tag.setVisible(isVisible());
        tag.setIndented(isIndented());

        for (Entry<String, String> entry : this.custom.entrySet()) {
            tag.setAttribute(entry.getKey(), entry.getValue());
        }

        return tag;
    }

    public HtmlComponent getChild(Predicate<HtmlComponent> predicate) {
        for (HtmlComponent child : getChildren()) {
            if (predicate.apply(child)) {
                return child;
            } else {
                HtmlComponent foundComponent = child.getChild(predicate);
                if (foundComponent != null) {
                    return foundComponent;
                }
            }
        }

        return null;
    }

    public List<HtmlComponent> getChildren(Predicate<HtmlComponent> predicate) {
        List<HtmlComponent> results = new ArrayList<HtmlComponent>();

        for (HtmlComponent child : getChildren()) {
            if (predicate.apply(child)) {
                results.add(child);
            }

            results.addAll(child.getChildren(predicate));
        }

        return results;
    }

    public HtmlComponent getChildWithId(final String id) {
        return getChild(new Predicate<HtmlComponent>() {
            @Override
            public boolean apply(HtmlComponent component) {
                return component.getId().equals(id);
            }
        });
    }

    public static HtmlComponent getComponent(HtmlComponent component, Predicate<HtmlComponent> predicate) {
        if (component == null) {
            return null;
        }

        if (predicate.apply(component)) {
            return component;
        }

        return component.getChild(predicate);
    }

    public static List<HtmlComponent> getComponents(HtmlComponent component, Predicate<HtmlComponent> predicate) {
        if (component == null) {
            return new ArrayList<HtmlComponent>();
        }

        if (predicate.apply(component)) {
            List<HtmlComponent> results = new ArrayList<HtmlComponent>();
            results.add(component);

            return results;
        } else {
            return component.getChildren(predicate);
        }
    }

    //
    //
    //

    private static Pattern ACCEPTABLE_ID_START_CHAR = Pattern.compile("[A-Za-z]");
    private static Pattern ACCEPTABLE_ID_CHAR = Pattern.compile("[A-Za-z0-9_:.-]");

    public static String getValidIdOrName(String desired) {
        if (desired == null) {
            return null;
        }

        // ID and NAME tokens must begin with a letter ([A-Za-z]) and may be
        // followed by any number of letters, digits ([0-9]), hyphens ("-"),
        // underscores ("_"), colons (":"), and periods (".").

        StringBuilder name = new StringBuilder();

        int position = 0;
        for (Character c : desired.toCharArray()) {
            if (position == 0) {
                if (!ACCEPTABLE_ID_START_CHAR.matcher(c.toString()).matches()) {
                    name.append("i"); // ensure that it starts with a letter
                }
            }

            if (ACCEPTABLE_ID_CHAR.matcher(c.toString()).matches()) {
                name.append(c);
            } else {
                name.append('_'); // an acceptable character
            }

            position++;
        }

        return name.toString();
    }
}
