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
				
					<h3 style="display:inline-block">Familiaridade sobre features no reposit�rio ${repository.name}</h3>
					<button style="margin-top: 15px" class="btn btn-lg btn-primary pull-right" data-toggle="modal" data-target="#config" ><i class="glyphicon glyphicon-cog"></i></button>
					<jsp:include page="chart-config-modal.jsp" />
					
					<hr>
					
					<div class="row vdivided">
						<div class="container-tree col-md-4" >
							<button style="margin-bottom:10px; float: right" type="button" class="btn btn-danger btn-sm" onclick="feature_delete();"> <i class="glyphicon glyphicon-remove"></i> Remover Feature</button>
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
			    var nodeId = data.instance.get_node(data.selected).id;
			    request(newPath, nodeId);
			});
			
			$.ajax({
				type : 'POST',
				url : '/codivision/repository/'+repository+'/feature/tree',
				success : function(treeData){
					var data = treeData;
					$('#jstree').jstree({
					
						'core' : {
							  'check_callback' : true,
							  'data' : treeData
							},
						'types' : {
							"FEATURE" : {
							      "valid_children" : ["FEATUE", "ELEMENT"]
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
					url : '/codivision/repository/'+repository+'/features/alterations',
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
					            text: 'Familiaridade nessa funcionalidade/arquivo:'
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
							if ((item.y/total) > (existence/100.0)) {
	                        	$('#chart').highcharts().setTitle(null, { text: item.name + ' det�m bastante conhecimento', style : {color: 'red', fontWeight: 'bold'}});
	                        } else if (item.y/total > alert/100.0) {
	                        	$('#chart').highcharts().setTitle(null, { text: item.name + ' det�m bastante conhecimento', style : {color: '#EAC300', fontWeight: 'bold'}});
	                    	}
  						});
					}
				});
			}
		
		});

		function feature_delete() {
			delete_backend();
			delete_frontend();
		};

		function delete_backend() {
			var repository = $('#repository').val();
			var ref = $('#jstree').jstree(true),
			sel = ref.get_selected();
			var node = ref.get_node(sel);
			var idFeature = node.id;
			var nameFeature = "/" + ref.get_path(node, "/");

			$.ajax({
				type : 'POST',
				url : '/codivision/remove/feature',
				data : {'nameFeature' : nameFeature, 'idFeature' : idFeature},
			});
		}

		function delete_frontend() {
			var tree = $('#jstree').jstree(true);
			var	selected = tree.get_selected();
			tree.hide_node(selected);
			tree.delete_node(selected);
		};

	</script>
</body>