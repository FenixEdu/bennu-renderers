<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<fr:view name="list">
    <fr:schema bundle="EXAMPLE_RESOURCES" type="org.fenixedu.bennu.renderers.example.domain.ShoppingList">
        <fr:slot name="name" key="label.example.shoppinglist.name" />
        <fr:slot name="creationDate" key="label.example.shoppinglist.creation" />
    </fr:schema>
    <fr:layout name="tabular">
        <fr:property name="link(view)" value="/shopping.do?method=view" />
        <fr:property name="param(view)" value="externalId/listId" />
        <fr:property name="key(view)" value="label.example.shoppinglist.view" />
        <fr:property name="bundle(view)" value="EXAMPLE_RESOURCES" />

        <fr:property name="link(delete)" value="/shopping.do?method=delete" />
        <fr:property name="param(delete)" value="externalId/listId" />
        <fr:property name="key(delete)" value="label.example.shoppinglist.delete" />
        <fr:property name="bundle(delete)" value="EXAMPLE_RESOURCES" />
    </fr:layout>
</fr:view>

<html:link action="shopping.do?method=create">
    <bean:message bundle="EXAMPLE_RESOURCES" key="label.example.shoppinglist.create" />
</html:link>
