<!Doctype html>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@page import="pt.ist.bennu.core.presentationTier.DefaultContext"%>

<%
	DefaultContext context = (DefaultContext) request.getAttribute("_CONTEXT_");
%>

<html>
	<head>
		<jsp:include page="<%=context.getHead()%>" />
		<script type="text/javascript" src="bankai/js/libs/mustache/mustache.js"></script>
		<script type="text/javascript" src="bankai/js/libs/jquery/jquery.js"></script>
		<script type="text/javascript" src="bennu-portal/portal.js"></script>
	</head>
	<body style="display:none;" class="body">
		<jsp:include page="<%=context.getBody()%>" />
	</body>
</html>