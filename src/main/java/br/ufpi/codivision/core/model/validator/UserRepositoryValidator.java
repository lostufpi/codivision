/**
 * 
 */
package br.ufpi.codivision.core.model.validator;

import javax.inject.Inject;

import br.com.caelum.vraptor.validator.SimpleMessage;
import br.ufpi.codivision.common.validator.BaseValidator;
import br.ufpi.codivision.core.dao.UserRepositoryDAO;
import br.ufpi.codivision.core.model.UserRepository;
import br.ufpi.codivision.core.model.enums.PermissionType;

/**
 * @author Werney Ayala
 *
 */
public class UserRepositoryValidator extends BaseValidator {
	
	@Inject UserRepositoryDAO dao;
	
	public void validateRemove(Long userRepositoryId){
		UserRepository userRepository = dao.findById(userRepositoryId);
		if(userRepository.getPermission() == PermissionType.OWNER)
			validator.add(new SimpleMessage("error", "repository.permission.owner.error"));
	}
	
	public void validateAdd(String login, Long repositoryId) {
		validator.check(dao.findByLogin(login, repositoryId) == null, new SimpleMessage("error","repository.permission.add.error"));
	}
	
}
