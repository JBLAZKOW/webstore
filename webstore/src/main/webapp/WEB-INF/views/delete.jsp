<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet"
	href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css">
<title>Delete</title>
</head>
<body>
	<section>
		<div class="jumbotron">
			<div class="container">
				<h1>Book ${book.id} has been succesfuly deleted.</h1>
				<strong>Book title: </strong>${book.title}
				<p>
					<strong>Wrote by</strong>: ${book.authors}
				</p>
			</div>
			<p>
				<a href="<spring:url value="/books/all" />" class="btn btn-default">
					<span class="glyphicon-hand-left glyphicon"></span> back
				</a>

			</p>
		</div>
	</section>

</body>
</html>