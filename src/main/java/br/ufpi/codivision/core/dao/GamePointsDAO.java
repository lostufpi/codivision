/**
 * 
 */
package br.ufpi.codivision.core.dao;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.GamePoints;


public class GamePointsDAO extends GenericDAO<GamePoints>{

	/**
	 * @param entityClass
	 */
	public GamePointsDAO() {
		super(GamePoints.class);
	}

}
