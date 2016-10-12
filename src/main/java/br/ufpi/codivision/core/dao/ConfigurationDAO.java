/**
 * 
 */
package br.ufpi.codivision.core.dao;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.Configuration;

/**
 * @author Werney Ayala
 *
 */
public class ConfigurationDAO extends GenericDAO<Configuration>{

	/**
	 * @param entityClass
	 */
	public ConfigurationDAO() {
		super(Configuration.class);
	}

}
