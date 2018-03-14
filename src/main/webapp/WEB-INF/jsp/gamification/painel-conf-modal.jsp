<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="modal fade" id="config">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">Configurações</h4>
			</div>
			
			<form action="${linkTo[RepositoryController].config(repository.id, extractionPath.id)}" method="post">
				<div class="modal-body">

					<div class="row">
					
						<div class="col-lg-4">
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
						
						<div class="col-lg-4">
							<div class="form-group date">
								<fmt:formatDate pattern="dd/MM/yyyy"
									value="${configuration.initDate}" var="initDate" />
								<label for="initDate"><fmt:message key="repository.config.date.init" />:</label> <input type="text"
									class="form-control datepicker" id="initDate" placeholder="Data inicial"
									name="configuration.initDate">
							</div>
						</div>
						
						<div class="col-lg-4">
							<div class="form-group date">
								<fmt:formatDate pattern="dd/MM/yyyy"
									value="${configuration.endDate}" var="endDate" />
								<label for="endDate"><fmt:message key="repository.config.date.end" />:</label> <input type="text"
									class="form-control datepicker" id="endDate" placeholder="Data final"
									name="configuration.endDate">
							</div>
						</div>
						
					</div>

					<hr>

					<h4><fmt:message key="repository.config.degradation" />:</h4>
					
					<div class="row">
					
					
						<div class="col-lg-6">
							<div class="form-group">
								<label for="monthlyReduction"><fmt:message key="repository.config.degradation.monthly" />:</label> 
								<div class="input-group">
									<input type="number" class="form-control" id="monthlyReduction"
										placeholder="Redução mensal" name="configuration.monthlyDegradation"
										value="${configuration.monthlyDegradation}"  data-container="body" 
										data-toggle="popover" data-placement="left" data-trigger="hover"
										data-content="<fmt:message key="repository.config.monthly.popover" />">
									<div class="input-group-addon">%</div>
								</div>
							</div>
						</div>
						
						<div class="col-lg-6">
							<div class="form-group">
								<label for="changeReduction"><fmt:message key="repository.config.degradation.change" />:</label> 
								<div class="input-group">
									<input type="number" class="form-control" id="changeReduction"
										placeholder="Redução por alteração" name="configuration.changeDegradation"
										value="${configuration.changeDegradation}" data-container="body" 
										data-toggle="popover" data-placement="right" data-trigger="hover"
										data-content="<fmt:message key="repository.config.change.popover" />">
									<div class="input-group-addon">%</div>
								</div>
							</div>
						</div>
						
					</div>
					
					<hr>
					
					<h4><fmt:message key="repository.config.threshold" />:</h4>
					
					<div class="row">
						
						<div class="col-lg-4">
							<div class="form-group">
								<label for="add"><fmt:message key="repository.config.threshold.alert" />:</label> 
								<div class="input-group"> 
								<input type="number"
									class="form-control" id="alert" placeholder="Peso para adição"
									name="configuration.alertThreshold"
									value="${configuration.alertThreshold}">
									<div class="input-group-addon">%</div>
								</div>
							</div>
						</div>
						
						<div class="col-lg-4 col-lg-offset-2">
							<div class="form-group">
								<label for="add"><fmt:message key="repository.config.threshold.existence" />:</label>
								<div class="input-group"> 
								<input type="number"
									class="form-control" id="existence" placeholder="Peso para adição"
									name="configuration.existenceThreshold"
									value="${configuration.existenceThreshold}">
									<div class="input-group-addon">%</div>
								</div>
							</div>
						</div>
						
						<div class="col-lg-4">
							<div class="form-group">
								<label for="add"><fmt:message key="repository.config.threshold.truck_factor" />:</label>
								<div class="input-group"> 
								<input type="number"
									class="form-control" id="truckfactor" placeholder="Peso para adição"
									name="configuration.truckFactorThreshold"
									value="${configuration.truckFactorThreshold}">
									<div class="input-group-addon">%</div>
								</div>
							</div>
						</div>
						
					</div>

					<hr>
					
					<h4><fmt:message key="repository.config.weight" />:</h4>

					<div class="row">
					
						<div class="col-lg-3">
							<div class="form-group">
								<label for="add"><fmt:message key="repository.config.weight.add" />:</label> <input type="number"
									class="form-control" id="add" placeholder="Peso para adição"
									name="configuration.addWeight"
									value="${configuration.addWeight}">
							</div>
						</div>
						
						<div class="col-lg-3">
							<div class="form-group">
								<label for="mod"><fmt:message key="repository.config.weight.mod" />:</label> <input
									type="number" class="form-control" id="mod"
									placeholder="Peso para modificação"
									name="configuration.modWeight"
									value="${configuration.modWeight}">
							</div>
						</div>
						
						<div class="col-lg-3">
							<div class="form-group">
								<label for="del"><fmt:message key="repository.config.weight.del" />:</label> <input type="number"
									class="form-control" id="del" placeholder="Peso para deleção"
									name="configuration.delWeight"
									value="${configuration.delWeight}">
							</div>
						</div>
						
						<div class="col-lg-3">
							<div class="form-group">
								<label for="del"><fmt:message key="repository.config.weight.condition" />:</label> <input type="number"
									class="form-control" id="if" placeholder="Peso para condição"
									name="configuration.conditionWeight"
									value="${configuration.conditionWeight}">
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