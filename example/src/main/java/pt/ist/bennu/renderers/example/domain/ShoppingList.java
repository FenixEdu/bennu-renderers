package pt.ist.bennu.renderers.example.domain;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.Bennu;

public class ShoppingList extends ShoppingList_Base {
    public ShoppingList() {
        super();
        setCreationDate(new DateTime());
        setBennu(Bennu.getInstance());
    }
}
