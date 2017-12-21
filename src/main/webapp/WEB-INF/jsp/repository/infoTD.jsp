<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<head>
<link
	href="<c:url value="/vendor/jstree/themes/default/style.min.css" />"
	rel="stylesheet">
</head>

<body>

	<div class="row" >
	
	
		<div id="myModal2" class="modal fade" role="dialog">
		  <div class="modal-dialog modal-lg">
		
		    <!-- Modal content-->
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Informações detalhada dos arquivos</h4>
		      </div>
		      <div class="modal-body">
		        <div class="row vdivided">
						<div class="col-md-8" id="data">
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
						<div class="col-md-4" >
							<img id="img-carregando" src="http://i.kinja-img.com/gawker-media/image/upload/chag4hzw0pgvgy5ujnom.gif" class="img-responsive" alt="Cinque Terre">
							<div id="rec"></div>
						</div>
						
					</div>
		        
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Fechar</button>
		      </div>
		    </div>
		
		  </div>
		</div>
	
	
		<div id="myModal" class="modal fade" role="dialog">
		  <div class="modal-dialog modal-lg">
		
		    <!-- Modal content-->
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Informações detalhada dos arquivos</h4>
		      </div>
		      <div class="modal-body">
		        <div id="files"></div>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Fechar</button>
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
					<button id="detalhar"  type="button" class="btn btn-info btn-md" style="float: right;" data-toggle="modal" data-target="#myModal">Detalhar</button>

					<hr>
					<div class="col-lg-12">
						
						<div class="panel-body"  id="div-carregando">
							<center>
							<img src="http://i.kinja-img.com/gawker-media/image/upload/chag4hzw0pgvgy5ujnom.gif" class="img-responsive" alt="Cinque Terre">
						</center></div>	
							<div class="panel-body" style="display: none;" id="div-detalhar">
								<div id="chart-bar"></div>
							</div>
					    
					</div>
				</div>


			</div>
		</div>
	</div>
	
	

	<input type="hidden" id="repository" value="${repository.id}" />

	<script src="<c:url value="/vendor/jstree/jstree.min.js" />"></script>
	<script src="https://code.highcharts.com/highcharts.js"></script>
	<script src="https://code.highcharts.com/modules/exporting.js"></script>
	<script
		src="<c:url value="/vendor/highcharts/modules/no-data-to-display.js" />"></script>
	<script
		src="<c:url value="/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js" />"></script>
	<script
		src="<c:url value="/vendor/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js" />"></script>



	<script>
		$(document)
				.ready(function() {
					
					
					$('#div-detalhar').hide();
					var repository = $('#repository').val();
					
					var fileId;
					
					
					$.ajax({
						type : 'POST',
						url : '/codivision/repository/' + repository + '/files/criticality/chart',
						success : function(core) {
							if(core===""){
								document.getElementById("div-detalhar").innerHTML = "<center><h3>Não foram identificados problemas de Dívidas Técnicas no projeto</h3></center>";
							}
							
							$('#div-detalhar').show();
							$('#div-carregando').hide();
							document.getElementById("rec").innerHTML = "";

							var a1 = '{ "obj":';
							var a2 = a1.concat(core);
							var a3 = a2.concat('}');
							
							var myobj = JSON.parse(a3);
							
							Highcharts.chart('chart-bar', {
							    chart: {
							        type: 'column'
							    },
							    title: {
							        text: 'Criticidade dos arquivos em relação a presença de Dívidas Técnicas'
							    },
							    subtitle: {
							        text: 'Criticidade inferida para cada arquivo levando-se em consideração o acoplamento, complexidade ciclomática e as dívidas técnicas'
							    },
							    xAxis: {
							        type: 'category',
							        labels: {
							            rotation: -45,
							            style: {
							                fontSize: '13px',
							                fontFamily: 'Verdana, sans-serif'
							            }
							        }
							    },
							    yAxis: {
							        min: 0,
							        title: {
							            text: 'Criticidade'
							        }
							    },plotOptions: {
							            series: {
							                cursor: 'pointer',
							                point: {
							                    events: {
							                        click: function() {
							                           request(this.options.name);
							                           recomendacao(this.options.name);
							                           $('#rec').hide();
							                           $('#img-carregando').show();
							                           $("#myModal2").modal();
							                        }
							                    }
							                }
							            }
							        },
							    legend: {
							        enabled: false
							    },
							    tooltip: {
							        pointFormat: 'Criticidade: <b>{point.y:.1f} </b>'
							    },
							    series: [{
							        name: 'Population',
							        data: myobj.obj,
							        dataLabels: {
							            enabled: true,
							            rotation: -90,
							            color: '#FFFFFF',
							            align: 'right',
							            format: '{point.y:.1f}', // one decimal
							            y: 10, // 10 pixels down from the top
							            style: {
							                fontSize: '13px',
							                fontFamily: 'Verdana, sans-serif'
							            }
							        }
							    }]
							});
							
					
						}
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
							
							
							
							function recomendacao(fileName){
								
								$.ajax({
									type : 'POST',
									url : '/codivision/repository/'
											+ repository + '/td/recomendation',
									data : {
										'fileName' : fileName
									},
									success : function(core) {
										
										$('#img-carregando').hide();
										$('#rec').show();
										
										var a1 = '{ "obj":';
										var a2 = a1.concat(core);
										var a3 = a2.concat('}');
										
										var myobj = JSON.parse(a3);
								
									
									Highcharts.chart('rec', {
									    chart: {
									        type: 'bar'
									    },
									    title: {
									        text: 'Recomendação para pagamento de Dívidas Técnicas'
									    },
									    subtitle: {
									        text: ''
									    },
									    xAxis: {
									        type: 'category',
									        labels: {
									            rotation: 0,
									            style: {
									                fontSize: '13px',
									                fontFamily: 'Verdana, sans-serif'
									            }
									        }
									    },
									    yAxis: {
									        min: 0,
									        title: {
									            text: 'Grau de Recomendação para Pagamento (GRP)'
									        }
									    },
									    legend: {
									        enabled: false
									    },
									    tooltip: {
									        pointFormat: 'GRP <b>{point.y:.1f}</b>'
									    },
									    series: [{
									        name: 'Population',
									        data: myobj.obj,
									        dataLabels: {
									            enabled: true,
									            rotation: 0,
									            color: '#FFFFFF',
									            align: 'right',
									            format: '{point.y:.1f}', // one decimal
									            y: 10, // 10 pixels down from the top
									            style: {
									                fontSize: '13px',
									                fontFamily: 'Verdana, sans-serif'
									            }
									        }
									    }]
									});
							
									}});
							}
							

						});
	</script>

</body>
