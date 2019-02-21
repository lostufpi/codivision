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
			
					<h3 style="display:inline-block">Familiarity over features in the ${repository.name} repository</h3>
					<form class="pull-right" method="get" action="${linkTo[FeatureController].usecase(repository.id)}">
						<button style="margin-top: 15px; margin-left: 2px" class="btn btn-lg btn-primary pull-right"><i class="glyphicon glyphicon-tasks"></i></button>
					</form>
					<button style="margin-top: 15px" class="btn btn-lg btn-primary pull-right" data-toggle="modal" data-target="#config" ><i class="glyphicon glyphicon-cog"></i></button>
					<jsp:include page="../repository/features-config-modal.jsp"/>
					
					<hr>
					
					<div class="row vdivided">
						<div class="container-tree col-md-4" >
							<input style="margin-bottom:10px" type="text" class="form-control" id="jstree-search" placeholder="Search">
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
	<script src="<c:url value="/vendor/highcharts/highcharts.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/modules/no-data-to-display.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js" />"></script>
	
	
	<script>
		$(document).ready(function(){
			
			Highcharts.setOptions({lang: {noData: "Não houve alterações no período especificado."}});
			
			var repository = $('#repository').val();
			var alert = $('#alert').val();
			var existence = $('#existence').val();
			var truckFactor = $('#truckfactor').val();
			
			
			$('#div-detalhar').hide();
			
			
			var fileId;
			
			
			$.ajax({
				type : 'POST',
				url : '/codivision/repository/' + repository + '/feature/criticality/chart',
				success : function(core) {
					
					
					$('#div-detalhar').show();
					$('#div-carregando').hide();
					

					var a1 = '{ "obj":';
					var a2 = a1.concat(core);
					var a3 = a2.concat('}');
					
					var myobj = JSON.parse(a3);
					
					$('#chart-bar').highcharts({
					
					    chart: {
					        type: 'column'
					    },
					    title: {
					        text: 'Criticidade das Funcionalidades em relação a presença de Dívidas Técnicas'
					    },
					    subtitle: {
					        text: 'Criticidade inferida para cada funcionalidade levando-se em consideração o acoplamento, complexidade ciclomática e as dívidas técnicas'
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
					                point: {}
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
					            y: 10, // 10 pixels down from the
					            style: {
					                fontSize: '13px',
					                fontFamily: 'Verdana, sans-serif'
					            }
					        }
					    }]
					});
					
			
				}
			});
			

			$('#jstree').on("changed.jstree", function (e, data) {
			    var newPath = "/" + data.instance.get_path(data.selected[0], "/");
			    var nodeId = data.instance.get_node(data.selected).id;
			    request(newPath, nodeId);
			});
			
			$.ajax({
				type : 'POST',
				url : '/codivision/usecase/repository/'+repository+'/tree',
				success : function(treeData){
					var data = treeData;
					$('#jstree').jstree({
					
						'core' : {
							  'check_callback' : true,
							  'data' : treeData
							},
						'types' : {
							"FEATURE" : {
								 "icon" : "glyphicon glyphicon-folder-close",
							      "valid_children" : ["FEATURE", "ELEMENT"]
							    },
							"ELEMENT" : {
							      "icon" : "jstree-file",
							      "valid_children" : [],
							    }
							},
						'plugins' : ["types", "wholerow", "sort", "search", "state", "contextmenu"]
					});
					
					request("/", null);
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

			function request(newPath, nodeId){
				$('#truckfactor-label').text('');
				$.ajax({
					type : 'POST',
					url : '/codivision/usecase/repository/'+repository+'/alterations',
					data : {'newPath' : newPath, 'nodeId' : nodeId},
					success : function(chartData){
						
					    $('#chart').highcharts({
					    	colors: ['#9370DB', '#778899', '#DEB887', '#87CEFA', '#D3D3D3', '#A0522D', '#6495ED', '#D8BFD8', '#BDB76B'],
					        chart: {
					        },
					        credits: {
					            text: ''
					        },
					        title: {
					            text: 'Familiarity of the developers in the selected functionality:'
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
							if ((item.y/total) > (existence/100.0)) {
	                        	$('#chart').highcharts().setTitle(null, { text: item.name + ' is very familiar', style : {color: 'red', fontWeight: 'bold'}});
	                        } else if (item.y/total > alert/100.0) {
	                        	$('#chart').highcharts().setTitle(null, { text: item.name + ' is very familiar', style : {color: '#EAC300', fontWeight: 'bold'}});
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
		
		});
	</script>
</body>