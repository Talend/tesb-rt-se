<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%
    String basePath = request.getContextPath() + request.getServletPath();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>User Registration Confirmation</title>
</head>
<body>
<h1>User Registration Confirmation</h1>
<em></em>
<p>
Congratulations! You have successfully registered with Social.com</p>
</p>
<p>
Please check your personal <a href="<%= basePath %>social/accounts">page</a> next.
</p>
</body>
</html>
