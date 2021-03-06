<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
	<link href="<c:url value="/vendor/jstree/themes/default/style.min.css" />" rel="stylesheet">
	<link href="<c:url value="/vendor/datatable/css/bootstrap.min.css" />" rel="stylesheet">
	<link href="<c:url value="/vendor/datatable/css/dataTables.bootstrap.min.css" />" rel="stylesheet">
</head>

<body>
	<div class="row">
		
		<!-- Panel repository chart -->
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-body">
			
					<h3 style="display:inline-block">Agrega��o de features no reposit�rio ${repository.name}</h3>
					<form class="pull-right" method="get" action="${linkTo[FeatureController].features(repository.id)}">
						<button style="margin-top: 15px; margin-left: 2px" class="btn btn-lg btn-primary pull-right"><i class="glyphicon glyphicon-tasks"></i></button>
					</form>
					<hr>
					<div class="row vdivided">
						<div class="container-tree col-md-4" >
							<button style="margin-bottom:10px; margin-left:2px; float: right" type="button" class="btn btn-lg btn-danger" onclick="feature_delete();"> <i class="glyphicon glyphicon-remove-sign"></i></button>
							<button style="margin-bottom:10px; margin-left:2px; float: right" type="button" class="btn btn-lg btn-warning" onclick="auto_agroup();"> <i class="glyphicon glyphicon-th"></i></button>
							<button style="margin-bottom:10px; float: right" type="button" class="btn btn-lg btn-primary" data-toggle="modal" data-target="#use-case-name-modal"> <i class="glyphicon glyphicon-th-large"></i></button>
							<input style="margin-bottom:10px" type="text" class="form-control" id="jstree-search" placeholder="Search">
							<div id="jstree"></div>
						</div>
						<div class="col-md-8 text-center">
							<div id="table-use-cases">
								<table id="table_uc" class="table table-striped table-bordered" cellspacing="0" width="100%">
									<thead>
										<tr>
	      									<th style="text-align: center" colspan="3"> Functionalities
 </th>
	    								</tr>
										<tr>
											<th style="text-align: center">ID</th>
											<th style="text-align: center">Nome</th>
											<th style="text-align: center">N� Sub. Features</th>
											<th style="text-align: center">Op��o</th>
										</tr>
									</thead>
									<tbody id="table-use-cases-body">
											
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<input type="hidden" id="repository" value="${repository.id}" />
	
	
	<script src="<c:url value="/vendor/datatable/js/jquery-1.12.4.js" />"></script>
	<script src="<c:url value="/vendor/jstree/jstree.min.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/highcharts.js" />"></script>
	<script src="<c:url value="/vendor/highcharts/modules/no-data-to-display.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap-datepicker/locales/bootstrap-datepicker.pt-BR.min.js" />"></script>
	
    <script src="<c:url value="/vendor/datatable/js/jquery.dataTables.min.js" />"></script>
    <script src="<c:url value="/vendor/datatable/js/dataTables.bootstrap.min.js" />"></script>
	
	<jsp:include page="use-case-name-modal.jsp" />
	
	<script>

		$(document).ready(function(){
			var repository = $('#repository').val();
			updateUseCaseTable();
			
			$.ajax({
				type : 'POST',
				url : '/codivision/feature/repository/'+repository+'/tree',
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
						'plugins' : ["types", "wholerow", "sort", "search", "state", "contextmenu", "checkbox"]
					});
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
		});
	
		function feature_delete() {
			var tree = $('#jstree').jstree(true);
			var selected = tree.get_selected();
			for (var i = 0; i < selected.length; i++) {
				var node = tree.get_node(selected[i]);
				var text = tree.get_node(selected[i]).text;
				if(text != null){
					delete_frontend(node);
					if(!text.includes(".java")){
						delete_backend(node);
					}
				}
			}
		};
	
		function delete_backend(node) {
			var repository = $('#repository').val();
			$.ajax({
				type : 'POST',
				url : '/codivision/repository/'+repository+'/feature/remove',
				data : {'idFeature' : node.id},
			});
		}
	
		function delete_frontend(node) {
			var tree = $('#jstree').jstree(true);
			tree.hide_node(node);
			tree.delete_node(node);
		};

		function agroup() {
			var repository = $('#repository').val();
			var tree = $('#jstree').jstree(true);
			var selected = tree.get_selected();
			var features = [];
			for (var i = 0; i < selected.length; i++) {
				var node_text = tree.get_node(selected[i]).text;
				if(!node_text.includes(".java")){
					features.push(tree.get_node(selected[i]).id);
				}
			}

			var name = $('#usecase_name').val();
			$.ajax({
				type : 'POST',
				url : '/codivision/agroup/repository/'+repository+'/feature',
				data : {'name' : name, 'features' : features},
			});
		}

		function auto_agroup() {
			var repository = $('#repository').val();
			$.ajax({
				type : 'POST',
				url : '/codivision/agroup/repository/'+repository+'/feature/automatic',
			});
		}
		

		$('#btn_add_usecase').click(function(){
			agroup();
			updateUseCaseTable();
			window.location.reload(true);
		});

		function updateUseCaseTable() {
			var repository = $('#repository').val();
			$.ajax({
				type : 'POST',
				url : '/codivision/usecases/repository/' + repository,
				success : function(data) {
					var table_body = "";
					for (i = 0; i < data.length; i++) {
						table_body += '<tr><td>'
							+ data[i].id
							+ ' </td> <td> '
							+ data[i].name
							+ '</td><td>'
							+ data[i].totalFeatures
							+ '</td><td>'
							+ "<button type='button' class='btn btn-danger' id='"+data[i].id+"' onclick='usecase_delete("+data[i].id+");'><i class='glyphicon glyphicon-remove-sign'></i> </button>"
							+ '</td></tr>';
					}
					document.getElementById("table-use-cases-body").innerHTML = table_body;
					$('#table_uc').DataTable();
				}
			});
		}

		function usecase_delete(id) {
			var repository = $('#repository').val();
			$.ajax({
				type : 'POST',
				url : '/codivision/repository/'+repository+'/usecase/remove',
				data : {'idUseCase' : id},
			});
			window.location.reload(true);
		}
		
	</script>
</body>