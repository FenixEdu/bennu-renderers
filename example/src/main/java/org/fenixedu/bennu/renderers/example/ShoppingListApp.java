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
package org.fenixedu.bennu.renderers.example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.renderers.example.domain.ShoppingList;
import org.fenixedu.bennu.renderers.example.domain.ShoppingListItem;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.base.BaseAction;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsApplication;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

@Mapping(path = "/shopping")
@StrutsApplication(path = "shopping", bundle = "ExampleResources", descriptionKey = "title.example.shoppinglist.description",
        titleKey = "title.example.shoppinglist", accessGroup = "anyone")
@StrutsFunctionality(app = ShoppingListApp.class, bundle = "ExampleResources",
        descriptionKey = "title.example.shoppinglist.list.description", path = "list",
        titleKey = "title.example.shoppinglist.list")
public class ShoppingListApp extends BaseAction {

    @EntryPoint
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("list", Bennu.getInstance().getShoppingListSet());
        return forward("/example/shoppinglist.jsp");
    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return forward("/example/createShoppinglist.jsp");
    }

    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ShoppingList list = getDomainObject(request, "listId");
        request.setAttribute("list", list);
        return forward("/example/viewShoppinglist.jsp");
    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return list(mapping, form, request, response);
    }

    public ActionForward addItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ShoppingListItem item = getRenderedObject("create");
        RenderUtils.invalidateViewState();
        request.setAttribute("list", item.getShoppingList());
        return forward("/example/viewShoppinglist.jsp");
    }

}
