<!Doctype html>

<%@page import="pt.ist.bennu.core.presentationTier.DefaultContext"%>
<%
	DefaultContext context = (DefaultContext) request.getAttribute("_CONTEXT_");
%>

<html>
	<head>
		<script type="text/javascript" src="js/libs/mustache/mustache.js"></script>
		<script type="text/javascript" src="js/libs/jquery/jquery.js"></script>
		<script src="bennu-portal/portal.js" type="text/javascript"></script>
	</head>
	<body style="display:none;">
		<jsp:include page="<%=context.getBody()%>" />
	</body>
</html>