<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<head>
	<link href="<c:url value="/vendor/jstree/themes/default/style.min.css" />" rel="stylesheet">
	<link href="<c:url value="/css/painel.css" />" rel="stylesheet">
	<meta name="decorator" content="/decorators/none.jsp"/>
</head>

<body>



	<div class="row" id="body">
		
		<!-- Panel repository chart -->

			<div class="panel panel-default" id="nopadmag">
				<div class="panel-body">
				
					<h3 style="display:inline-block">Painel do repositório ${repository.name}</h3>
					<c:choose>
						<c:when test="${repository.gameId==null}">
							<button style="margin-top: 15px; margin-left: 15px; width:100px;" class="btn btn-lg btn-success pull-right" data-toggle="modal" data-target="#start" ><i class="glyphicon glyphicon-play"></i></button>
							<jsp:include page="painel-start-modal.jsp" />
							</c:when>
					<c:otherwise>
							<<button style="margin-top: 15px; margin-left: 15px;" class="btn btn-lg btn-primary pull-right" data-toggle="modal" data-target="#config" ><i class="glyphicon glyphicon-cog"></i></button>
							<jsp:include page="painel-conf-modal.jsp" />
					</c:otherwise>
					</c:choose>	
					
					<button style="margin-top: 15px; margin-left: 15px;" class="btn btn-lg btn-primary pull-right" data-toggle="modal" data-target="#config" ><i class="glyphicon glyphicon-refresh"></i></button>
					<jsp:include page="../repository/chart-config-modal.jsp" />
					<a href="${linkTo[RepositoryController].show(repository.id)}"><button style="margin-top: 15px; margin-left: 15px;" class="btn btn-lg btn-primary pull-right" ><i class="glyphicon glyphicon-home"></i></button></a>
					<hr>
					
					<div class="row vdivided">
						<div class="container-tree col-md-4">
							
							<div id="jstree"></div>
						</div>
						<div class="col-md-8 text-center">
							<div id="chart"></div>
							<p id="truckfactor-label"></p>
						</div>
					</div>

				</div>
			</div>
				<div class="dateAtt">
					<c:choose>
								<c:when test="${repository.lastUpdate==null}">Status do Repositório</c:when>
							<c:otherwise>
								<fmt:message key="repository.update.date" /></c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${repository.lastUpdate==null}">Atualizando</c:when>
							<c:otherwise>
								<fmt:formatDate pattern="dd/MM/yyyy" value="${repository.lastUpdate}" /></c:otherwise>
							</c:choose>	
				</div>

		</div>
	
	

	<input type="hidden" id="repository" value="${repository.id}" />
	
	<script src="<c:url value="/vendor/jstree/jstree.min.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/highcharts.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/modules/no-data-to-display.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js" />"></script>
	
</body>

