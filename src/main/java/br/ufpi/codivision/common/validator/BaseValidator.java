/**
 * 
 */
package br.ufpi.codivision.common.validator;

import javax.inject.Inject;

import br.com.caelum.vraptor.validator.Validator;

/**
 * @author Werney Ayala
 *
 */
public class BaseValidator {
	
	@Inject
	protected Validator validator;
	
	public <T> T onErrorRedirectTo(Class<T> controller) {
		return validator.onErrorRedirectTo(controller);
	}
	
	public <T> T onErrorForwardTo(Class<T> controller) {
		return validator.onErrorForwardTo(controller);
	}
	
}
