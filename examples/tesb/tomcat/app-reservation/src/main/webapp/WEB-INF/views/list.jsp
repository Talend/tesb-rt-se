<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Rent-a-Car (Web) - Car List</title>
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/style.css">

<script src="js/jquery.min.js"></script>
</head>
<body>
	<div class="container">
		<h1>Rent-a-Car (Web)</h1>

		<form:form action="back" method="POST">
			<input type="submit" title="Back" value="Back"
				class="btn btn-default" />
		</form:form>

		<form:form action="reserve" method="POST">
			<p class="h5">
				For the given date range we can offer you the following cars and
				conditions.<br />Please select the car you like to book in the
				table and click on 'Reserve'.
			</p>
			<table class="table table-hover">
				<thead>
					<tr>
						<th></th>
						<th>Brand</th>
						<th>Model</th>
						<th>Booking class</th>
						<th>Daily rate</th>
						<th>Weekend rate</th>
						<th>Insurance</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${cars}" var="car" varStatus="cntr">
						<tr radioBtnId="carIndex${cntr.index+1}">
							<td><form:radiobutton path="carIndex" value="${cntr.index}" /></td>
							<td>${car.brand}</td>
							<td>${car.designModel}</td>
							<td>${car.clazz}</td>
							<td>${car.rateDay}</td>
							<td>${car.rateWeekend}</td>
							<td>${car.securityGuarantee}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div>
				<input type="submit" title="Reserve" value="Reserve"
					class="btn btn-primary" />
			</div>
		</form:form>
	</div>
	<script type="text/javascript">
		var e = document.getElementById('carIndex1');
		if (e != null) {
			e.checked = true;
		}

		$(function() {
			$('tbody tr').click(function() {
				document.getElementById($(this).attr('radioBtnId')).checked=true;
			});
		});
	</script>
</body>
</html>
