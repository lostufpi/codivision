<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="modal fade bs-example-modal-sm" id="credential" data-backdrop="static">
	
	<div class="modal-dialog modal-sm" data-backdrop="static">
		
		<div class="modal-content">
		
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">
					Acesse sua conta no reposit�rio
				</h4>
			</div>

			
				
				<div class="modal-body">
					<div class="form-group">
						<label >Login:</label> <input type="text"
							class="form-control" id="login" required>
					</div>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<label >Senha:</label> <input type="password"
							class="form-control" id="password" required>
					</div>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<fmt:message key="close" />
					</button>
					<button id='btn_save' data-dismiss="modal" class="btn btn-primary">
						Logar
					</button>
				</div>
			

		</div>
		
	</div>
	
</div>