<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<fr:create id="create" type="pt.ist.bennu.renderers.example.domain.ShoppingList" action="shopping.do?method=list">
    <fr:schema bundle="EXAMPLE_RESOURCES" type="pt.ist.bennu.renderers.example.domain.ShoppingList">
        <fr:slot name="name" key="label.example.shoppinglist.name" required="true" />
    </fr:schema>
</fr:create>
