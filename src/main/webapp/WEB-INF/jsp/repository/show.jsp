<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<body>

	<div class="row">

		<div class="col-lg-3">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h5 style="text-align: center"><b><fmt:message
								key="repository.list" /></b></h5>
				</div>
				<!-- /.panel-heading -->
				<div class="panel-body">
					<div class="list-group text-center">
						<c:forEach var="repository" items="${repositoryList}">
							<a class="list-group-item"
								href="${linkTo[RepositoryController].show(repository.id)}">${repository.name}</a>
						</c:forEach>
					</div>
					<a class="btn btn-default btn-block"
						href="${linkTo[RepositoryController].list}"><fmt:message
							key="repository.add" /></a>
				</div>
			</div>
		</div>

		<div class="col-lg-9">

			<!-- Panel show -->
			<div class="panel panel-default">
				<div class="panel-body">

					<h3><fmt:message key="repository.info" /></h3>
					<hr>

					<div class="row">
						<div class="col-lg-4 text-right">
							<p class="text-right"><strong><fmt:message key="user.name" />: </strong></p>
							<p class="text-right"><strong><fmt:message key="repository.url" />: </strong></p>
							<p class="text-right"><strong><fmt:message key="repository.type" />: </strong></p>
							<p class="text-right"><strong>Branch: </strong></p>
							<p class="text-right"><strong>
							
							<c:choose>
								<c:when test="${repository.lastUpdate==null}">Status do Repositório</c:when>
							<c:otherwise>
								<fmt:message key="repository.update.date" /></c:otherwise>
							</c:choose>	
							:</strong></p>
						</div>
						<div class="col-lg-6">
							<p>${repository.name}</p>
							<p>${repository.url}</p>
							<p>${repository.branchName}</p>
							<c:choose>
								<c:when test="${repository.local==true}">
									<p><fmt:message key="repository.local" /></p>
								</c:when>
								<c:otherwise>
									<p><fmt:message key="repository.remote" /></p>
								</c:otherwise>
							</c:choose>
							<p>
							<c:choose>
								<c:when test="${repository.lastUpdate==null}">Atualizando</c:when>
							<c:otherwise>
								<fmt:formatDate pattern="dd/MM/yyyy" value="${repository.lastUpdate}" /></c:otherwise>
							</c:choose>	
							</p>
						</div>
					</div>

					<hr>

					<div>
						<button class="btn btn-danger pull-right" data-toggle="modal"
							data-target="#delete">
							<fmt:message key="delete" />
						</button>
						<jsp:include page="show-delete-modal.jsp" />
					</div>

					<div>
						<button class="btn btn-primary pull-right" data-toggle="modal"
							data-target="#edit" style="margin-right: 5px">
							<fmt:message key="change" />
						</button>
						<jsp:include page="show-edit-modal.jsp" />
					</div>
					<form method="post" action="${linkTo[RepositoryController].update(repository.id)}">
						<button class="btn btn-primary pull-right" style="margin-right: 5px">Update</button>
					</form>
					<form method="get" action="${linkTo[RepositoryController].chart(repository.id)}">
						<button class="btn btn-primary pull-right" style="margin-right: 5px">Familiaridade</button>
					</form>
					<form method="get" action="${linkTo[RepositoryController].testInformation(repository.id)}">
						<button class="btn btn-primary pull-right" style="margin-right: 5px" >Contribuição</button>
					</form>
					<form method="get" action="${linkTo[TechnicalDebtController].infoTD(repository.id)}">
						<button class="btn btn-primary pull-right" style="margin-right: 5px" >Dívida Técnica</button>
					</form>
					<form method="get" action="${linkTo[FeatureController].features(repository.id)}">
						<button class="btn btn-primary pull-right" style="margin-right: 5px" >Features</button>
					</form>
					<form method="get" action="${linkTo[GamificationController].painel(repository.id)}">
						<button class="btn btn-primary pull-right" style="margin-right: 5px" >Gamification</button>
					</form>
				</div>
			</div>
			
			<!-- Panel repository members -->
			<div class="panel panel-default">
				<div class="panel-heading">
					<div>
						<b><fmt:message key="repository.members" /></b>
					</div>
				</div>
				<div class="panel-body">

					<!-- Form add user -->
					<div>
						<form class="form-inline"
							action="${linkTo[UserRepositoryController].add(repository.id, login)}"
							method="POST">
							<div class="form-group">
								<label for="login"><fmt:message
										key="repository.member.add" />:</label> <input type="text"
									class="form-control" id="login" placeholder="Login do usuário"
									name="login" required>
							</div>
							<div class="form-group">
								<label for="login" class="sr-only"><fmt:message
										key="member.add" />:</label> <input type="hidden"
									class="form-control" id="repositoryId" name="repositoryId"
									value="${repository.id}">
							</div>
							<button type="submit" class="btn btn-primary">
								<fmt:message key="add" />
							</button>
						</form>
					</div>

					<!-- Table repository members -->
					<div style="margin-top: 10px">
						<table class="table table-bordered">
							<thead>
								<tr>
									<th>#</th>
									<th><fmt:message key="repository.members" /></th>
									<th><fmt:message key="permission" /></th>
									<th><fmt:message key="options" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${userRepositoryList}" var="member"
									varStatus="s">
									<tr>
										<td>${s.index + 1}</td>
										<td>${member.user.name}</td>
										<td><fmt:message key="${member.permission.type}" /></td>
										<td class="text-center"><a class="btn btn-danger"
											href="${linkTo[UserRepositoryController].remove(member.repository.id, member.id)}"><fmt:message
													key="remove" /></a></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>

					</div>
					
				</div>
			</div>
			<div class="panel panel-default">
				<div class="panel-heading">
					<div>
						<b><fmt:message key="repository.authors" /></b>
					</div>
				</div>
					<div class="panel-body">
					 
					<div style="margin-top: 10px">
						<table class="table table-bordered">
							<thead>
								<tr>
									<th>#</th>
									<th><fmt:message key="repository.authors" /></th>
									<th><fmt:message key="user.email" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${authors}" var="member"
									varStatus="s">
									<tr>
										<td>${s.index + 1}</td>
										<td>${member.name}</td>
										<td>${member.email}</td>
										</tr>
								</c:forEach>		
							</tbody>
						</table>
						</div>		
