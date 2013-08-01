<!Doctype html>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@page import="pt.ist.bennu.core.presentationTier.DefaultContext"%>

<%
	DefaultContext context = (DefaultContext) request.getAttribute("_CONTEXT_");
%>

<html>
	<head>
		<logic:iterate id="head" collection="<%=context.getHead()%>" type="java.lang.String">
			<jsp:include page="<%=head%>" />
		</logic:iterate>
		<script type="text/javascript" src="bankai/js/libs/mustache/mustache.js"></script>
		<script type="text/javascript" src="bankai/js/libs/jquery/jquery.js"></script>
		<logic:iterate id="script" collection="<%=context.getScripts()%>" type="java.lang.String">
			<jsp:include page="<%=script%>" />
		</logic:iterate>
		<script type="text/javascript" src="bennu-portal/portal.js"></script>
	</head>
	<body style="display:none;" class="body">
		<div id="portal-container">
			<jsp:include page="<%=context.getBody()%>" />
		</div>
	</body>
</html>