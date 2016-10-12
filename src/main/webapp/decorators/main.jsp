<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><fmt:message key="app.name"/></title>
	<link href="<c:url value="/vendor/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">
	<link href="<c:url value="/css/main.css" />" rel="stylesheet">
	<sitemesh:write property='head'/>
</head>

<body>

	<script src="<c:url value="/vendor/jquery/jquery.min.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap/js/bootstrap.min.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap/js/bootstrap-filestyle.min.js" />"></script>

	<!-- Navigation -->
    <nav class="navbar navbar-default navbar-fixed-top topnav" role="navigation">
        <div class="container topnav">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <figure class="logo">
                <a class="navbar-brand topnav" href="${linkTo[HomeController].home}"><img src="<c:url value='/img/logo.png'/>" /></a>
                </figure>
            </div>
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            	<ul class="nav navbar-nav">
				<c:choose>
					<c:when test="${not empty userSession.user}">
						<li><a href="${linkTo[RepositoryController].list }"><fmt:message key="repository.list"/></a>
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose>
				</ul>
                <ul class="nav navbar-nav navbar-right">
                    <c:choose>
					<c:when test="${not empty userSession.user}">
						<li><a href="${linkTo[UserController].profile }">${userSession.user.name}</a></li>
						<li><a href="${linkTo[UserController].logout}"><fmt:message key="exit"/></a></li>
					</c:when>
					<c:otherwise>
						<li><a href="${linkTo[HomeController].home}"><fmt:message key="home"/></a></li>
						<li><a href="${linkTo[UserController].login}"><fmt:message key="signin"/></a></li>
					</c:otherwise>
				</c:choose>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container -->
    </nav>

	<div class="container">

		<c:if test="${not empty errors}">
			<div class="alert alert-danger">
				<button type="button" class="close" data-dismiss="alert">&times;</button>
				<c:forEach items="${errors}" var="error">
					<b><fmt:message key="${error.category}" /></b> - <fmt:message
						key="${error.message}" />
					<br />
				</c:forEach>
			</div>
		</c:if>

		<c:if test="${not empty notice}">
			<div class="alert alert-success">
				<button type="button" class="close" data-dismiss="alert">&times;</button>
				<b><fmt:message key="${notice.category}" /></b> -
				<fmt:message key="${notice.message}" />
				<br />
			</div>
		</c:if>
		
		<c:if test="${not empty warning}">
			<div class="alert alert-warning">
				<button type="button" class="close" data-dismiss="alert">&times;</button>
				<b><fmt:message key="${warning.category}" /></b> -
				<fmt:message key="${warning.message}" />
				<br />
			</div>
		</c:if>

		<div id="generic-modal" class="modal fade">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title">
							<fmt:message key="confirm.delete" />
						</h4>
					</div>
					<div class="modal-body">
						<p>One fine body&hellip;</p>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">
							<fmt:message key="close" />
						</button>
						<button type="button" class="btn btn-danger"
							id="generic-modal-confirmation">
							<fmt:message key="remove" />
						</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>

		<div class="container-fluid">
			<sitemesh:write property='body'/>
		</div>

	</div>
	
	<script>
		$(document).ready(function(){
			$(document).on('click', '.confirmation-modal', function(e){
				e.preventDefault();
				var $this = $(this), 
					$modal = $('#generic-modal'),
					body = $this.data('conf-modal-body');
				if(body){
					$modal.find('.modal-body').html(body);
				}
				$modal.modal('show');
				$modal.find('#generic-modal-confirmation').unbind('click').on('click', function(e){
					document.location.href = $this.attr('href');
				});
			});
		});
	</script>

</body>

</html>