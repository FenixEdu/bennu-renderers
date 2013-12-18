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
