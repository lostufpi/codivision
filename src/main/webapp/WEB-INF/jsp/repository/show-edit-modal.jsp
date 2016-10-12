<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="modal fade" id="edit">
	
	<div class="modal-dialog">
		
		<div class="modal-content">
		
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">
					<fmt:message key="repository.name.new" />${repository.name}
				</h4>
			</div>

			<form method="post"
				action="${linkTo[RepositoryController].edit(repository.id)}">
				
				<div class="modal-body">
					<div class="form-group">
						<label for="repositoryName"><fmt:message
								key="repository.name" />:</label> <input type="text"
							class="form-control" id="repositoryName" name="repositoryName"
							value="${repository.name}" required>
					</div>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<fmt:message key="close" />
					</button>
					<button type="submit" class="btn btn-primary">
						<fmt:message key="save" />
					</button>
				</div>
			</form>

		</div>
		
	</div>
	
</div>