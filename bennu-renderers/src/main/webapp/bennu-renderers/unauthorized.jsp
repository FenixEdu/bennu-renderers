<%--

    Copyright © 2008 Instituto Superior Técnico

    This file is part of Bennu Renderers Framework.

    Bennu Renderers Framework is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Bennu Renderers Framework is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Bennu Renderers Framework.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%
response.setStatus(403);
%>
<!DOCTYPE html>

<head>
    <title>
        ${fr:message('resources.RendererResources', 'title.not.authorized')}
    </title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <style>
        body {
            font: 16px 'Roboto', sans-serif;
            background-color: #F1F1F1;
            font-weight:100;
            color: #617383;
        }
        .container {
            margin: 120px auto 0 auto;
            width: 700px;
            background-color: white;
            padding: 30px;
            border-radius: 7px;
        }
        .content { margin-top: 25px; }
        .right { float: right; }
        .title {
            border-bottom: 1px solid #eee;
            padding-bottom: 15px;
            font-size: 22px;
            min-height: 35px;
        }
    </style>
</head>

<body>
    <div class="container">
        <div class="title">
            ${fr:message('resources.RendererResources', 'title.not.authorized')}
            <span class="right">
                <img src="${pageContext.request.contextPath}/api/bennu-portal/configuration/logo"/>
            </span>
        </div>
        <div class="content">
            ${fr:message('resources.RendererResources', 'message.not.authorized')}
        </div>
    </div>
</body>
