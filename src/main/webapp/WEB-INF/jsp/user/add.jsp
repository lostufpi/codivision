<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<body>

	<div class="container">
	
		<div class="panel panel-default panel-login">
			<form action="${linkTo[UserController].add}" method="post">

				<h2 class="text-center"><fmt:message key="signup" /></h2>
				
				<hr>
				
				<div class="form-group">
					<label for="name"><fmt:message key="user.name" />:</label> <input type="text"
						class="form-control" id="name" name="user.name" required placeholder="Nome">
				</div>

				<div class="form-group">
					<label for="user"><fmt:message key="user.login" />:</label> <input type="text"
						class="form-control" id="user" name="user.login" required placeholder="Usuário">
				</div>
				
				<div class="form-group">
					<label for="user"><fmt:message key="user.email" />:</label> <input type="email"
						class="form-control" id="user" name="user.email" required placeholder="E-mail">
				</div>

				<div class="form-group">
					<label for="pwd"><fmt:message key="user.password" />:</label> <input type="password"
						class="form-control" id="pwd" name="user.password" required placeholder="Senha">
				</div>
				
				<div class="form-group">
					<label for="pwd"><fmt:message key="user.password.repeat" />:</label> <input type="password"
						class="form-control" id="check_pwd" name="checkPassword" required placeholder="Digite a senha novamente"
						>
				</div>
				
				<button type="submit" class="btn btn-primary btn-block"><fmt:message key="signup" /></button>

			</form>
		</div>

	</div>
	<!-- /container -->

</body>

