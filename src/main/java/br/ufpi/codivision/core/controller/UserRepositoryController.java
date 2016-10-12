/**
 * 
 */
package br.ufpi.codivision.core.controller;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.ufpi.codivision.common.annotation.Permission;
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.dao.UserRepositoryDAO;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.User;
import br.ufpi.codivision.core.model.UserRepository;
import br.ufpi.codivision.core.model.enums.PermissionType;
import br.ufpi.codivision.core.model.validator.UserRepositoryValidator;
import br.ufpi.codivision.core.model.validator.UserValidator;

/**
 * @author Werney Ayala
 *
 */
@Controller
public class UserRepositoryController {
	
	@Inject private Result result;
	
	@Inject private UserRepositoryDAO dao;
	@Inject private RepositoryDAO repositoryDAO;
	
	@Inject private UserValidator userValidator;
	@Inject private UserRepositoryValidator validator;
	
	/**
	 * This method adds a user to the repository
	 * The User added will be able view the repository
	 * @param repositoryId - The id of the target repository
	 * @param userId - The id of the User will be able to view the repository
	 */
	@Permission(PermissionType.OWNER)
	@Post("/repository/{repositoryId}/permission")
	public void add(Long repositoryId, String login){
		
		validator.validateAdd(login, repositoryId);
		validator.onErrorRedirectTo(RepositoryController.class).show(repositoryId);
		
		Repository repository = repositoryDAO.findById(repositoryId);
		User user = userValidator.validateFindByLogin(login);
		userValidator.onErrorRedirectTo(RepositoryController.class).show(repositoryId);
		
		UserRepository permission = new UserRepository();
		permission.setPermission(PermissionType.MEMBER);
		permission.setRepository(repository);
		permission.setUser(user);
		dao.save(permission);
		
		result.redirectTo(RepositoryController.class).show(repositoryId);
		
	}
	
	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}/permission/{permissionId}/remove")
	public void remove(Long repositoryId, Long permissionId){
		
		validator.validateRemove(permissionId);
		validator.onErrorRedirectTo(RepositoryController.class).show(repositoryId);
		
		dao.delete(permissionId);
		result.redirectTo(RepositoryController.class).show(repositoryId);
		
	}
	
}
