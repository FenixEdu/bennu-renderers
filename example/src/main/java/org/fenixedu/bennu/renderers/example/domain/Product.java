/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of Renderers Example.
 *
 * Renderers Example is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Renderers Example is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Renderers Example.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.renderers.example.domain;

import java.util.TreeSet;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

public class Product extends Product_Base implements Comparable<Product> {

    public Product() {
        super();
        setBennu(Bennu.getInstance());
    }

    public Product(String name) {
        this();
        setName(name);
    }

    public static TreeSet<Product> getPossibleProducts() {
        if (Bennu.getInstance().getProductSet().isEmpty()) {
            initProducts();
        }
        return new TreeSet<>(Bennu.getInstance().getProductSet());
    }

    @Atomic
    private static void initProducts() {
        new Product("Maçã");
        new Product("Banana");
        new Product("Bróculos");
        new Product("Tomate");
    }

    @Override
    public int compareTo(Product o) {
        return getName().compareTo(o.getName());
    }
}
