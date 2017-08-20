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
					<div class="row vdivided" style="padding-right: 10px;padding-left: 10px;">
						<div class="container-tree col-md-4">
							<input style="margin-bottom:10px" type="text" class="form-control" id="jstree-search" placeholder="Pesquisar">
							<div id="jstree"></div>
						</div>
						<h3 style="display:inline-block;padding-left: 20px;">Contribuição no Projeto ${repository.name}</h3>
						
						<button style="margin-top: 15px" class="btn btn-lg btn-primary pull-right" data-toggle="modal" data-target="#testInfo-config" ><i class="glyphicon glyphicon-cog"></i></button>
						
						<div class="modal fade" id="testInfo-config" role="dialog">
<div class="modal-dialog modal-lg">
	
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">Configurações</h4>
			</div>

			<form action="${linkTo[RepositoryController].testConfig(repository.id, extractionPath.id)}" method="post">
			
				<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-body">
					<div class="row vdivided">
						<div class="container-tree col-md-4" style="overflow:auto; height:400px;">
					 		<div id="jstree-modal"></div>		
					 </div>
					 
					 <div class="row" style="height: 74px;">
					
						<div class="col-lg-4" style="width: 210px;">
							<div class="form-group">
								<label for="timeWindow"><fmt:message key="repository.config.time_window" />:</label> <select
									class="form-control" id="timeWindow"
									name="configuration.timeWindow">
									<c:forEach var="option" items="${windows}">
										<c:choose>
											<c:when test="${option.type == 'window.custom'}">
												<option id="show-date" value="${option}"><fmt:message key="${option.type}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${option}"><fmt:message key="${option.type}" /></option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="col-lg-4" style="width: 180px;">
							<div class="form-group date">
								<fmt:formatDate pattern="dd/MM/yyyy"
									value="${configuration.initDate}" var="initDate" />
								<label for="initDate"><fmt:message key="repository.config.date.init" />:</label> <input type="text"
									class="form-control datepicker" id="initDate" placeholder="Data inicial"
									name="configuration.initDate" >
							</div>
						</div>
						
						<div class="col-lg-4" style="width: 180px;">
							<div class="form-group date">
								<fmt:formatDate pattern="dd/MM/yyyy"
									value="${configuration.endDate}" var="endDate" />
								<label for="endDate"><fmt:message key="repository.config.date.end" />:</label> <input type="text"
									class="form-control datepicker" id="endDate" placeholder="Data final"
									name="configuration.endDate">
							</div>
						</div>
						
					</div>
					
					<div class="panel-body" style="overflow:auto; height:400px;">
					<div class="list-group text-center">
					<table class="table table-hover">
						<thead>
							<tr>
								<th style="width: 90px;"></th>
								<th>Diretório dos Testes</th>
								
							</tr>
						</thead>
						<tbody id="tbody">
									
						
						</tbody>
					</table>
					</div></div>
					
</div></div></div>

</div>

				</div>
				<div style="padding-left: 20px; padding-bottom: 20px;">
				<button type="button" id="addTest" class="btn btn-primary" style="left:10px">
						Definir como Teste
					</button>
</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<fmt:message key="close" />
					</button>
					<button type="submit" class="btn btn-primary"><fmt:message key="save" /></button>
				</div>
</form>
		</div>

	</div>
</div>
						
						<hr>
						<div class="col-md-8 text-center">
							<div id="chartColumn"></div>
							
						</div>
						
					</div>
					<hr>
					<div class="row">
				<div class="col-md-5 text-center">
							<div id="pie"></div>
							
				</div>	
				<div class="col-md-7 text-center">
				<h4>Ranking dos Desenvolvedores na Realização de Testes</h4>
							<div id="table" class="table-overflow" style="height:350px;overflow-y:auto;"></div>
							
				</div>
				</div>
					<hr>
					<div class="col-lg-9"></div>
					<div id="input" class="col-lg-3"
												>

												<h6 id="name_input1" style="text-align: center"></h6>
												<select id="select1" class="form-control">
												</select>


											</div>
											<hr>
				<div class="col-md-12 text-center">
							<div id="chart"></div>
							
				</div>
				<hr>
				
				
				
				
			</div>
		</div>
	</div>
