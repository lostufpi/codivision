<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<body>

	<div class="container">
	
		<div class="panel panel-default panel-login">
			<form action=${linkTo[UserController].login } method="post">

				<h2 class="text-center"><fmt:message key="user.signin" /></h2>
				
				<hr>

				<div class="form-group">
					<label for="user"><fmt:message key="user" />:</label> <input type="text"
						class="form-control" id="user" name="user.login" required>
				</div>

				<div class="form-group">
					<label for="pwd"><fmt:message key="user.password" />:</label> <input type="password"
						class="form-control" id="pwd" name="user.password" required>
				</div>

				<div class="checkbox">
					
					 <label> <a href="${linkTo[UserController].add}"><fmt:message key="sign_up" /></a></label>
					 <label> <a href="${linkTo[UserController].forgotPassword}"><fmt:message key="user.password.forgot" /></a></label>
					
				</div>

				<button type="submit" class="btn btn-primary btn-block"><fmt:message key="signin" /></button>

			</form>
		</div>

	</div>
	<!-- /container -->

</body>