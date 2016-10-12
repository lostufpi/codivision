/**
 * 
 */
package br.ufpi.codivision.core.dao;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.DirTree;

/**
 * @author Werney Ayala
 *
 */
public class DirTreeDAO extends GenericDAO<DirTree>{

	/**
	 * @param entityClass
	 */
	public DirTreeDAO() {
		super(DirTree.class);
	}
	
}
