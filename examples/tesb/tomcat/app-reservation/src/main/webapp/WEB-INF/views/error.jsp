<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Rent-a-Car (Web) - Data Input</title>

<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/style.css">
</head>
<body>
	<div class="container">
		<p class="lead">The backend service does not work properly, sorry.</p>
		<p>${excptMessage}</p>
		<p>
			<form:form action="done" method="POST">
				<input type="submit" value="Try again" class="btn btn-primary" />
			</form:form>
		</p>
	</div>
</body>
</html>
