<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<body>

	<div class="container">

		<div class="panel panel-default panel-login">
			<form action="${linkTo[EmailController].recover()}" method="post">

				<h2 class="text-center"><fmt:message key="retrieve.account" /></h2>
				
				<hr>

				<div class="form-group">
					<label for="user"><fmt:message key="user.email" />:</label> <input type="email"
						class="form-control" id="email" name="email" required>
				</div>

				<button type="submit" class="btn btn-primary btn-block"><fmt:message key="retrieve" /></button>

			</form>
		</div>

	</div>
	<!-- /container -->

</body>