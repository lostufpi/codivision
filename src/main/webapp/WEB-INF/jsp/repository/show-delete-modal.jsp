<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<div class="modal fade" id="delete">
	
	<div class="modal-dialog">
		
		<div class="modal-content">
			
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">
					<fmt:message key="confirm.delete" />
				</h4>
			</div>
			
			<div class="modal-body">
				<p><fmt:message key="repository.delete.dialog" /></p>
			</div>
			
			<div class="modal-footer">

					<button type="button" class="btn btn-default" data-dismiss="modal">
						<fmt:message key="close" />
					</button>
					<a class="btn btn-danger" href="${linkTo[RepositoryController].delete(repository.id)}">
						<fmt:message key="delete" />
					</a>
					
			</div>
			
		</div>

	</div>

</div>