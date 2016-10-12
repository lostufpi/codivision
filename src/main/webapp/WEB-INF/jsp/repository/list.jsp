<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<body>


	<div class="col-lg-3">
		<div class="panel panel-default" style="height: 401px;">
			<div class="panel-body">
				<img src="${urlImage}" alt="Avatar">

				<h4 style="text-align: center; margin-bottom: 5px">Informações
					do usuário</h4>
				<hr style="margin-top: 0px; margin-bottom: 0px">
				<h6>
					<b>Nome: </b>${userSession.user.name}</h6>
				<h6>
					<b>Login: </b>${userSession.user.login}</h6>
				<h6>
					<b>Email: </b>${userSession.user.email}</h6>
				<hr style="margin-top: 0px; margin-bottom: 10px">
				<form action="${linkTo[UserController].profile}">
					<button type="submit" class="btn btn-default"
						style="padding: 5px 0px 5px 10px; width: 215px">
						<span
							style="background: url(../img/edit.png) left no-repeat; background-size: 24px 24px; padding: 20px 25px 20px">
							Editar Perfil</span>
					</button>
				</form>
			</div>
		</div>

	</div>



	<div class="col-lg-9">
		<div class="row">
			<div class="panel panel-default">
				<div class="panel-body">
					<h3>
						<fmt:message key="repository.add" />
					</h3>
					<hr>
					<form id="addForm" action="${linkTo[RepositoryController].add}"
						method="post">
						<div class="row">
							<div class="col-lg-4">
								<label for=""><fmt:message key="vcs" />:</label> <select
									class="form-control" id="type" name="repository.type">
									<c:forEach var="option" items="${types}">
										<option id="${option}" value="${option}"><fmt:message
												key="${option.type}" /></option>
									</c:forEach>
								</select>
							</div>
							<div class="col-lg-4">
								<label for="local"><fmt:message key="repository.type" />:</label>
								<select class="form-control" id="local" name="repository.local">
									<option value="false"><fmt:message
											key="repository.remote" /></option>
									<option value="true"><fmt:message
											key="repository.local" /></option>
								</select>
							</div>
							<div class="col-lg-4">
								<label for="path">Extrair a partir de:</label> <input
									type="text" class="form-control" id="path"
									name="extractionPath.path">
							</div>
						</div>

						<div class="form-group" style="margin-top: 15px">
							<label for="name"><fmt:message key="repository.name" />:</label>
							<input type="text" class="form-control" id="name"
								name="repository.name" required>
						</div>

						<div id="div-url" class="form-group">
							<label for="url"><fmt:message key="repository.url" />:</label> <input
								type="text" class="form-control" id="url" name="repository.url">
						</div>

						<hr>
						<button type="submit" class="btn btn-primary pull-right">
							<fmt:message key="add" />
						</button>
					</form>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="panel panel-default">
				<div class="panel-body">
					<h3>
						<fmt:message key="repository.list" />
					</h3>
					<hr>

					<c:choose>
						<c:when test="${repositoryList.size()==0}">
							<h5>Você não possui repositórios cadastrados!</h5>
						</c:when>
						<c:otherwise>
							<table class="table table-bordered">
								<thead>
									<tr>
										<th>#</th>
										<th><fmt:message key="repository.name" /></th>
										<th>
										<c:choose>
											<c:when test="${repository.lastUpdate==null}">Status do Repositório</c:when>
										<c:otherwise>
											<fmt:message key="repository.update.date" /></c:otherwise>
										</c:choose>		
										</th>
										<th class="text-center"><fmt:message key="options" /></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${repositoryList}" var="repository"
										varStatus="s">
										<tr>
											<td>${s.index + 1}</td>
											<td>${repository.name}</td>
											<td>
											
											<c:choose><c:when test="${repository.lastUpdate==null}">Atualizando</c:when>
											<c:otherwise>
											<fmt:formatDate pattern="dd/MM/yyyy" value="${repository.lastUpdate}" /></c:otherwise>
											</c:choose>	
											
											</td>
											<td class="text-center"><a class="btn btn-primary"
												href="${linkTo[RepositoryController].show(repository.id)}"><fmt:message
														key="visualize" /></a> <a
												class="btn btn-danger confirmation-modal"
												data-conf-modal-body="<fmt:message key="repository.delete.dialog" />"
												href="${linkTo[RepositoryController].delete(repository.id)}"><fmt:message
														key="remove" /> </a></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:otherwise>
					</c:choose>




				</div>
			</div>
		</div>

	</div>

	<script>
		$(document).ready(function() {

			$('#type').change(showdata);
			showdata();

			function showdata() {
				if ($('#type option:selected').attr('id') == 'SVN') {
					$('#local').removeAttr('disabled');
					$('#path').removeAttr('readonly');
					$('#path').val("/trunk");
					$('#name').removeAttr('disabled');
				} else {
					$('#local').val("false").attr('selected', 'selected');
					$('#local').attr('disabled', 'disabled');
					$('#path').val("/master");
					$('#path').attr('readonly', 'readonly');
					$('#name').val(" ");
					$('#name').attr('disabled', 'disabled');
				}
			}
		});
	</script>

</body>