<!-- Parte que altera email dos autores -->								
<div>

	<button class="btn btn-primary pull-right" data-toggle="modal"
		data-target="#editAuthorEmail" style="margin-right: 5px">
		<fmt:message key="change.email" />
	</button>
	<div class="modal fade" id="editAuthorEmail">
	
<div class="modal-dialog">
		
		<div class="modal-content">
		
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">
					<fmt:message key="author.change.email" />
				</h4>
			</div>
			<form method="post"
				action="${linkTo[RepositoryController].changemail(repository.id)}">
				<div class="modal-body">
					<div class="form-group">
						<label for="repositoryName"><fmt:message
								key="author.name" />:</label> 
						<select name="authorName" id="authorName">
						<c:forEach items="${authors}" var="member">
						<option value="${member.id}">${member.name}</option>		
										
							</c:forEach>			
						</select>
					</div>
					<div class="form-group">
						<label for="repositoryName"><fmt:message
								key="author.email" />:</label> <input type="email"
							class="form-control" id="newEmail" name="newEmail" required>
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
				</div>	
				
				
<!--  Botão que Une autores repetidos -->
				
<div>

	<button class="btn btn-primary pull-right" data-toggle="modal"
		data-target="#editAuthor" style="margin-right: 5px">
		<fmt:message key="author.edit" />
	</button>
	<div class="modal fade" id="editAuthor">
	
<div class="modal-dialog">
		
		<div class="modal-content">
		
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">
					<fmt:message key="author.edit.father" />
				</h4>
			</div>
			<form method="post"
				action="${linkTo[RepositoryController].removeAuthor(repository.id)}">
				<div class="modal-body">
					<div class="form-group">
						<label for="repositoryName"><fmt:message
								key="author.father" />:</label> 
						<select name="authorName" id="authorName">
						<c:forEach items="${authors}" var="member">
						<option value="${member.id}">${member.name}</option>		
										
							</c:forEach>			
						</select>
					</div>
					
						
					<div class="form-group">
						<label for="repositoryName"><fmt:message
								key="author.father.others" />:</label> 
								<p><c:forEach items="${authors}" var="member" varStatus="s">
						<input id="authors${s.index}" type="checkbox" value="${member.id}">${member.name}					
						</c:forEach></p>
						<input type="hidden" 
							class="form-control" id="removeAuthors" value="" name="removeAuthors">
					</div>

				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<fmt:message key="close" />
					</button>
					<button type="submit" onclick="generateAuthors()" class="btn btn-primary">
						<fmt:message key="save" />
					</button>
				</div>
			</form>

		</div>
		
	</div>
</div>
				</div>	
				

				</div>
		</div>
	</div>
	</div>
<script>
var removeA = document.getElementById("removeAuthors");


function generateAuthors(){
	removeA.value=""
	var aux;
	var i=0;
	while(document.getElementById("authors"+i)!=null){
		aux=document.getElementById("authors"+i);
		if (aux.checked){
			if(removeA.value==""){
				removeA.value=aux.value;
			}
			else {
				removeA.value=removeA.value +";" + aux.value;
			}
		}
		i=i+1;
	}
}


//var select = document.getElementById("authorName");
//var choice = select.selectedIndex;
//var i=0;
//while(document.getElementById("authors"+i)!=null){
//	aux=document.getElementById("authors"+i);
//	if(i==choice){
//		aux.disabled="disabled";
//	}
//	i=i+1;
//}



</script>

</body>



