<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<body>

	<div class="container">
	
		<div class="panel panel-default panel-login">
			<form action="${linkTo[EmailController].redefineAccount()}" method="post">
				
				<input type ="hidden" value="${loginUser}" name="loginUser">
				
				<h2 class="text-center"><fmt:message key="new.password" /></h2>

				<hr>
				
				<div class="form-group">
					<label for="user"><fmt:message key="user.password" />:</label> <input type="password"
						class="form-control" id="password" name="password" required>
				</div>

				<div class="form-group">
					<label for="user"><fmt:message key="retype_the_password" />:</label> <input type="password"
						class="form-control" id="password" name="checkPassword" required>
				</div>

				<button type="submit" class="btn btn-primary btn-block"><fmt:message key="save" /></button>

			</form>
		</div>

	</div>
	<!-- /container -->

</body>