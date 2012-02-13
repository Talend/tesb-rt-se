<%@ page import="javax.servlet.http.HttpServletRequest, oauth.common.ConsumerRegistration" %>

<%
    ConsumerRegistration reg = (ConsumerRegistration)request.getAttribute("newClient");
    String basePath = request.getContextPath() + request.getServletPath();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Client Application Registration Confirmation</title>
</head>
<body>
<h1>Client Application Registration Confirmation</h1>
<em></em>
<p>
Please use the provided Client Identifier and Shared Secret when requesting
Calendar resources of Social.com users as part of OAuth flows.
</p>
<table>
        <tr>
            <td>Client Identifier:</td>
            <td><%= reg.getId() %></td>
        </tr>
        <tr>
            <td>Client Shared Secret:</td>
            <td><%= reg.getSecret() %></td>
        </tr> 
</table> 
<p>
Calendar resources of individual users can be accessed at <%= basePath %>thirdparty/calendar using an OAuth access token.
Only HTTP GET verbs can be used.
</p>

<p>
Please follow this <a href="<%= basePath %>forms/registerUser.jsp">link</a> to get a user registered with Social.com 
</p>

</body>
</html>

