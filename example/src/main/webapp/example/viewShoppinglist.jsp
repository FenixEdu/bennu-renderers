<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of Renderers Example.

    Renderers Example is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Renderers Example is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Renderers Example.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<h2>
    <fr:view name="list" property="name" />
</h2>

<fr:view name="list" property="itemSet">
    <fr:schema bundle="EXAMPLE_RESOURCES" type="org.fenixedu.bennu.renderers.example.domain.ShoppingListItem">
        <fr:slot name="product.name" key="label.example.shoppinglist.product" />
        <fr:slot name="amount" key="label.example.shoppinglist.amount" help="help.example.shoppinglist.amount" />
    </fr:schema>
    <fr:layout name="tabular"></fr:layout>
</fr:view>

<fr:create id="create" type="org.fenixedu.bennu.renderers.example.domain.ShoppingListItem"
    action="shopping.do?method=addItem">
    <fr:schema bundle="EXAMPLE_RESOURCES" type="org.fenixedu.bennu.renderers.example.domain.ShoppingListItem">
        <fr:hidden slot="shoppingList" name="list" />
        <fr:slot name="product" layout="menu-select" key="label.example.shoppinglist.product" required="true">
            <fr:property name="from" value="possibleProducts"></fr:property>
            <fr:property name="format" value="\${name}" />
        </fr:slot>
        <fr:slot name="amount" key="label.example.shoppinglist.amount" required="true" />
    </fr:schema>
</fr:create>
