/**
 * 
 */
package br.ufpi.codivision.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.ufpi.codivision.core.model.enums.PermissionType;

/**
 * @author Werney Ayala
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	
	PermissionType value();

}
