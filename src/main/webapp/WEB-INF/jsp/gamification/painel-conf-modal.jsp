<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="modal fade" id="conf">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">Configurações</h4>
			</div>
			
			<form action="${linkTo[GamificationController].stop(repository.id)}" method="post">
				<div class="modal-body">

					<div class="row">
	
							<div class="form-group">
								<label style="width:100%; text-align:center;" for="timeWindow"><fmt:message key="gamification.close" /></label>
							</div>
					</div>

				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="gamification.cancel" /></button>
					<button type="submit" class="btn btn-primary"><fmt:message key="gamification.confirm" /></button>
				</div>
				
			</form>
		</div>

	</div>

</div>

<script>
	$(document).ready(function(){
		
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
		
	});
</script>