</div>
	<input type="hidden" id="repository" value="${repository.id}" />
	<input type="hidden" id="repositoryName" value="${repository.name}" />
	<input type="hidden" id="extractionPath" value="${extractionPath.id}" />
	<input type="hidden" id="path" value="${extractionPath.path}" />
	
	<script src="https://code.highcharts.com/highcharts.js"></script>
	<script src="https://code.highcharts.com/modules/exporting.js"></script>
	
	

	<script src="<c:url value="/vendor/jstree/jstree.min.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/highcharts.js" />"></script>
	<script
		src="<c:url value="/vendor/highcharts/modules/no-data-to-display.js" />"></script>
	<script
		src="<c:url value="/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js" />"></script>
	<script
		src="<c:url value="/vendor/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js" />"></script>
	<script type="text/javascript">
	function remove(pathId) {
		var repository = $('#repository').val();
		
		$.ajax({
			type : 'POST',
			url : '/codivision/repository/'+repository+'/deleteTestPath',
			data : {'pathId' : pathId},
			success : function() {
				$.ajax({
					type : 'POST',
					url : '/codivision/repository/'+repository+'/testFile',
					success : function(data) {
						var i;
						var text = "";
						for(i = 0; i < data.length; i++ ){
							text += "<tr><th style='width: 90px';> <button type='button' class='btn btn-danger' id='click"+i+"' >Remover </button></th>";
							text += "<th><h5>";
							text += data[i].path;	
							text += "</h5></th></tr>";
							
						}
						document.getElementById("tbody").innerHTML = text;
						for(i = 0; i < data.length; i++ ){
							document.getElementById("click"+i).setAttribute("onClick", "remove("+i+");");
						}
					}

				});
			}
		
	});
		}
	</script>

	<script>
		$(document).ready(
				function() {
					
					$('.datepicker').datepicker({
					    format: 'dd/mm/yyyy',
					    language: 'pt-BR'
					})
					
					$('#timeWindow').val("${configuration.timeWindow}").attr('selected','selected');
					$("[data-toggle=popover]").popover();
					
					$('#timeWindow').change(showdata);
					showdata();
					
					function showdata(){
						if($('#timeWindow option:selected').attr('id') == 'show-date'){
			        		$('.date').show();
			        	} else {
			        		$('.date').hide();
			        	}
					}

					Highcharts.setOptions({lang: {noData: "Não houveram alterações no período especificado"}});
					
					var repositoryName = $('#repositoryName').val();
					var repository = $('#repository').val();
					var path = $('#extractionPath').val();
					var extractionPath = $('#path').val();
					var newPathTest = extractionPath;
					
					$('#addTest').click(function(){
						
						newPathTest = newPathTest.substring(extractionPath.length);
						if(newPathTest===""){
							newPathTest = "/";
						}
						$.ajax({
							type : 'POST',
							url : '/codivision/repository/'+repository+'/addTestPath',
							data : {'newPathTest' : newPathTest},
							success : function() {
								$.ajax({
									type : 'POST',
									url : '/codivision/repository/'+repository+'/testFile',
									success : function(data) {
										var i;
										var text = "";
										for(i = 0; i < data.length; i++ ){
											text += "<tr><th style='width: 90px';> <button type='button' class='btn btn-danger' id='click"+i+"' >Remover </button></th>";
											text += "<th><h5>";
											text += data[i].path;	
											text += "</h5></th></tr>";
											
										}
										document.getElementById("tbody").innerHTML = text;
										for(i = 0; i < data.length; i++ ){
											document.getElementById("click"+i).setAttribute("onClick", "remove("+i+");");
										}
									}

								});
							}
						
					});
						
					});
					
					
					
					
					$.ajax({
						type : 'POST',
						url : '/codivision/repository/'+repository+'/percentageContribuition',
						success : function(data) {
							Highcharts.chart('pie', {
								colors: ['#00FF00', '#FF0000'],
							    chart: {
							        plotBackgroundColor: null,
							        plotBorderWidth: null,
							        plotShadow: false,
							        type: 'pie'
							    },
							    title: {
							        text: 'Porcentagem da Contribuição'
							    },
							    tooltip: {
							        pointFormat: '{series.name}: <b>{point.percentage:.0f} %</b>'
							    },
							    
							    series: [{
							        name: 'Contribuição',
							        colorByPoint: true,
							        data: data
							    }]
							});
						}
					});
					
					
					$.ajax({
						type : 'POST',
						url : '/codivision/repository/'+repository+'/testFile',
						success : function(data) {
							var i;
							var text = "";
							for(i = 0; i < data.length; i++ ){
								text += "<tr><th style='width: 90px';> <button type='button' class='btn btn-danger' id='click"+i+"' >Remover </button></th>";
								text += "<th><h5>";
								text += data[i].path;	
								text += "</h5></th></tr>";
								
							}
							document.getElementById("tbody").innerHTML = text;
							for(i = 0; i < data.length; i++ ){
								document.getElementById("click"+i).setAttribute("onClick", "remove("+i+");");
							}
						}

					});
					
					
					
					$.ajax({
						type : 'POST',
						url : '/codivision/repository/' + repository + '/authors',
						success : function(dataAuthor) {
							
							var element = document.getElementById('name_input1');
							element.innerHTML = '<b>Desenvolvedor</b>';
							
							var x1 = document.createElement('option');
							x1.setAttribute('value','Todos');
							var t1 = document.createTextNode('Todos');
							x1.appendChild(t1);
							document.getElementById('select1').appendChild(x1);
							
					for (i = 0; i < dataAuthor.length; i++) {
						var x = document.createElement('option');
						x.setAttribute('value',dataAuthor[i]);
						var t = document.createTextNode(dataAuthor[i]);
						x.appendChild(t);
						document.getElementById('select1').appendChild(x);
					}
							
							
							
						}
					});
					
					$('#select1').change(show);
					
					
					project();
					
					function show() {
						if($('#select1 option:selected').val() == 'Todos'){
							project();
						}else{
							chartsPlot($('#select1 option:selected').val());
						}
						
						
						
					}
					
					function project(){
						
						$.ajax({
							type : 'POST',
							url : '/codivision/repository/' + repository + '/projectHistoric',
							success : function(data) {
							
						Highcharts.chart('chart', {
							colors: ['#00FF00', '#FF0000'],
							title: {
					            text: 'Histórico de Commits do Projeto '+repositoryName,
					            x: -20 //center
					        },
					        subtitle: {
					            text: 'Relação entre os commits comuns e commits de teste',
					            x: -20
					        },
					        xAxis: {
					            categories: data.dataCategories
					        },
					        yAxis: {
					            title: {
					                text: 'Quantidade de Linhas Alteradas'
					            },
					            plotLines: [{
					                value: 0,
					                width: 1,
					                color: '#808080'
					            }]
					        },
					        tooltip: {
					            valueSuffix: ''
					        },
					        legend: {
					       
					        },
					        series: data.dataSeries
					    });
						
							}
						});
						
						}

					
					function chartsPlot(nome){
						
						
						
						$.ajax({
							type : 'POST',
							url : '/codivision/repository/' + repository + '/authorHistoric',
							data : {'author' : nome},
							success : function(datas) {
								
								Highcharts.chart('chart', {
									colors: ['#00FF00', '#FF0000'],
								
					        title: {
					            text: 'Histórico de Commits do Desenvolvedor '+nome,
					            x: -20 //center
					        },
					        subtitle: {
					            text: 'Relação entre os commits comuns e commits de teste',
					            x: -20
					        },
					        xAxis: {
					            categories: datas.dataCategories
					        },
					        yAxis: {
					            title: {
					                text: 'Quantidade de Linhas Alteradas'
					            },
					            plotLines: [{
					                value: 0,
					                width: 1,
					                color: '#808080'
					            }]
					        },
					        tooltip: {
					            valueSuffix: ''
					        },
					        legend: {
					       
					        },
					        series: datas.dataSeries
					    });
						
							}
						});
						}

					$.ajax({
						type : 'POST',
						url : '/codivision/repository/'+repository+'/path/'+path+'/tree',
						success : function(treeData) {
							
							$('#jstree-modal').jstree(
									{
										'core' : {
											'data' : treeData
										},
										'types' : {
											"FOLDER" : {
												"valid_children" : [ "FOLDER",
														"FILE" ]
											},
											"FILE" : {
												"icon" : "jstree-file",
												"valid_children" : [],
											}
										},
										'plugins' : [ "types", "wholerow",
												"sort", "search" ]
									});

							$('#jstree').jstree(
									{
										'core' : {
											'data' : treeData
										},
										'types' : {
											"FOLDER" : {
												"valid_children" : [ "FOLDER",
														"FILE" ]
											},
											"FILE" : {
												"icon" : "jstree-file",
												"valid_children" : [],
											}
										},
										'plugins' : [ "types", "wholerow",
												"sort", "search" ]
									});

							request("/");

						}

					});
					
					$('#jstree').on("changed.jstree", function (e, data) {
					    var newPath = "/" + data.instance.get_path(data.selected[0], "/");
					    request(newPath);
					});
					
					$('#jstree-modal').on("changed.jstree", function (e, data) {
					    newPathTest = "/" + data.instance.get_path(data.selected[0], "/");
					    
					});
					
					
					
					
					function request(newPath){
						
						
						$.ajax({
							type : 'POST',
							url : '/codivision/repository/'+repository+'/path/'+path+'/alterationsQntLine',
							data : {'newPath' : newPath},
							success : function(data) {
								Highcharts.chart('chartColumn', {
									colors: ['#00FF00', '#FF0000'],
									chart: {
								        type: 'bar'
								    },
								    title: {
								        text: null
								    },
								
								    xAxis: {
								        categories: data.dataCategories,
								        title: {
								            text: null
								        }
								    },
								    yAxis: {
								        min: 0,
								        title: {
								            text: 'Quantidade de Linhas Alteradas',
								            align: 'high'
								        },
								        labels: {
								            overflow: 'justify'
								        }
								    },
								    tooltip: {
								        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
								        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
								            '<td style="padding:0"><b>{point.y:.0f}</b></td></tr>',
								        footerFormat: '</table>',
								        shared: true,
								        useHTML: true
								    },
								    plotOptions: {
								        bar: {
								            dataLabels: {
								                enabled: false
								            }
								        }
								    },
								   
								    credits: {
								        enabled: false
								    },
								    series: data.dataSeries
								});
							}
						});
						
					}
					
					var to = false;
					
					$('#jstree-search').keyup(function () {
					    if(to) { clearTimeout(to); }
					    to = setTimeout(function () {
					      var v = $('#jstree-search').val();
					      $('#jstree').jstree(true).search(v);
					    }, 250);
					});
					
					$.ajax({
						type : 'POST',
						url : '/codivision/repository/' + repository + '/rankingDevelops',
						success : function(data) {
							var i;
							var text = "<table class='table'><thead><tr><th>#</th><th><center>Desenvolvedor</center></th><th>Porcentagem</th><th>Contribuição</th><th><center>Contribuição nos Testes</center></th></tr></thead><tbody>";
							var k = 1;
							var sum = 0;
							var sumTest = 0;
							for(i = 0; i < data.length; i++ ){
								sum = sum + data[i].y;
								sumTest = sumTest + data[i].absolute;
								if(data[i].absolute > 0){
								text += "<tr class='info'><td>"+k+"</td><td>"+data[i].name+"</td><td>"+ Number(((data[i].absolute * 100)/(data[i].y + data[i].absolute))).toFixed(2) +"%</td><td>"+data[i].y+"</td><td>"+data[i].absolute+"</td></tr>";
								}else{
									text += "<tr class='danger'><td>"+k+"</td><td>"+data[i].name+"</td><td>"+ Number(((data[i].absolute * 100)/(data[i].y + data[i].absolute))).toFixed(2) +"%</td><td>"+data[i].y+"</td><td>"+data[i].absolute+"</td></tr>";
									
								}
								k = k + 1;
							}
							text += "<tr class='success'><td>-</td><td>Total</td><td>"+ Number(((sumTest * 100)/(sum + sumTest))).toFixed(2) +"%</td><td>"+sum+"</td><td>"+sumTest+"</td></tr>";
							
							text += "</tbody></table>";
							document.getElementById("table").innerHTML = text;
						}
						});
					
					
					

				});
	</script>
</body>