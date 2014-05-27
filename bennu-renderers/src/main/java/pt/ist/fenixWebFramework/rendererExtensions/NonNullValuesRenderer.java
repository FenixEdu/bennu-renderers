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

import pt.ist.fenixWebFramework.renderers.ValuesRenderer;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;

public class NonNullValuesRenderer extends ValuesRenderer {

    @Override
    protected Layout getLayout(final Object object, Class type) {
        return new NonNullValuesLayout(getContext().getMetaObject());
    }

    class NonNullValuesLayout extends ValuesLayout {

        public NonNullValuesLayout(MetaObject metaObject) {
            super(metaObject);
        }

        @Override
        protected boolean hasMoreComponents() {
            for (int pos = this.index; pos < this.slots.size(); pos++) {
                MetaSlot slot = slots.get(pos);
                Object object = slot.getObject();

                if (object instanceof String) {
                    String string = (String) object;
                    object = (string.length() == 0) ? null : object;
                }

                if (object != null) {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected MetaSlot getNextSlot() {
            for (int pos = this.index; pos < this.slots.size(); pos++) {
                MetaSlot slot = slots.get(pos);
                Object object = slot.getObject();

                if (object instanceof String) {
                    String string = (String) object;
                    object = (string.length() == 0) ? null : object;
                }

                if (object != null) {
                    this.index = pos + 1;
                    return slot;
                }
            }

            return null;
        }

    }
}
