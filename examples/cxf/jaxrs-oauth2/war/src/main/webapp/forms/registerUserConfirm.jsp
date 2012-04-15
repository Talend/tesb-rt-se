<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%
    String basePath = request.getContextPath() + request.getServletPath();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>User Registration Confirmation</title>
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
<h1>User Registration Confirmation</h1>
<em></em>
<br/>
<p>
<big><big>
Congratulations! You have successfully registered with Social.com</p>
</p>
<p>
Please check your personal <a href="<%= basePath %>social/accounts"> page</a> next.
</p>
</big></big>
</div>
</body>
</html>
