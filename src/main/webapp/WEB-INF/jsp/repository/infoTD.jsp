<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<head>
<link
	href="<c:url value="/vendor/jstree/themes/default/style.min.css" />"
	rel="stylesheet">
</head>

<body>

	<div class="row">

		<!-- Panel repository chart -->
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-body">

					<h3 style="display: inline-block">Informações sobre Dívida
						Técnica no Repositório ${repository.name}</h3>
					<button style="margin-top: 15px"
						class="btn btn-lg btn-primary pull-right" data-toggle="modal"
						data-target="#config">
						<i class="glyphicon glyphicon-cog"></i>
					</button>
					<%-- 					<jsp:include page="chart-config-modal.jsp" /> --%>

					<hr>

					<div class="row vdivided">
						<div class="container-tree col-md-3">
							<input style="margin-bottom: 10px" type="text"
								class="form-control" id="jstree-search" placeholder="Pesquisar">
							<div id="jstree"></div>
						</div>
						<div class="col-md-9" id="data"></div>
					</div>
				</div>


			</div>
		</div>
	</div>



	<input type="hidden" id="repository" value="${repository.id}" />

	<script src="<c:url value="/vendor/jstree/jstree.min.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/highcharts.js" />"></script>
	<script
		src="<c:url value="/vendor/highcharts/modules/no-data-to-display.js" />"></script>
	<script
		src="<c:url value="/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js" />"></script>
	<script
		src="<c:url value="/vendor/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js" />"></script>



	<script>
		$(document)
				.ready(
						function() {

							var repository = $('#repository').val();

							$('#jstree').on(
									"changed.jstree",
									function(e, data) {
										var fileName = "/"
												+ data.instance.get_path(
														data.selected[0], "/");
										request(fileName);
									});

							function request(fileName) {
								$
										.ajax({
											type : 'POST',
											url : '/codivision/repository/'
													+ repository + '/td',
											data : {
												'fileName' : fileName
											},
											success : function(data) {
												
												var name = data.path.split("/")[data.path.split("/").length - 1];
												
												var smell = "";
												for(i = 0; i < data.codeSmells.length; i++){
													smell += data.codeSmells[i].codeSmellType+" ";
												}
												
												if(smell === ""){
													smell = "Classe não possui code smells"
												}
												
												var metrics = "";
												for(i = 0; i < data.codeMetrics.length; i++){
													metrics += "<tr><td>" + data.codeMetrics[i].metricType+" </td> <td> "+ data.codeMetrics[i].qnt + "</td></tr>";
												}


												
												var text = '<div class="panel-body"> '
												+' <ul class="nav nav-tabs"> '+
												' <li class="active"> '+
												' <a data-toggle="tab" href="#home">'+
												'Classe</a></li> <li><a data-toggle="tab" href="#menu1">Métodos</a></li> </ul> <div class="tab-content"> '+
												' <div id="home" class="tab-pane fade in active"> <br/> '+
												' <p> <strong>Nome</strong>: '+name+' </p> '+
												' <p> <strong>Diretório</strong>: '+data.path+' </p> '+
												' <p> <strong>CodeSmell</strong>: '+smell+' </p> '+
												' <table class="table"> '+
												' <thead> <tr> <th>Métrica de Código</th> '+
												' <th>Valor</th> </tr> </thead> '+
												' <tbody>'+ metrics +
												' </tbody> '+
												' </table> </div> <div id="menu1" class="tab-pane fade"> </div> </div> </div>';

												document.getElementById("data").innerHTML = text;
											
											}
										});

							}

							$.ajax({
								type : 'POST',
								url : '/codivision/repository/' + repository
										+ '/tree/td',
								success : function(treeData) {

									$('#jstree').jstree(
											{
												'core' : {
													'data' : treeData
												},
												'types' : {
													"FOLDER" : {
														"valid_children" : [
																"FOLDER",
																"FILE" ]
													},
													"FILE" : {
														"icon" : "jstree-file",
														"valid_children" : [],
													}
												},
												'plugins' : [ "types",
														"wholerow", "sort",
														"search" ]
											});

									request("/");
								}

							});

						});
	</script>

</body>
