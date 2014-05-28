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
package pt.ist.fenixWebFramework.renderers.components.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;

public class HiddenSlot implements Serializable {
    private String name;
    private Class<Converter> converter;
    private MetaSlotKey key;
    private List<String> values;
    private boolean multiple;

    public HiddenSlot(String name, Class<Converter> converter) {
        super();

        this.name = name;
        this.converter = converter;
        this.values = new ArrayList<String>();
        this.multiple = false;
    }

    public HiddenSlot(String slot, String value, Class<Converter> converter) {
        this(slot, converter);

        addValue(value);
    }

    public Class<Converter> getConverter() {
        return this.converter;
    }

    public void setConverter(Class<Converter> converter) {
        this.converter = converter;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return this.values;
    }

    public void addValue(String value) {
        this.values.add(value);
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isMultiple() {
        return this.multiple || getValues().size() > 1;
    }

    public MetaSlotKey getKey() {
        return this.key;
    }

    public void setKey(MetaSlotKey key) {
        this.key = key;
    }
}