/**
 * 
 */
package br.ufpi.codivision.core.dao;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.Gamification;


public class GamificationDAO extends GenericDAO<Gamification>{

	/**
	 * @param entityClass
	 */
	public GamificationDAO() {
		super(Gamification.class);
	}

}
