<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="modal fade bs-example-modal-sm" id="use-case-name-modal" data-backdrop="static">
	<div class="modal-dialog modal-sm" data-backdrop="static">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">
					Identificação do Caso de Uso 
				</h4>
			</div>
			
			<div class="modal-body">
				<div class="form-group">
					<label>Nome:</label> <input type="text"
						class="form-control" id="usecase_name" required>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<fmt:message key="close" />
				</button>
				<button id='btn_add_usecase' data-dismiss="modal" class="btn btn-primary">
					Cadastrar
				</button>
			</div>
		</div>
	</div>
</div>