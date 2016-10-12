<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>MyIslands</title>
	<link href="<c:url value="/vendor/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">
	<link href="<c:url value="/css/main.css" />" rel="stylesheet">
</head>


<body>

	<div class="jumbotron">
		<div class="container text-center">
			<h1>MyIslands</h1>
			<p class="lead">
				Uma ferramenta pra identificação de ilhas de conhecimento <br> a partir
				de informações extraídas de repositórios de software.
			</p>
		</div>
	</div>

	<div class="container">
		<h1>Como funciona:</h1>
		<div class="row text-center">
			<div class="col-lg-4">
				<h3>1. Adicionar um repositório</h3>
				<p>Adicione um repositório</p>
			</div>
			<div class="col-lg-4">
				<h3>2. Extração das informações</h3>
				<p>Serão extraídas as informações referentes às alterações
					realizadas por cada membro da equipe de desenvolvimento em cada
					arquivo do projeto de desenvolvimento.</p>
			</div>
			<div class="col-lg-4">
				<h3>3. Visualização das ilhas</h3>
				<p>É exibido ao usuário todo a hierarquia de arquivos do projeto
					e coforme são exploradas os diretórios e arquivos são exibidos ao
					usuário gráficos que informam a porcentagem de alterações
					realizadas por cada desenvolvedor no diretório ou arquivo
					selecionado.</p>
			</div>
		</div>
	</div>

	<script src="<c:url value="/vendor/jquery/jquery.min.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap/js/bootstrap.min.js" />"></script>

</body>

</html>