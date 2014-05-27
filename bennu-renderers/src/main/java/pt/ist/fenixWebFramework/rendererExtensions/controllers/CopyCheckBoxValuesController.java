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
/**
 * 
 */
package pt.ist.fenixWebFramework.rendererExtensions.controllers;

import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBox;
import pt.ist.fenixWebFramework.renderers.components.HtmlMultipleValueComponent;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlController;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;

public class CopyCheckBoxValuesController extends HtmlController {

    private List<HtmlCheckBox> checkboxes;

    private boolean copyTrueValues;

    public CopyCheckBoxValuesController() {
        super();

        this.checkboxes = new ArrayList<HtmlCheckBox>();
        this.copyTrueValues = true;
    }

    public CopyCheckBoxValuesController(final boolean copyTrueValues) {
        this();
        this.copyTrueValues = copyTrueValues;
    }

    public void addCheckBox(HtmlCheckBox checkBox) {
        this.checkboxes.add(checkBox);
    }

    @Override
    public void execute(IViewState viewState) {
        HtmlMultipleValueComponent component = (HtmlMultipleValueComponent) getControlledComponent();

        List<String> values = new ArrayList<String>();

        for (HtmlCheckBox checkBox : this.checkboxes) {
            if (checkBox.isChecked() == copyTrueValues) {
                values.add(checkBox.getValue());
            }
        }

        component.setValues(values.toArray(new String[0]));
    }
}