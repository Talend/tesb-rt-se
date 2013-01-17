<%@ page import="javax.servlet.http.HttpServletRequest, oauth.common.ReservationFailure" %>

<%
    ReservationFailure reserve = (ReservationFailure) request.getAttribute("data");
    String basePath = request.getContextPath() + request.getServletPath();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Restaurant Failure</title>
</head>
<body>
<h1>Restaurant Reservation Failure Report</h1>
<em></em>
<p>
<%= reserve.getMessage() %>
</p>
<br/>
Back to <a href="<%= basePath %>forms/reservation.jsp">reservations</a>. 


</body>
</html>
