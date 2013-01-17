<%@ page import="javax.servlet.http.HttpServletRequest,oauth.service.UserAccount,oauth.common.Calendar,oauth.common.CalendarEntry" %>
<%
    String basePath = request.getContextPath() + request.getServletPath();
    UserAccount account = (UserAccount)request.getAttribute("account");
    Calendar calendar = account.getCalendar();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>User Account</title>
    <STYLE TYPE="text/css">
	<!--
	  table { empty-cells: show; }
	-->
</STYLE>
</head>
<body>
<h1>User Account</h2>
<br/>
<h2>Login Information:</h2>
<br/>
<table border="1">
<tr>
<td>Login:</td><td><%= account.getName() %></td>
</tr>
<tr>
<td>Password:</td><td><%= account.getPassword() %></td>
</tr>
</table>
<br/>

<h2>Calendar:</h2>
<br/>
<table border="1">
    <tr><th>Hour</th><th>Event</th></tr> 
    <%
       for (CalendarEntry entry : calendar.getEntries()) {
    %>
       <tr>
           <td><%= entry.getHour() %></td>
           <td><%= entry.getEventDescription() %></td>
       </tr>
    <%   
       }
    %> 
</table>

<br/>
<p>
Our partner, Restaurant Reservations, is offering an online service which you can use to reserve a dinner at your favourite restaurant.
</p>
<p>
Please follow this <a href="<%= basePath %>forms/reservation.jsp">link</a> to find out more.
</p>
</body>
</html>
