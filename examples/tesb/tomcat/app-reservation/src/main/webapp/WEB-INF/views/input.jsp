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

<link href="css/smoothness/jquery-ui-custom.min.css" rel="stylesheet">

<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/style.css">

<script src="js/jquery.min.js"></script>
<script src="js/jquery-ui.custom.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(function() {
		$("#pickupDate").datepicker({
			dateFormat : "dd.mm.yy"
		});
		$("#returnDate").datepicker({
			dateFormat : "dd.mm.yy"
		});
	});
</script>
</head>
<body>
	<div class="container">
		<form:form action="search" method="POST" modelAttribute="inputData"
			class="form-datainput" role="form">
			<h1>Rent-a-Car (Web)</h1>
			<div class="form-group">
				<form:label path="customerName">Customer Name</form:label>
				<div class="input-group">
					<form:input path="customerName" class="form-control" />
					<div class="input-group-btn">
						<button type="button" class="btn btn-default dropdown-toggle"
							data-toggle="dropdown">
							<span class="caret"></span>
						</button>
						<ul class="dropdown-menu pull-right">
							<c:forEach items="${knownCustomers}" var="customer">
								<li><a
									onclick="$('#customerName').val('${customer}'); return false;"
									href="#">${customer}</a></li>
							</c:forEach>
						</ul>
					</div>
				</div>
				<form:errors path="customerName" cssClass="error small" />
			</div>
			<div class="form-group">
				<form:label path="pickupDate">Pick up date</form:label>
				<form:input path="pickupDate" class="form-control" />
				<form:errors path="pickupDate" cssClass="error small" />
			</div>
			<div class="form-group">
				<form:label path="returnDate">Return date</form:label>
				<form:input path="returnDate" class="form-control" />
				<form:errors path="returnDate" cssClass="error small" />
				<form:errors path="" cssClass="error small" element="div" />
			</div>
			<input type="submit" value="Find" class="btn btn-primary" />
		</form:form>
	</div>
</body>
</html>
