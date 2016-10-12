<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<body>
	
	<div class="col-lg-3">
		<div class="panel panel-default">
			<div class="panel-body">
					<img src="${urlImage}" alt="Avatar">
					<hr>
					<form action="http://www.gravatar.com" target="blanck">
					<button type="submit" class="btn btn-default" style="padding: 5px 0px 5px 10px; width: 215px">
					<span style="background:url(../img/image.png) left no-repeat;background-size: 24px 24px; padding: 20px 25px 20px">
							Alterar Foto
						</button></form>
			</div>
		</div>
	</div>

	<div class="col-lg-9">
		<div class="row">
			<div class="panel panel-default">
				<div class="panel-body">
					
					<h3>Informações do usuário</h3>
					<hr>
					
					<form method="post" action="${linkTo[UserController].edit() }">
						
						<div class="form-group">
							<label for="name"><fmt:message key="user.name" />:</label> <input
								type="text" class="form-control" id="name" name="user.name"
								required value="${userSession.user.name}">
						</div>

						<div class="form-group">
							<label for="user"><fmt:message key="user.login" />:</label> <input
								type="text" class="form-control" id="login" name="user.login"
								required value="${userSession.user.login}">
						</div>

						<div class="form-group">
							<label for="user"><fmt:message key="user.email" />:</label> <input
								type="email" class="form-control" id="email" name="user.email"
								required value="${userSession.user.email}">
						</div>
						
						<div class="form-group">
							<label for="user"><fmt:message key="user.password.current" />:</label> <input
								type="password" class="form-control" id="password" name="user.password"
									>
						</div>
						
						<div class="form-group">
							<label for="user"><fmt:message key="user.password.new" />:</label> <input
								type="password" class="form-control" id="password" name="passwordNew"
								>
						</div>
						
						<div class="form-group">
							<label for="user"><fmt:message key="user.password.repeat" />:</label> <input
								type="password" class="form-control" id="password" name="passwordNewCheck"
								>
						</div>
						<hr>
						<button class="btn btn-primary pull-right"><fmt:message key="save" /></button>
					</form>
				</div>
			</div>
		</div>
	</div>
	
</body>