<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="modal fade" id="start">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">Iniciando Gamificação</h4>
			</div>
			
			<form action="${linkTo[GamificationController].start(repository.id)}" method="post">
				<div class="modal-body">

					<div class="row">
					
						<div class="col-lg-4" style="width: 100%;">
							<div class="form-group">
								<label for="timeWindow"><fmt:message key="gamification.cicle.time" />:</label>
								<input value="12" type="number" style="right: 40px; position: absolute;" id="cicleTime" name="start.cicle" data-toggle="popover" data-placement="left" data-trigger="hover"
										data-content="<fmt:message key="gamification.cicle.time.title" />" ></input>
							</div>
						</div>
						
						<div class="col-lg-4" style="width: 100%;">
							<div class="form-group">
								<label for="timeWindow"><fmt:message key="gamification.awards.time" />:</label>
								<input value="7" type="number" style="right: 40px; position: absolute;" id="awardsTime" name="start.awardsAtt" data-toggle="popover" data-placement="left" data-trigger="hover"
										data-content="<fmt:message key="gamification.awards.time.title" />" ></input>
							</div>
						</div>
						
						<div class="col-lg-4" style="width: 100%;">
							<div class="form-group">
								<label for="timeWindow"><fmt:message key="gamification.frequency.update.time" />:</label>
								<input value="24" type="number" style="right: 40px; position: absolute;" id="taskAttTime" name="start.taskAtt" data-toggle="popover" data-placement="left" data-trigger="hover"
										data-content="<fmt:message key="gamification.frequency.update.time.title" />" ></input>
							</div>
						</div>
						
						<div class="col-lg-4" style="width: 100%;">
							<div class="form-group">
								<label for="timeWindow"><fmt:message key="gamification.frequency.notification" />:</label>
								<input value="2" type="number" style="right: 40px; position: absolute;" id="msgsTime" name="start.msgTimer" data-toggle="popover" data-placement="left" data-trigger="hover"
										data-content="<fmt:message key="gamification.frequency.notification.title" />" ></input>
							</div>
						</div>
					</div>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="close" /></button>
					<button type="submit" class="btn btn-primary"><fmt:message key="save" /></button>
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