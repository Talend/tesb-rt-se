<%@ page import="javax.servlet.http.HttpServletRequest, oauth2.common.ReservationConfirmation" %>

<%
    ReservationConfirmation reserve = (ReservationConfirmation) request.getAttribute("data");
    String basePath = request.getContextPath() + request.getServletPath();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Restaurant Reservation Confirmation</title>
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
<h1>Restaurant Reservation Confirmation</h1>
<em></em>
<p>
<big><big>
Here are the reservation details:
</big></big>
</p>
<table>
<tr>
<td><big><big><big>Address:</big></big></big></td>
<td><big><big><%= reserve.getAddress() %></big></big></td>
<tr>
<td><big><big><big>Time:</big></big></big></td>
<td><big><big><%= reserve.getHour() %> p.m</big></big></td>
<tr>
</table>
<br/>
<p>
<big><big>
<%
  if (reserve.isCalendarUpdated()) {
%>
Please verify your personal <a href="<%= basePath %>social/accounts">calendar</a> has been updated.

<%
  } else {
%> 

We have not been able to update your <a href="<%= basePath %>social/accounts">calendar</a>
<p/> with the reservation details. Please record them yourself.
 
<%
  }
%> 
</p>
<br/>
<p>
Back to <a href="<%= basePath %>forms/reservation.jsp">reservations</a>.
</p>
</big></big>
</div>
</body>
</html>
