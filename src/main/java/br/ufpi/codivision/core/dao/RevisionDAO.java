package br.ufpi.codivision.core.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.Author;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.enums.RepositoryType;

/**
 * @author Werney Ayala
 *
 */
public class RevisionDAO extends GenericDAO<Revision> {

	/**
	 * @param entityClass
	 */
	public RevisionDAO() {
		super(Revision.class);
	}
	
	public Revision findByExternalId(String externalId){
		List<Revision> revs; 
		try{
			revs = em.createQuery("SELECT a FROM Revision a WHERE a.externalId = :externalId ", Revision.class)
					.setParameter("externalId", externalId)
					.getResultList();
			return revs.isEmpty() ? null : revs.get(0);
			
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Integer> getTotalFilesOfRevisions(Long repositoryId){
		return em.createQuery("select revision.totalFiles from Repository as repository, "
				+ "IN(repository.revisions) as revision where repository.id = :repositoryId "
				+ "order by revision.totalFiles asc", Integer.class)
				.setParameter("repositoryId", repositoryId)
				.getResultList();
	}
	
	public String findLastRevision(Long repositoryId) {
		return em.createQuery("select revision.extenalId from Repository as repository, "
				+ "IN(repository.revisions) as revision where repository.id = :repositoryId and "
				+ "revision.date = (select max(revision.date) from Repository as repo, "
				+ "IN(repo.revisions) as rev where repo.id = :repositoryId)", String.class)
				.setParameter("repositoryId", repositoryId)
				.getSingleResult();
	}
	
	public List<String> getRevisionOutliers(Long repositoryId, Integer limiar) {
		return em.createQuery("select revision.externalId from Repository as repository, "
				+ "IN(repository.revisions) as revision where repository.id = :repositoryId and "
				+ "revision.totalFiles > :limiar", String.class)
				.setParameter("repositoryId", repositoryId)
				.setParameter("limiar",  limiar)
				.getResultList();
	}
	
	public List<Long> getIdFromRevisionOutliers(Long repositoryId, List<String> revisionOutliers) {
		return em.createQuery("select revision.id from Repository as repository, IN(repository.revisions) as revision "
				+ "where repository.id = :repositoryId and revision.externalId in (:revisionOutliers)",Long.class)
				.setParameter("repositoryId", repositoryId)
				.setParameter("revisionOutliers", revisionOutliers)
				.getResultList();
	}
	
	public Date getMaxRevisionDate(Long repositoryId) {
		return em.createQuery("select max(revision.date) from Repository as repository, "
				+ "IN(repository.revisions) as revision where repository.id = :repositoryId", Date.class)
				.setParameter("repositoryId", repositoryId)
				.getSingleResult();
	}
	
	public List<Long> getRevisionsToExtract() {
		return em.createQuery("select revision.id from Repository as repository, "
				+ "IN(repository.revisions) as revision where revision.extracted = false "
				+ "and repository.type = :repositoryType",Long.class)
				.setParameter("repositoryType", RepositoryType.GITHUB)
				.getResultList();
	}
	
	public Object[] getRevisionToExtract(Long revisionId) {
		
		try {
			
			Query query = em.createQuery("select repository.name, repository.owner, revision from "
					+ "Repository as repository, IN(repository.revisions) as revision where revision.id = :revisionId ")
					.setParameter("revisionId", revisionId);
			
			Object[] result = (Object[]) query.getSingleResult();
			
			return result;
			
		} catch (NoResultException e) {
			return null;
		}
		
	}

}
