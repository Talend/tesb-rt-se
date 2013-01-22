<%@ page import="javax.servlet.http.HttpServletRequest, oauth2.common.ConsumerRegistration" %>

<%
    ConsumerRegistration reg = (ConsumerRegistration)request.getAttribute("newClient");
    String basePath = request.getContextPath() + request.getServletPath();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Client Application Registration Confirmation</title>
    <STYLE TYPE="text/css">
	<!--
	  div.padded {  
         padding-left: 15em;  
      }   
	-->
</STYLE>
</head>
<body>
<div class="padded">

<h1>Client Application Registration Confirmation</h1>
<em></em>
<br/>
<p>
<big><big>
Please use the provided Client Identifier and Shared Secret when<br/>requesting
Calendar resources of Social.com users as part of OAuth flows.
</big></big>
</p>
<br/>
<table>
        <tr>
            <td><big><big><big>Client Identifier:</big></big></big></td>
            <td>&nbsp;&nbsp;</td>
            <td><big><big><%= reg.getId() %></big></big></td>
        </tr>
        <tr>
            <td><big><big><big>Client Secret:</big></big></big></td>
            <td>&nbsp;&nbsp;</td>
            <td><big><big><%= reg.getSecret() %></big></big></td>
        </tr> 
</table>
<br/> 
<p>
<big><big>
Calendars of Social.com users can be accessed at <b><%= basePath %>thirdparty/calendar</b><br/> using an OAuth access token.
HTTP GET and POST verbs can be used.
</p>
<br/>
<p>
Please follow this <a href="<%= basePath %>forms/registerUser.jsp">link</a> to get a user registered with Social.com
</p>
</big></big>
</div>
</body>
</html>

