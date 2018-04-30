<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<head>
	<link href="<c:url value="/vendor/jstree/themes/default/style.min.css" />" rel="stylesheet">
</head>

<body>

	<div class="row">
		
		<!-- Panel repository chart -->
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-body">
				
					<h3 style="display:inline-block">Porcentagem da Familiaridade no Reposit�rio ${repository.name}</h3>
					<button style="margin-top: 15px" class="btn btn-lg btn-primary pull-right" data-toggle="modal" data-target="#config" ><i class="glyphicon glyphicon-cog"></i></button>
					<jsp:include page="chart-config-modal.jsp" />
					
					<hr>
					
					<div class="row vdivided">
						<div class="container-tree col-md-4">
							<button style="margin-bottom:10px; margin-left:2px; float: right" type="button" class="btn btn-lg btn-primary" onclick="requestJavaFilesAlterations();"> <i class="glyphicon glyphicon-folder-close"></i></button>
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
	
	<script src="<c:url value="/vendor/jstree/jstree.min.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/highcharts.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/modules/no-data-to-display.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js" />"></script>
	
	
	<script>
		$(document).ready(function(){
			
			Highcharts.setOptions({lang: {noData: "N�o houveram altera��es neste diret�rio/arquivo no per�odo especificado"}});
			
			var repository = $('#repository').val();
			var alert = $('#alert').val();
			var existence = $('#existence').val();
			var truckFactor = $('#truckfactor').val();

			$('#jstree').on("changed.jstree", function (e, data) {
			    var newPath = "/" + data.instance.get_path(data.selected[0], "/");
			    request(newPath);
			});
			
			$.ajax({
				type:'GET',
				url : '/codivision/repository/'+repository+'/warningpaths',
				success: function(data){
					
					if (typeof data != 'undefined' && data === 0) {
						$('#panel_wp').append('<p> </p>');
						$('#panel_wp').append('<p>Parab�ns!!!</br>Este reposit�rio</br>n�o possui locais cr�ticos.</p>');
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
				url : '/codivision/repository/'+repository+'/tree',
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
					url : '/codivision/repository/'+repository+'/alterations',
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
					            text: 'Porcentagem das altera��es neste diret�rio:'
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
					            name: 'Modifica��es',
					            data: chartData
					        }]
					    });
						
					    var total = 0;
						$.each(chartData, function(i, item) {
							total += item.y;
						});
						
						$.each(chartData, function(i, item) {
							if (item.y/total > existence/100.0) {
	                        	$('#chart').highcharts().setTitle(null, { text: item.name + ' det�m bastante conhecimento', style : {color: 'red', fontWeight: 'bold'}});
	                        } else if (item.y/total > alert/100.0) {
	                        	$('#chart').highcharts().setTitle(null, { text: item.name + ' det�m bastante conhecimento', style : {color: '#EAC300', fontWeight: 'bold'}});
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

		function requestJavaFilesAlterations(){
			var repository = $('#repository').val();
			var truckFactor = $('#truckfactor').val();
			$('#truckfactor-label').text('');
			$.ajax({
				type : 'POST',
				url : '/codivision/repository/'+repository+'/javaFilesAlterations',
				success : function(chartData){
				    $('#chart').highcharts({
				    	colors: ['#9370DB', '#778899', '#DEB887', '#87CEFA', '#D3D3D3', '#A0522D', '#6495ED', '#D8BFD8', '#BDB76B'],
				        chart: {
				        },
				        credits: {
				            text: ''
				        },
				        title: {
				            text: 'Porcentagem das altera��es neste diret�rio:'
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
				            name: 'Modifica��es',
				            data: chartData
				        }]
				    });
					
				    var total = 0;
					$.each(chartData, function(i, item) {
						total += item.y;
					});
					
					$.each(chartData, function(i, item) {
						if (item.y/total > existence/100.0) {
                        	$('#chart').highcharts().setTitle(null, { text: item.name + ' det�m bastante conhecimento', style : {color: 'red', fontWeight: 'bold'}});
                        } else if (item.y/total > alert/100.0) {
                        	$('#chart').highcharts().setTitle(null, { text: item.name + ' det�m bastante conhecimento', style : {color: '#EAC300', fontWeight: 'bold'}});
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
				}
			});
		}
	</script>

</body>