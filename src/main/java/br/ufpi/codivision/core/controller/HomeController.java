/**
 * 
 */
package br.ufpi.codivision.core.controller;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.ufpi.codivision.common.annotation.Public;

/**
 * @author Werney Ayala
 *
 */
@Controller
public class HomeController {
	
	
	@Public
	@Get("/")
	public void home() { }
	
}
