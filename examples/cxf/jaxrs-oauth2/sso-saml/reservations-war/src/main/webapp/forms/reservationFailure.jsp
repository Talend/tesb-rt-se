<%@ page import="javax.servlet.http.HttpServletRequest, oauth2.common.ReservationFailure" %>

<%
    ReservationFailure reserve = (ReservationFailure) request.getAttribute("data");
    String basePath = request.getContextPath() + request.getServletPath();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Restaurant Failure</title>
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
<h1>Restaurant Reservation Failure Report</h1>
<em></em>
<p>
<big><big>
<%= reserve.getMessage() %>
</big></big>
</p>
<br/>
<big>
Back to <a href="<%= basePath %>forms/reservation.jsp">reservations</a>.
</big> 
</div>

</body>
</html>
