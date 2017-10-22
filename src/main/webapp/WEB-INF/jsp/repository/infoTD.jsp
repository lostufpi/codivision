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
				
					<h3 style="display:inline-block">Informações sobre Dívida Técnica no Repositório ${repository.name}</h3>
					<button style="margin-top: 15px" class="btn btn-lg btn-primary pull-right" data-toggle="modal" data-target="#config" ><i class="glyphicon glyphicon-cog"></i></button>
<%-- 					<jsp:include page="chart-config-modal.jsp" /> --%>
					
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
		
		var repository = $('#repository').val();
	
		$.ajax({
			type : 'POST',
			url : '/codivision/repository/'+repository+'/tree/td',
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
				
			}
		});
		
	
	
	});

</script>

</body>
