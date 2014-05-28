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

import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBox;
import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBoxList;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlController;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.components.state.ViewDestination;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

public class CheckBoxOptionListRendererWithPostBack extends CheckBoxOptionListRenderer {
    private String destination;

    public String getDestination() {
        return destination;
    }

    /**
     * Allows to choose the postback destination. If this property is not
     * specified the default "postback" destination is used.
     * 
     * @property
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        final Layout layout = super.getLayout(object, type);

        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object object, Class type) {
                HtmlCheckBoxList checkBoxList = (HtmlCheckBoxList) layout.createComponent(object, type);

                checkBoxList.setController(new PostBackController(getDestination()));
                for (HtmlCheckBox checkBox : checkBoxList.getCheckBoxes()) {
                    checkBox.setOnClick("this.form.submit();");
                }

                return checkBoxList;
            }

        };
    }

    private static class PostBackController extends HtmlController {

        private String destination;

        public PostBackController(String destination) {
            this.destination = destination;
        }

        @Override
        public void execute(IViewState viewState) {

            ViewDestination destination = viewState.getDestination(this.destination);

            if (destination != null) {
                viewState.setCurrentDestination(destination);
            } else {
                viewState.setCurrentDestination("postBack");
            }

            viewState.setSkipValidation(true);
        }

    }
}
