package pt.ist.fenixWebFramework.rendererExtensions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.ist.fenixframework.DomainObject;

public class PageContainerBean implements Serializable {

    private transient List<? extends DomainObject> objects;
    private List<DomainObject> pageObjects;
    private DomainObject selected;
    private Integer numberOfPages;

    private Integer page = 1;

    public List<? extends DomainObject> getObjects() {
        return objects;
    }

    public void setObjects(List<? extends DomainObject> objects) {
        this.objects = objects;
        setPageObjects(null);
    }

    public List<DomainObject> getPageObjects() {
        if (this.pageObjects != null) {
            List<DomainObject> result = new ArrayList<DomainObject>();
            for (DomainObject domainObject : this.pageObjects) {
                result.add(domainObject);
            }
            return result;
        } else {
            return null;
        }
    }

    public void setPageObjects(List<? extends DomainObject> pageObjects) {
        if (pageObjects != null) {
            this.pageObjects = new ArrayList<DomainObject>();
            for (DomainObject object : pageObjects) {
                this.pageObjects.add(object);
            }
        } else {
            this.pageObjects = null;
        }
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public DomainObject getSelected() {
        return selected;
    }

    public void setSelected(DomainObject selected) {
        this.selected = selected;
    }

    public List<DomainObject> getPageByPageSize(int pageSize) {
        if (getPageObjects() != null) {
            return getPageObjects();
        } else {
            if (getObjects() != null && !getObjects().isEmpty()) {
                validatePageNumber(pageSize);
                int from = (getPage() - 1) * pageSize;
                int to = getObjects().size() > getPage() * pageSize ? getPage() * pageSize : getObjects().size();
                List<DomainObject> subList = new ArrayList<DomainObject>(getObjects().subList(from, to));
                setPageObjects(subList);
                return subList;
            } else {
                return Collections.emptyList();
            }
        }
    }

    private void validatePageNumber(int pageSize) {
        if (getPage() < 1) {
            setPage(1);
        } else {
            Integer numberOfPages = getNumberOfPages(pageSize);
            if (getPage() > numberOfPages) {
                setPage(numberOfPages);
            }
        }

    }

    public int getNumberOfPages(int pageSize) {
        if (getObjects() != null) {
            this.numberOfPages = (int) Math.ceil((double) getObjects().size() / pageSize);
        }
        return this.numberOfPages;
    }

    public boolean hasNextPage(int pageSize) {
        return getPage() < getNumberOfPages(pageSize);
    }

    public boolean hasPreviousPage(int pageSize) {
        return getPage() > 1;
    }

    public List<? extends DomainObject> getAllObjects() {
        if (getPageObjects() != null) {
            return getPageObjects();
        } else {
            if (getObjects() != null) {
                setPageObjects(getObjects());
                return getObjects();
            } else {
                return Collections.emptyList();
            }
        }
    }

    public void setPageJump(Integer pageJump) {
        setPage(pageJump);
    }

    public Integer getPageJump() {
        return null;
    }

}
