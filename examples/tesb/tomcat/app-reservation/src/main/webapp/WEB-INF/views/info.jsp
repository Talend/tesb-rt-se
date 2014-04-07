<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Rent-a-Car (Web) - Reservation Info</title>
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/style.css">
</head>
<body>

	<div class="container">
		<h1>Rent-a-Car (Web)</h1>

		<!-- 		<p> -->
		<%-- 			<form:form action="done" method="POST"> --%>
		<!-- 				<input type="submit" value="Done" class="btn btn-primary" /> -->
		<%-- 			</form:form> --%>
		<!-- 		</p> -->

		<div class="top-space">
			<p class="h5 ${resStatus}">${confirm.description}</p>
		</div>

		<div class="form-group row">
			<div class="col-xs-2">Reservation ID</div>
			<div class="col-xs-2">
				<input type="text" readonly="readonly"
					value="${confirm.reservationId}" />
			</div>
		</div>

		<div class="form-group">
			<p>
				<em>Customer details</em>
			</p>

			<div class="row">
				<div class="col-xs-1">Name</div>
				<div class="col-xs-3">
					<input type="text" readonly="readonly"
						value="${confirm.customer.name}" />
				</div>
			</div>

			<div class="row">
				<div class="col-xs-1">EMail</div>
				<div class="col-xs-3">
					<input type="text" readonly="readonly"
						value="${confirm.customer.email}" />
				</div>
			</div>

			<div class="row">
				<div class="col-xs-1">City</div>
				<div class="col-xs-4">
					<input type="text" readonly="readonly"
						value="${confirm.customer.city}" />
				</div>
			</div>

			<div class="row">
				<div class="col-xs-1">Status</div>
				<div class="col-xs-4">
					<input type="text" readonly="readonly"
						value="${confirm.customer.status}" />
				</div>
			</div>
		</div>

		<div class="form-group">
			<p>
				<em>Car details</em>
			</p>
			<div class="row">
				<div class="col-xs-1">Brand</div>
				<div class="col-xs-4">
					<input type="text" readonly="readonly" value="${confirm.car.brand}" />
				</div>
			</div>
			<div class="row">
				<div class="col-xs-1">Model</div>
				<div class="col-xs-4">
					<input type="text" readonly="readonly"
						value="${confirm.car.designModel}" />
				</div>
			</div>
		</div>

		<div class="form-group">
			<p>
				<em>Reservation details</em>
			</p>
			<div class="row">
				<div class="col-sm-1">Pick up date</div>
				<div class="col-sm-2">
					<input type="text" readonly="readonly" value="${confirm.fromDate}"
						class="form-control-static" />
				</div>
				<div class="col-sm-1">Return date</div>
				<div class="col-sm-2">
					<input type="text" readonly="readonly" value="${confirm.toDate}" />
				</div>
			</div>
			<div class="row">
				<div class="col-sm-1">Daily rate</div>
				<div class="col-sm-2">
					<input type="text" readonly="readonly"
						value="${confirm.car.rateDay}" />
				</div>
				<div class="col-sm-1">Weekend rate</div>
				<div class="col-sm-2">
					<input type="text" readonly="readonly"
						value="${confirm.car.rateWeekend}" />
				</div>
			</div>
			<div class="row">
				<div class="col-sm-1">Credits</div>
				<div class="col-sm-2">
					<input type="text" readonly="readonly"
						value="${confirm.creditPoints}" />
				</div>
			</div>
		</div>

		<form:form action="done" method="POST">
			<input type="submit" value="Done" class="btn btn-primary" />
		</form:form>
	</div>
</body>
</html>
