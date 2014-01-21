<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<h2>
    <fr:view name="list" property="name" />
</h2>

<fr:view name="list" property="itemSet">
    <fr:schema bundle="EXAMPLE_RESOURCES" type="org.fenixedu.bennu.renderers.example.domain.ShoppingListItem">
        <fr:slot name="product.name" key="label.example.shoppinglist.product" />
        <fr:slot name="amount" key="label.example.shoppinglist.amount" />
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
