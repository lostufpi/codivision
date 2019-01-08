<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<head>
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
				<div class="button-content">
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
							data-target="#conf">
							<i class="glyphicon glyphicon-cog"></i>
						</button>
						<jsp:include page="painel-conf-modal.jsp" />
					</c:otherwise>
				</c:choose>

				<form method="post" action="${linkTo[RepositoryController].update(repository.id)}"><button style="margin-top: 15px; margin-left: 15px;"
					class="btn btn-lg btn-primary pull-right">
					<i class="glyphicon glyphicon-refresh"></i>
				</button></form>
				<jsp:include page="../repository/chart-config-modal.jsp" />
				<a href="${linkTo[RepositoryController].show(repository.id)}"><button
						style="margin-top: 15px; margin-left: 15px;"
						class="btn btn-lg btn-primary pull-right">
						<i class="glyphicon glyphicon-home"></i>
					</button></a>
				</div>
				<hr>

				<div class="data-center" style="height: 82%;">
				<c:choose>
					<c:when test="${repository.haveGameId()==false}">
						<p style="text-align: center; margin-top: 50px; width:100%;">
							A gamificação está desativada para esse projeto, ative-a no botão de iniciar
						</p>
					</c:when>
					<c:otherwise>
						<div class="container-tree col-md-4" style="height: 100%; width: 25%; margin: 10px; box-sizing: border-box;">
							<div style="overflow-y: auto; overflow-x: hidden; ">
								<div style="margin-bottom: 20px; font-weight: bold;">
									Autor
									<div style="float:right;"> Medalhas</div>
								</div>
								<c:forEach items="${authors}" var="member"
									varStatus="s">
									<div class="medals">
										${s.index + 1}
										${member.name}
										<c:choose>
											<c:when test="${member.getDmedal()!=0}">
												<div><div style="background-color: #e1e1e1;" class="bg"></div><p>${member.dmedal}</p></div>
											</c:when>
										</c:choose>
										<c:choose>
											<c:when test="${member.getGmedal()!=0}">
												<div><div style="background-color: #ffd33c;" class="bg"></div><p>${member.gmedal}</p></div>
											</c:when>
										</c:choose>
										<c:choose>
											<c:when test="${member.getSmedal()!=0}">
												<div><div style="background-color: #b7b7b7;" class="bg"></div><p>${member.smedal}</p></div>
											</c:when>
										</c:choose>
										<c:choose>
											<c:when test="${member.getBmedal()!=0}">
												<div><div style="background-color: #a17419;" class="bg"></div><p>${member.bmedal}</p></div>
											</c:when>
										</c:choose>
									</div>
									<div style=" margin: 15px 0px; width: 100%; height: 1px; background-color: #e0e0e0ee;"></div>
								</c:forEach>	
							</div>
						</div>
						
						<script src="https://code.highcharts.com/highcharts.js"></script>
						<script src="https://code.highcharts.com/modules/data.js"></script>
						<script src="https://code.highcharts.com/modules/exporting.js"></script>
						<script src="https://code.highcharts.com/modules/export-data.js"></script>
						<div class="cont-center">
							<div class="carousel">
								<div id="chart-one" style="min-width: 310px; height: 500px; margin: 0 auto"></div>
								<table id="datatable-one" hidden="true">
								    <thead>
								        <tr>
								            <th></th>
								            <th>Código</th>
								            <th>Teste</th>
								        </tr>
								    </thead>
								    <tbody>
								    <c:forEach items="${authors}" var="member">
								 		<tr>
								            <th>${member.name}</th>
								            <td>${member.getLastGamePointParam(member.gpValid).numberOfLinesCode}</td>
								            <td>${member.getLastGamePointParam(member.gpValid).numberOfLinesTest}</td>
								        </tr>
								    </c:forEach>
								    </tbody>
								</table>
							</div>
							<div class="carousel">
								<div id="chart-two" style="min-width: 310px; height: 500px; margin: 0 auto"></div>					
								<table id="datatable-two" hidden="true">
								    <thead>
								        <tr>
								            <th></th>
								            <th>Medalhas de ouro</th>
								        </tr>
								    </thead>
								    <tbody>
								    <c:forEach items="${authors}" var="member">
								 		<tr>
								            <th>${member.name}</th>
								            <td>${member.gmedal}</td>
								        </tr>
								    </c:forEach>
								    </tbody>
								</table>
							</div>
							<div class="carousel">			
								<div id="chart-three" style="min-width: 310px; height: 500px; margin: 0 auto"></div>					
								<table id="datatable-three" hidden="true">
								    <thead>
								        <tr>
								            <th></th>
								            <th>Código</th>
								            <th>Teste</th>
								        </tr>
								    </thead>
								    <tbody>
								    <c:forEach items="${authors}" var="member">
								 		<tr>
								            <th>${member.name}</th>
								            <td>${member.numberOfLinesCode}</td>
								            <td>${member.numberOfLinesTest}</td>
								        </tr>
								    </c:forEach>
								    </tbody>
								</table>
							</div>
							<div class="carousel">
								<div id="chart-four" style="min-width: 310px; height: 500px; margin: 0 auto"></div>					
								<table id="datatable-four" hidden="true">
								    <thead>
								        <tr>
								            <th></th>
								            <th>Medalhas de prata</th>
								        </tr>
								    </thead>
								    <tbody>
								    <c:forEach items="${authors}" var="member">
								 		<tr>
								            <th>${member.name}</th>
								            <td>${member.smedal}</td>
								        </tr>
								    </c:forEach>
								    </tbody>
								</table>
							</div>
							<div class="carousel">
								<div id="chart-five" style="min-width: 310px; height: 500px; margin: 0 auto"></div>					
								<table id="datatable-five" hidden="true">
								    <thead>
								        <tr>
								            <th></th>
								            <th>Medalhas de bronze</th>
								        </tr>
								    </thead>
								    <tbody>
								    <c:forEach items="${authors}" var="member">
								 		<tr>
								            <th>${member.name}</th>
								            <td>${member.bmedal}</td>
								        </tr>
								    </c:forEach>
								    </tbody>
								</table>
							</div>
							<div class="carousel">
								<div id="chart-six" style="min-width: 310px; height: 500px; margin: 0 auto"></div>					
								<table id="datatable-six" hidden="true">
								    <thead>
								        <tr>
								            <th></th>
								            <th>Medalhas</th>
								        </tr>
								    </thead>
								    <tbody>
								    <c:forEach items="${authors}" var="member">
								 		<tr>
								            <th>${member.name}</th>
								            <td>${member.bmedal + member.smedal + member.gmedal}</td>
								        </tr>
								    </c:forEach>
								    </tbody>
								</table>
							</div>
							<div class="carousel">
								<div id="chart-seven" style="min-width: 310px; height: 500px; margin: 0 auto"></div>					
								<table id="datatable-seven" hidden="true">
								    <thead>
								        <tr>
								            <th></th>
								            <th>Ciclos</th>
								        </tr>
								    </thead>
								    <tbody>
								    <c:forEach items="${authors}" var="member">
								 		<tr>
								            <th>${member.name}</th>
								            <td>${member.gpValid + 1}</td>
								        </tr>
								    </c:forEach>
								    </tbody>
								</table>
							</div>
						</div>
					</c:otherwise>
				</c:choose>
				</div>

			</div>
		</div>
		<div class="dateAtt">
			<c:choose>
				<c:when test="${repository.lastUpdateFromGit==null}">Status do Repositório</c:when>
				<c:otherwise>
					<fmt:message key="repository.update.date" />
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${repository.lastUpdateFromGit==null}">Atualizando</c:when>
				<c:otherwise>
					<fmt:formatDate pattern="dd/MM/yyyy"
						value="${repository.lastUpdateFromGit}" />
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
	    setTimeout(carousel, 5000); // Change image every 2 seconds
	}
	$(document).ready(function(){
		 $('#chart-one').highcharts({
		    data: {
		        table: 'datatable-one'
		    },
		    colors: ['rgb(124, 181, 236)', '#00ff00'],
		    chart: {
		        type: 'column',
		        
		    },
		    title: {
		        text: 'Quantidade de linhas produzidas no último ciclo'
		    },
		    yAxis: {
		        allowDecimals: false,
		        title: {
		            text: 'Quantidade de linhas'
		        }
		    },
		    tooltip: {
		        formatter: function () {
		            return '<b>' + this.series.name + '</b><br/>' +
		                this.point.y + ' ' + this.point.name.toLowerCase();
		        }
		    }
		});
		 
		$('#chart-two').highcharts({
			    data: {
			        table: 'datatable-two'
			    },
			    chart: {
			        type: 'column'
			    },
			    colors: ['#ffd33c'],
			    title: {
			        text: 'Quantidade de medalhas de ouro em todo projeto'
			    },
			    yAxis: {
			        allowDecimals: false,
			        title: {
			            text: 'Quantidade de medalhas'
			        }
			    },
			    tooltip: {
			        formatter: function () {
			            return '<b>' + this.series.name + '</b><br/>' +
			                this.point.y + ' ' + this.point.name.toLowerCase();
			        }
			    }
			});
		 
		$('#chart-three').highcharts({
			    data: {
			        table: 'datatable-three'
			    },
			    colors: ['rgb(124, 181, 236)', '#00ff00'],
			    chart: {
			        type: 'column'
			    },
			    title: {
			        text: 'Quantidade de linhas produzidas em todo projeto'
			    },
			    yAxis: {
			        allowDecimals: false,
			        title: {
			            text: 'Quantidade de linhas'
			        }
			    },
			    tooltip: {
			        formatter: function () {
			            return '<b>' + this.series.name + '</b><br/>' +
			                this.point.y + ' ' + this.point.name.toLowerCase();
			        }
			    }
			});
		
		$('#chart-four').highcharts({
		    data: {
		        table: 'datatable-four'
		    },
		    colors: ['#b7b7b7'],
		    chart: {
		        type: 'column'
		    },
		    title: {
		        text: 'Quantidade de medalhas de prata em todo projeto'
		    },
		    yAxis: {
		        allowDecimals: false,
		        title: {
		            text: 'Quantidade de medalhas'
		        }
		    },
		    tooltip: {
		        formatter: function () {
		            return '<b>' + this.series.name + '</b><br/>' +
		                this.point.y + ' ' + this.point.name.toLowerCase();
		        }
		    }
		});
		
		$('#chart-five').highcharts({
		    data: {
		        table: 'datatable-five'
		    },
		    colors: ['#a17419'],
		    chart: {
		        type: 'column'
		    },
		    title: {
		        text: 'Quantidade de medalhas de bronze em todo projeto'
		    },
		    yAxis: {
		        allowDecimals: false,
		        title: {
		            text: 'Quantidade de medalhas'
		        }
		    },
		    tooltip: {
		        formatter: function () {
		            return '<b>' + this.series.name + '</b><br/>' +
		                this.point.y + ' ' + this.point.name.toLowerCase();
		        }
		    }
		});

		
		$('#chart-six').highcharts({
		    data: {
		        table: 'datatable-six'
		    },
		    colors: ['#e1e1e1'],
		    chart: {
		        type: 'column'
		    },
		    title: {
		        text: 'Quantidade de medalhas em todo projeto'
		    },
		    yAxis: {
		        allowDecimals: false,
		        title: {
		            text: 'Quantidade de medalhas'
		        }
		    },
		    tooltip: {
		        formatter: function () {
		            return '<b>' + this.series.name + '</b><br/>' +
		                this.point.y + ' ' + this.point.name.toLowerCase();
		        }
		    }
		});
		 
		$('#chart-seven').highcharts({
		    data: {
		        table: 'datatable-seven'
		    },
		    colors: ['#e1e1e1'],
		    chart: {
		        type: 'column'
		    },
		    title: {
		        text: 'Quantidade de ciclos de gamification no projeto'
		    },
		    yAxis: {
		        allowDecimals: false,
		        title: {
		            text: 'Quantidade de ciclos'
		        }
		    },
		    tooltip: {
		        formatter: function () {
		            return '<b>' + this.series.name + '</b><br/>' +
		                this.point.y + ' ' + this.point.name.toLowerCase();
		        }
		    }
		});
	});
	</script>

</body>

