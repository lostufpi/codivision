/**
 * 
 */
package br.ufpi.codivision.common.interceptor;

import java.util.Arrays;

import javax.inject.Inject;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ValuedParameter;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.ufpi.codivision.common.annotation.Permission;
import br.ufpi.codivision.common.security.UserSession;
import br.ufpi.codivision.core.controller.RepositoryController;
import br.ufpi.codivision.core.dao.UserRepositoryDAO;
import br.ufpi.codivision.core.model.UserRepository;
import br.ufpi.codivision.core.model.enums.PermissionType;

/**
 * @author Werney Ayala
 *
 */
@Intercepts
public class PermissionInterceptor {
	
	@Inject private UserSession currentUser;
	@Inject private UserRepositoryDAO userRepositoryDAO;
	@Inject private MethodInfo methodInfo;
	@Inject private Result result;
	        private Permission permission;
	
	@Accepts
	public boolean accepts(ControllerMethod method) {
		this.permission = method.getMethod().getAnnotation(Permission.class);
		return method.containsAnnotation(Permission.class);
	}
	
	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {
		
		Long repositoryId = 0l;
		ValuedParameter[] parameters = methodInfo.getValuedParameters();
		for (ValuedParameter parameter : parameters) {
			if(parameter.getName().equals("repositoryId"))
				repositoryId = (Long) parameter.getValue();
		}
		
		UserRepository userRepository = userRepositoryDAO.findByIds(currentUser.getUser().getId(), repositoryId);
		if (permission.value().equals(PermissionType.OWNER) && ((userRepository == null) || (userRepository.getPermission().equals(PermissionType.MEMBER)))) {
			result.include("errors", Arrays.asList(new SimpleMessage("error", "permission.denied")));
			result.redirectTo(RepositoryController.class).list();
			return;
		} else if (userRepository == null) {
			result.include("errors", Arrays.asList(new SimpleMessage("error", "permission.denied")));
			result.redirectTo(RepositoryController.class).list();
			return;
		}
		
		stack.next();
		
	}
}
