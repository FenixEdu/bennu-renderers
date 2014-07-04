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

<fr:create id="create" type="org.fenixedu.bennu.renderers.example.domain.ShoppingList" action="shopping.do?method=list">
    <fr:schema bundle="EXAMPLE_RESOURCES" type="org.fenixedu.bennu.renderers.example.domain.ShoppingList">
        <fr:slot name="name" key="label.example.shoppinglist.name" required="true" />
    </fr:schema>
</fr:create>
