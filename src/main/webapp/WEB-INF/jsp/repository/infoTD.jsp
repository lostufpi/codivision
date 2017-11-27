<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<head>
<link
	href="<c:url value="/vendor/jstree/themes/default/style.min.css" />"
	rel="stylesheet">
</head>

<body>

	<div class="row">

		
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-body">
					<div id="files">
					
					</div>
				
				</div>
		    </div>
		</div>
	</div>

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
							<div style="overflow: auto; height: 600px;" id="jstree"></div>
						</div>
						<div class="col-md-9" id="data">
							<div class="panel-body">
								<ul class="nav nav-tabs">
									<li class="active"><a data-toggle="tab" href="#home">
											Classe</a></li>
									<li><a data-toggle="tab" href="#menu1">Métodos</a></li>
								</ul>
								<div class="tab-content">
									<br />
									<div id="home" class="tab-pane fade in active">
										<br />
									</div>
									<div id="menu1" class="tab-pane fade">
										<div class="container-tree col-md-5">
											<br />
											<div id="jstree2" style="overflow: auto; height: 550px;"></div>
										</div>
										<div class="col-md-7" id="data2"></div>
									</div>
								</div>
							</div>



						</div>
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
				.ready(function() {

							var repository = $('#repository').val();
							
							var fileId;

							$('#jstree').on(
									"changed.jstree",
									function(e, data) {
										var fileName = "/"
												+ data.instance.get_path(
														data.selected[0], "/");
										request(fileName);
									});
							
							$('#jstree2').on(
									"changed.jstree",
									function(e, data) {
										if(data.node !== undefined){
											requestMethod(data.node);
										}
									});
							
							
							function requestMethod(node){
								
								
								$.ajax({
									type : 'POST',
									url : '/codivision/repository/'
											+ repository + '/file/'+fileId+'/method/td',
									data : {
										'methodName' : node.text
									},
									success : function(data) {

								
									var smell = "";
									for (i = 0; i < data.codeSmells.length; i++) {
										smell += data.codeSmells[i].codeSmellType
												+ " ";
									}

									if (smell === "") {
										smell = "Método não possui code smells"
									}

									var metrics = "";
									for (i = 0; i < data.codeMetrics.length; i++) {
										metrics += "<tr><td>"
												+ data.codeMetrics[i].metricType
												+ " </td> <td> "
												+ data.codeMetrics[i].qnt
												+ "</td></tr>";
									}


									var text = ' <div>'
											+ ' <p> <strong>Nome</strong>: '
											+ data.name.split("(")[0]
											+ ' </p> '
											+ ' <p> <strong>CodeSmell</strong>: '
											+ smell
											+ ' </p> '
											+ ' <table class="table"> '
											+ ' <thead> <tr> <th>Métrica de Código</th> '
											+ ' <th>Valor</th> </tr> </thead> '
											+ ' <tbody>'
											+ metrics
											+ ' </tbody> '
											+ ' </table> </div> '
											;

									document.getElementById("data2").innerHTML = text;
									}
								});
								
							}
							
							
							function request(fileName) {
								$.ajax({
											type : 'POST',
											url : '/codivision/repository/'
													+ repository + '/td',
											data : {
												'fileName' : fileName
											},
											success : function(data) {

												var name = data.path.split("/")[data.path
														.split("/").length - 1];

												var smell = "";
												for (i = 0; i < data.codeSmells.length; i++) {
													smell += data.codeSmells[i].codeSmellType
															+ " ";
												}

												if (smell === "") {
													smell = "Classe não possui code smells"
												}

												var metrics = "";
												for (i = 0; i < data.codeMetrics.length; i++) {
													metrics += '<tr><td>'
															+ data.codeMetrics[i].metricType
															+ ' </td> <td> '
															+ data.codeMetrics[i].qnt
															+ "</td></tr>";
												}


												var text = ' <div>'
														+ ' <p> <strong>Nome</strong>: '
														+ name
														+ ' </p> '
														+ ' <p> <strong>Diretório</strong>: '
														+ data.path
														+ ' </p> '
														+ ' <p> <strong>CodeSmell</strong>: '
														+ smell
														+ ' </p> '
														+ ' <p> <strong>Quantidade de CodeSmells de comentários</strong>: '
														+ data.qntBadSmellComment
														+ ' </p> '
														+ ' <table class="table"> '
														+ ' <thead> <tr> <th>Métrica de Código</th> '
														+ ' <th>Valor</th> </tr> </thead> '
														+ ' <tbody>'
														+ metrics
														+ ' </tbody> '
														+ ' </table> </div> '
														;

												document.getElementById("home").innerHTML = text;
												
												fileId = data.id;

												$.ajax({
													type : 'POST',
													url : '/codivision/repository/'+repository+'/method/' + fileId + '/tree',
													success : function(treeData) {
														
														

														$('#jstree2').jstree(
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
														
														
														$('#jstree2').jstree(true).settings.core.data = treeData;
														$('#jstree2').jstree(true).refresh();

														
													}

												});
											
											
											}
										});

							}

							$.ajax({
								type : 'POST',
								url : '/codivision/repository/' + repository + '/tree/td',
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
							
							
							$.ajax({
								type : 'POST',
								url : '/codivision/repository/' + repository + '/files/criticality',
								success : function(data) {
									
									var core = "";
									for (i = 0; i < data.length; i++) {
										core += '<tr><td>'
												+ data[i].path
												+ ' </td> <td> '
												+ data[i].acoplamento
												+ '</td><td>'
												+ data[i].complexidade
												+ ' </td><td>'
												+ data[i].qntTD
												+ ' </td><td>'
												+ data[i].gc
												+ ' </td></tr>';
									}

									var table =  ' <table class="table"> '
									+ ' <thead> <tr> <th>Nome do Arquivo</th> '
									+ ' <th>Acoplamento</th>' 
									+ ' <th>Complexidade</th> '
									+ ' <th>Quantidade de DTs</th> '
									+ ' <th>Grau de Criticidade</th> '
									+ '</tr> </thead> '
									+ ' <tbody>'
									+ core
									+ ' </tbody> '
									+ ' </table> </div> '
									
									document.getElementById("files").innerHTML = table;
									
								}

							});

						});
	</script>

</body>
