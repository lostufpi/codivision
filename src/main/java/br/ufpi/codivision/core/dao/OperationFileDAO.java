/**
 * 
 */
package br.ufpi.codivision.core.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.OperationFile;

/**
 * @author Werney Ayala
 *
 */
public class OperationFileDAO extends GenericDAO<OperationFile>{

	/**
	 * @param entityClass
	 */
	public OperationFileDAO() {
		super(OperationFile.class);
	}
	
	public Object[] getNotExtractedFileById(Long id){
		
		try{
		
		Query query = em.createQuery("select repository.url, revision.externalId, file, repository.repositoryRoot from Repository as repository, "
					+ "IN(repository.revisions) as revision, IN(revision.files) as file where file.extracted = false and file.id = :id")
					.setParameter("id", id);
			Object[] result = (Object[]) query.getSingleResult();
			
			return result;
			
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Long> getNotExtractedFiles(){
		return em.createQuery("select file.id from Repository as repository, IN(repository.revisions) as revision, "
				+ "IN(revision.files) as file where file.extracted = false", Long.class)
				.setFirstResult(0)
				.setMaxResults(1000)
				.getResultList();
	}
	
}
