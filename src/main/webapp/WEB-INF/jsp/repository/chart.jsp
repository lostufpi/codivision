<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<head>
	<link href="<c:url value="/vendor/jstree/themes/default/style.min.css" />" rel="stylesheet">
</head>

<body>

	<div class="row">
	
		<!-- Panel repository list -->	
		<div class="col-lg-3">
			<div class="panel panel-default text-center">
				<div class="panel-heading">
					<b>Locais críticos</b>
				</div>
				<!-- /.panel-heading -->
				<div id="panel_wp" class="panel-body" style="overflow:auto; height:233px;">
					<div class="list-group" id="warningPaths"></div>
				</div>
			</div>
		
			<div class="panel panel-default text-center">
				<div class="panel-heading">
					<b><fmt:message key="repository.paths" /></b>
				</div>
				<!-- /.panel-heading -->
				<div class="panel-body" style="overflow:auto; height:233px;">
					<div class="list-group text-center">
							<c:forEach var="paths" items="${extractionPaths}">
								<a class="list-group-item" href="${linkTo[RepositoryController].chart(repository.id, paths.id)}">${paths.path}</a>
							</c:forEach>
					</div>
					
				</div>
			</div>
		</div>
		
		<!-- Panel repository chart -->
		<div class="col-lg-9">
			<div class="panel panel-default">
				<div class="panel-body">
				
					<h3 style="display:inline-block">Porcentagem de alterações no repositório ${repository.name}</h3>
					<button style="margin-top: 15px" class="btn btn-lg btn-primary pull-right" data-toggle="modal" data-target="#config" ><i class="glyphicon glyphicon-cog"></i></button>
					<jsp:include page="chart-config-modal.jsp" />
					
					<hr>
					
					<div class="row vdivided">
						<div class="container-tree col-md-4">
							<input style="margin-bottom:10px" type="text" class="form-control" id="jstree-search" placeholder="Pesquisar">
							<div id="jstree"></div>
						</div>
						<div class="col-md-8 text-center">
							<div id="chart"></div>
							<p id="truckfactor-label"></p>
						</div>
					</div>
					
					<!-- <input style="margin-bottom:10px" type="text" class="form-control" id="jstree-search" placeholder="Pesquisar">
					<div id="jstree"></div>
					<div id="chart"></div> -->
					
				</div>
			</div>
		</div>
	</div>
	
	

	<input type="hidden" id="repository" value="${repository.id}" />
	<input type="hidden" id="extractionPath" value="${extractionPath.id}" />
	
	<script src="<c:url value="/vendor/jstree/jstree.min.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/highcharts.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/modules/no-data-to-display.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js" />"></script>
	
	
	<script>
		$(document).ready(function(){
			
			Highcharts.setOptions({lang: {noData: "Não houveram alterações neste diretório/arquivo no período especificado"}});
			
			var repository = $('#repository').val();
			var path = $('#extractionPath').val();
			var alert = $('#alert').val();
			var existence = $('#existence').val();
			var truckFactor = $('#truckfactor').val();

			$('#jstree').on("changed.jstree", function (e, data) {
			    var newPath = "/" + data.instance.get_path(data.selected[0], "/");
			    request(newPath);
			});
			
			$.ajax({
				type:'GET',
				url : '/codivision/repository/'+repository+'/path/'+path+'/warningpaths',
				success: function(data){
					
					if (typeof data != 'undefined' && data === 0) {
						$('#panel_wp').append('<p> </p>');
						$('#panel_wp').append('<p>Parabéns!!!</br>Este repositório</br>não possui locais críticos.</p>');
					}
					
					$.each(data, function(index, el){
						$('#warningPaths').append('<a id="'+ el.id +'" class="list-group-item path-item" href="#">'+el.text+'</a>');
					});
					
					$('.path-item').on('click', function() {
						var id = $(this).attr('id');
						$('#jstree').jstree(true).deselect_all(true);
						$('#jstree').jstree(true).select_node(id);
						$('#jstree').animate({
							scrollTop: $('#'+ id + '_anchor').offset().top,
							scrollLeft: $('#'+ id + '_anchor').offset().top
						}, 800);
						
					});
					
				}
			});
			
			$.ajax({
				type : 'POST',
				url : '/codivision/repository/'+repository+'/path/'+path+'/tree',
				success : function(treeData){

					$('#jstree').jstree({
						'core' : {
							  'data' : treeData
							},
						'types' : {
							"FOLDER" : {
							      "valid_children" : ["FOLDER", "FILE"]
							    },
							"FILE" : {
							      "icon" : "jstree-file",
							      "valid_children" : [],
							    }
							},
						'plugins' : ["types", "wholerow", "sort", "search"]
					});
					
					request("/");

				}
			});
			
			var to = false;
			
			$('#jstree-search').keyup(function () {
			    if(to) { clearTimeout(to); }
			    to = setTimeout(function () {
			      var v = $('#jstree-search').val();
			      $('#jstree').jstree(true).search(v);
			    }, 250);
			});
			
			function request(newPath){
				$('#truckfactor-label').text('');
				$.ajax({
					type : 'POST',
					url : '/codivision/repository/'+repository+'/path/'+path+'/alterations',
					data : {'newPath' : newPath},
					success : function(chartData){
						
						//Inicio callback principal
  						
					    $('#chart').highcharts({
					    	colors: ['#9370DB', '#778899', '#DEB887', '#87CEFA', '#D3D3D3', '#A0522D', '#6495ED', '#D8BFD8', '#BDB76B'],
					        chart: {
					        },
					        credits: {
					            text: ''
					        },
					        title: {
					            text: 'Porcentagem das alterações neste diretório:'
					        },
					        subtitle: {
					        	text: '#',
  					        	style: {
  					        		color: '#FFFFFF'
  					        	}
					        },
					        tooltip: {
					            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
					        },
					        plotOptions: {
					            pie: {
					            	borderWidth: 1,
					                allowPointSelect: false,
					                cursor: 'pointer',
					                dataLabels: {
					                    enabled: true,
					                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',
					                    style: {
					                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
					                    }
					                }
					            }
					        },
					        series: [{
					            type: 'pie',
					            name: 'Modificações',
					            data: chartData
					        }]
					    });
						
					    var total = 0;
						$.each(chartData, function(i, item) {
							total += item.y;
						});
						
						$.each(chartData, function(i, item) {
							if (item.y/total > existence/100.0) {
	                        	$('#chart').highcharts().setTitle(null, { text: item.name + ' detém bastante conhecimento', style : {color: 'red', fontWeight: 'bold'}});
	                        } else if (item.y/total > alert/100.0) {
	                        	$('#chart').highcharts().setTitle(null, { text: item.name + ' detém bastante conhecimento', style : {color: '#EAC300', fontWeight: 'bold'}});
	                    	}
  						});
						
						chartData.sort(function(a, b){return b.y-a.y});
						var truckFactorTotal = 0;
						$.each(chartData, function(i, item) {
							truckFactorTotal += item.y/total;
							if(truckFactorTotal > truckFactor/100) {
								$('#truckfactor-label').text('Truck Factor: '+ (i+1));
								return false;
							}
						});
						
  						
					   

						//Fim callback principal
					}
				});
			}
		
		});
	</script>

</body>