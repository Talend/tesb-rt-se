<%@ page import="javax.servlet.http.HttpServletRequest,oauth2.service.UserAccount,oauth2.common.Calendar,oauth2.common.CalendarEntry" %>
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
	  div.padded {  
         padding-left: 5em;  
      } 
	-->
</STYLE>
</head>
<body>
<div class="padded">
<h1>User Account</h1>
<h2>Login Information:</h2>
<br/>
<table border="1">
<tr>
<td><big><big><big>Login</big></big></big></td><td><big><big><%= account.getName() %></big></big></td>
</tr>
<tr>
<td><big><big><big>Password</big></big></big></td><td><big><big><%= account.getPassword() %></big></big></td>
</tr>
</table>
<br/>

<h2>Calendar:</h2>
<br/>
<table border="1">
    <tr><th><big><big>Hour</big></big></th><th><big><big>Event</big></big></th></tr> 
    <%
       for (CalendarEntry entry : calendar.getEntries()) {
    %>
       <tr>
           <td><big><big><%= entry.getHour() %></big></big></td>
           <td><big><big><%= entry.getEventDescription() %></big></big></td>
       </tr>
    <%   
       }
    %> 
</table>

<br/>
<p>
<big><big>
Our partner, Restaurant Reservations, is offering an online service which you can use<br/> to reserve a dinner at your favourite restaurant.
</p>
<p>
Please follow this <a href="<%= basePath %>forms/reservation.jsp">link</a> to find out more.
</p>
</big></big>
</div>
</body>
</html>
