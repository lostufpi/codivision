<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<head>
<link
	href="<c:url value="/vendor/jstree/themes/default/style.min.css" />"
	rel="stylesheet">
<link href="<c:url value="/css/painel.css" />" rel="stylesheet">
<meta name="decorator" content="/decorators/none.jsp" />
</head>

<body>



	<div class="row" id="body">

		<!-- Panel repository chart -->

		<div class="panel panel-default" id="nopadmag">
			<div class="panel-body" style="height: 100%;">

				<h3 style="display: inline-block">Painel do repositório
					${repository.name}</h3>
				<c:choose>
					<c:when test="${repository.haveGameId()==false}">
						<button style="margin-top: 15px; margin-left: 15px; width: 100px;"
							class="btn btn-lg btn-success pull-right" data-toggle="modal"
							data-target="#start">
							<i class="glyphicon glyphicon-play"></i>
						</button>
						<jsp:include page="painel-start-modal.jsp" />
					</c:when>
					<c:otherwise>
							<button style="margin-top: 15px; margin-left: 15px;"
							class="btn btn-lg btn-primary pull-right" data-toggle="modal"
							data-target="#config">
							<i class="glyphicon glyphicon-cog"></i>
						</button>
						<jsp:include page="painel-conf-modal.jsp" />
					</c:otherwise>
				</c:choose>

				<button style="margin-top: 15px; margin-left: 15px;"
					class="btn btn-lg btn-primary pull-right" data-toggle="modal"
					data-target="#config">
					<i class="glyphicon glyphicon-refresh"></i>
				</button>
				<jsp:include page="../repository/chart-config-modal.jsp" />
				<a href="${linkTo[RepositoryController].show(repository.id)}"><button
						style="margin-top: 15px; margin-left: 15px;"
						class="btn btn-lg btn-primary pull-right">
						<i class="glyphicon glyphicon-home"></i>
					</button></a>
				<hr>

				<div class="row vdivided" style="height: 82%;">
				<c:choose>
					<c:when test="${repository.haveGameId()==false}">
						<p style="text-align: center; margin-top: 50px;">
							A gamificação está desativada para esse projeto, ative-a no botão de iniciar
						</p>
					</c:when>
					<c:otherwise>
						<div class="container-tree col-md-4" style="height: 100%; width: 20%; margin: 10px;">
							<div style="overflow: auto;">
								<div style="margin-bottom: 20px; font-weight: bold;">
									Autor
									<div style="float:right;"> Medalhas</div>
								</div>
								<c:forEach items="${authors}" var="member"
									varStatus="s">
									<div>
										${s.index + 1}
										${member.name}
										<c:choose>
											<c:when test="${member.getDmedal()!=0}">
												<div style="margin-left: 5px; height: 20px; width: 20px; background-color: #e1e1e1; color: #000000bb; float:right; font-size: 25px; padding-left: 5px">${member.dmedal}</div>
											</c:when>
										</c:choose>
										<c:choose>
											<c:when test="${member.getGmedal()!=0}">
												<div style="margin-left: 5px; height: 20px; width: 20px; background-color: #ffd33c; color: #000000bb; float:right; font-size: 25px; padding-left: 5px">${member.gmedal}</div>
											</c:when>
										</c:choose>
										<c:choose>
											<c:when test="${member.getSmedal()!=0}">
												<div style="margin-left: 5px; height: 20px; width: 20px; background-color: #b7b7b7; color: #000000bb; float:right; font-size: 25px; padding-left: 5px">${member.smedal}</div>
											</c:when>
										</c:choose>
										<c:choose>
											<c:when test="${member.getBmedal()!=0}">
												<div style="margin-left: 5px; height: 20px; width: 20px; background-color: #a17419; color: #000000bb; float:right; font-size: 25px; padding-left: 5px">${member.bmedal}</div>
											</c:when>
										</c:choose>
									</div>
									<div style=" margin: 15px 0px; width: 100%; height: 1px; background-color: #e0e0e0ee;"></div>
								</c:forEach>	
							</div>
						</div>
						<div class="col-md-8 text-center">
							<div class="carousel">a</div>
							<div class="carousel">b</div>
							<div class="carousel">c</div>
							<div class="carousel">d</div>
							<div class="carousel">e</div>
							<div class="carousel">f</div>
							<div class="carousel">h</div>
						</div>
					</c:otherwise>
				</c:choose>
				</div>

			</div>
		</div>
		<div class="dateAtt">
			<c:choose>
				<c:when test="${repository.lastUpdate==null}">Status do Repositório</c:when>
				<c:otherwise>
					<fmt:message key="repository.update.date" />
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${repository.lastUpdate==null}">Atualizando</c:when>
				<c:otherwise>
					<fmt:formatDate pattern="dd/MM/yyyy"
						value="${repository.lastUpdate}" />
				</c:otherwise>
			</c:choose>
		</div>

	</div>



	<input type="hidden" id="repository" value="${repository.id}" />
	<input type="hidden" id="authors" value="${authors}" />

	<script src="<c:url value="/vendor/jstree/jstree.min.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/highcharts.js" />"></script>
	<script
		src="<c:url value="/vendor/highcharts/modules/no-data-to-display.js" />"></script>
	<script
		src="<c:url value="/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js" />"></script>
	<script
		src="<c:url value="/vendor/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js" />"></script>
		
	<script>
	var slideIndex = 0;
	carousel();

	function carousel() {
	    var i;
	    var x = document.getElementsByClassName("carousel");
	    for (i = 0; i < x.length; i++) {
	      x[i].style.display = "none";
	    }
	    slideIndex++;
	    if (slideIndex > x.length) {slideIndex = 1}
	    x[slideIndex-1].style.display = "block";
	    setTimeout(carousel, 2000); // Change image every 2 seconds
	}
	</script>

</body>

