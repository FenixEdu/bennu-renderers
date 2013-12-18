package org.fenixedu.bennu.renderers.example.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.DateTime;

public class ShoppingList extends ShoppingList_Base {
    public ShoppingList() {
        super();
        setCreationDate(new DateTime());
        setBennu(Bennu.getInstance());
    }
}
