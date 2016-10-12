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
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.ufpi.codivision.common.annotation.Public;
import br.ufpi.codivision.common.security.UserSession;
import br.ufpi.codivision.core.controller.UserController;

/**
 * @author Werney Ayala
 *
 */
@Intercepts
public class AuthorizationInterceptor {
	
	@Inject
	private UserSession currentUser;
	
	@Inject
	private Result result;
	
	@Accepts
	public boolean accepts(ControllerMethod method) {
		return !method.containsAnnotation(Public.class);
	}
	
	/**
	 * Intercepts the request and checks if there is a user logged in.
	 */
	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {

		/**
		 * You can use the result even in interceptors, but you can't use Validator.onError* methods because
		 * they throw ValidationException.
		 */
		if (!currentUser.isLogged()) {
			// remember added parameters will survive one more request, when there is a redirect
			result.include("errors", Arrays.asList(new SimpleMessage("error", "permission.denied")));
			result.redirectTo(UserController.class).login();
			return;
		}
		stack.next();
	}
	
}
