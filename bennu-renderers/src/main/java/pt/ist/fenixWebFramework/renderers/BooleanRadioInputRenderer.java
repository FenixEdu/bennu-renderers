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
import pt.ist.fenixWebFramework.renderers.components.HtmlLabel;
import pt.ist.fenixWebFramework.renderers.components.HtmlListItem;
import pt.ist.fenixWebFramework.renderers.components.HtmlRadioButton;
import pt.ist.fenixWebFramework.renderers.components.HtmlRadioButtonList;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderMode;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.utils.RendererPropertyUtils;

/**
 * The <code>BooleanRadioInputRender</code> provides a way of doing the
 * input of an Boolean value by using a list of radio buttons. All the values
 * of that Boolean are presented as a radio button and the use can choose
 * one of the values.
 * 
 * <p>
 * Example:
 * <ul>
 * <li><input type="radio" name="same"/>True</li>
 * <li><input type="radio" name="same" checked="checked"/>False</li>
 * </ul>
 * 
 * @author mrsp
 */
public class BooleanRadioInputRenderer extends InputRenderer {

    /**
     * this field is a format {@link FormatRenderer} that must evaluate to true
     * of false, if true the slot will be rendered read-only, if false it won't
     * 
     * @author joantune - João Antunes
     */
    private String readOnlyIf;
    private String readOnlyIfNot;

    private String trueLabel;
    private String falseLabel;
    private String bundle;

    private String eachClasses;

    private String eachStyle;

    public BooleanRadioInputRenderer() {
        super();

        setStyle("white-space: nowrap;");
    }

    public String getReadOnlyIf() {
        return readOnlyIf;
    }

    /**
     * This property is a formatted property (like {@link ConditionalFormatRenderer}) that is evaluated. If it is true, the
     * slot will be considered read only, if false it won't
     * 
     * @author joantune - João Antunes
     * @property
     */
    public void setReadOnlyIf(String readOnlyIf) {
        this.readOnlyIf = readOnlyIf;
    }

    public String getEachClasses() {
        return eachClasses;
    }

    public String getReadOnlyIfNot() {
        return readOnlyIfNot;
    }

    /**
     * This property is a formatted property (like {@link ConditionalFormatRenderer}) that is evaluated. If it is false, the
     * slot will be considered read only, if true it won't
     * 
     * @author joantune - João Antunes
     * @property
     */
    public void setReadOnlyIfNot(String readOnlyIfNot) {
        this.readOnlyIfNot = readOnlyIfNot;
    }

    /**
     * This property will set the class for each list item
     * 
     * @property
     */
    public void setEachClasses(String eachClasses) {
        this.eachClasses = eachClasses;
    }

    public String getEachStyle() {
        return eachStyle;
    }

    /**
     * This property will set the style for each list item
     * 
     * @property
     */
    public void setEachStyle(String eachStyle) {
        this.eachStyle = eachStyle;
    }

    public String getBundle() {
        return this.bundle;
    }

    /**
     * Sets the bundle to be used when overring the boolean value presentation
     * with labels.
     * 
     * @property
     */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getFalseLabel() {
        return this.falseLabel;
    }

    /**
     * Sets the key to use when presenting the <code>false</code> value.
     * 
     * @property
     */
    public void setFalseLabel(String falseLabel) {
        this.falseLabel = falseLabel;
    }

    public String getTrueLabel() {
        return this.trueLabel;
    }

    /**
     * Sets the key to use when presenting the <code>true</code> value.
     * 
     * @property
     */
    public void setTrueLabel(String trueLabel) {
        this.trueLabel = trueLabel;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {

                Boolean booleanObject = (Boolean) object;

                if (getReadOnlyIf() != null || getReadOnlyIfNot() != null) {
                    //let's get its parent to assert what to do regarding the read only
                    Object parentObject = null;
                    if (getContext().getParentContext() != null) {
                        parentObject = getContext().getParentContext().getMetaObject().getObject();
                    }

                    Boolean useReadOnlyIfResult = null;
                    Boolean useReadOnlyIfNotResult = null;
                    try {
                        if (getReadOnlyIf() != null) {
                            useReadOnlyIfResult =
                                    (Boolean) RendererPropertyUtils.getProperty(parentObject, getReadOnlyIf(), false);
                        }
                        if (getReadOnlyIfNot() != null) {
                            useReadOnlyIfNotResult =
                                    (Boolean) RendererPropertyUtils.getProperty(parentObject, getReadOnlyIfNot(), false);
                        }
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }

                    if ((useReadOnlyIfNotResult != null || useReadOnlyIfResult != null)) {
                        if ((useReadOnlyIfNotResult != null && !useReadOnlyIfNotResult)
                                || (useReadOnlyIfResult != null && useReadOnlyIfResult)) {
                            if (booleanObject == null) {
                                return new HtmlText("");
                            }
                            return new HtmlText(String.valueOf(booleanObject));
                        }
                    }

                }

                HtmlRadioButtonList radioList = new HtmlRadioButtonList();

                Boolean booleanTrue = Boolean.TRUE;
                MetaObject booleanMetaObject = MetaObjectFactory.createObject(booleanTrue, null);
                PresentationContext newContext = getContext().createSubContext(booleanMetaObject);
                newContext.setRenderMode(RenderMode.OUTPUT);
                HtmlComponent component = getBooleanComponent(booleanTrue, newContext);

                HtmlLabel trueLabel = new HtmlLabel();
                trueLabel.setBody(component);
                HtmlRadioButton buttonTrue = radioList.addOption(trueLabel, booleanTrue.toString());
                trueLabel.setFor(buttonTrue);

                Boolean booleanFalse = Boolean.FALSE;
                booleanMetaObject = MetaObjectFactory.createObject(booleanFalse, null);
                newContext = getContext().createSubContext(booleanMetaObject);
                newContext.setRenderMode(RenderMode.OUTPUT);
                component = getBooleanComponent(booleanFalse, newContext);

                HtmlLabel falseLabel = new HtmlLabel();
                falseLabel.setBody(component);
                HtmlRadioButton buttonFalse = radioList.addOption(falseLabel, booleanFalse.toString());
                falseLabel.setFor(buttonFalse);

                buttonTrue.setChecked(booleanObject == null ? false : booleanObject);
                buttonFalse.setChecked(booleanObject == null ? false : !booleanObject);

                radioList.setTargetSlot((MetaSlotKey) getInputContext().getMetaObject().getKey());

                for (HtmlListItem item : radioList.getList().getItems()) {
                    item.setClasses(getEachClasses());
                    item.setStyle(getEachStyle());
                }

                return radioList;
            }

            private HtmlComponent getBooleanComponent(Boolean value, PresentationContext newContext) {
                if (value == null) {
                    return RenderKit.getInstance().render(newContext, value);
                } else {
                    if (value) {
                        if (getTrueLabel() == null) {
                            return RenderKit.getInstance().render(newContext, value);
                        } else {
                            return new HtmlText(RenderUtils.getResourceString(getBundle(), getTrueLabel()));
                        }
                    } else {
                        if (getFalseLabel() == null) {
                            return RenderKit.getInstance().render(newContext, value);
                        } else {
                            return new HtmlText(RenderUtils.getResourceString(getBundle(), getFalseLabel()));
                        }
                    }
                }
            }

        };
    }
}
