package pt.ist.fenixWebFramework.renderers.utils;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.math.IntMath;

public class CollectionPager<T> {

    private final Collection<T> collection;
    private final int perPage;

    public CollectionPager(Collection<T> collection, int perPage) {
        super();
        this.collection = collection;
        this.perPage = perPage;
    }

    public int getNumberOfPages() {
        return IntMath.divide(collection.size(), perPage, RoundingMode.CEILING);
    }

    public Collection<T> getPage(int pageNum) {
        if (pageNum < 1) {
            pageNum = 1;
        }
        return collection.stream().skip((pageNum - 1) * perPage).limit(perPage).collect(Collectors.toList());
    }
}
