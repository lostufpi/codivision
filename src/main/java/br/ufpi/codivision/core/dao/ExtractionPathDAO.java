/**
 * 
 */
package br.ufpi.codivision.core.dao;

import java.util.List;

import javax.persistence.NoResultException;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.Repository;


/**
 * @author Werney Ayala
 *
 */
public class ExtractionPathDAO extends GenericDAO<ExtractionPath>{

	/**
	 * @param entityClass
	 */
	public ExtractionPathDAO() {
		super(ExtractionPath.class);
	}

	
	public List<ExtractionPath> getExtractionPaths(Long repositoryId) {
		return em.createQuery("select paths from Repository as repository, IN(repository.extractionPaths) as paths "
				+ "where repository.id = :repositoryId", ExtractionPath.class)
				.setParameter("repositoryId", repositoryId)
				.getResultList();
	}
	 
	public Repository findRepositoryByExtractionPath(Long extractionPathId){
		try {
			return em.createQuery("select repository from Repository as repository, IN(repository.extractionPaths) extractionPath "
					+ "where extractionPath.id = :pathId", Repository.class)
					.setParameter("pathId", extractionPathId)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public ExtractionPath findById(Long repositoryId, Long extractionPathId) {
		try {
			return em.createQuery("select extractionPath from Repository as repository, IN(repository.extractionPaths) extractionPath "
					+ "where repository.id = :repositoryId and extractionPath.id = :pathId", ExtractionPath.class)
					.setParameter("repositoryId", repositoryId)
					.setParameter("pathId", extractionPathId)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public ExtractionPath findByPath(Long repositoryId, String path) {
		try {
			return em.createQuery("select extractionPath from Repository as repository, IN(repository.extractionPaths) extractionPath "
					+ "where repository.id = :repositoryId and extractionPath.path = :path",ExtractionPath.class)
					.setParameter("repositoryId", repositoryId)
					.setParameter("path", path)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}