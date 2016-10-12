/**
 * 
 */
package br.ufpi.codivision.core.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.UserRepository;

/**
 * @author Werney Ayala
 *
 */
public class UserRepositoryDAO extends GenericDAO<UserRepository>{

	/**
	 * Default contructor
	 */
	public UserRepositoryDAO() {
		super(UserRepository.class);
	}
	
	public UserRepository findByIds(Long userId, Long repositoryId) {
		try{
		return em.createQuery("select ur from UserRepository as ur where ur.user.id = :userId "
				+ "and ur.repository.id = :repositoryId ", UserRepository.class)
				.setParameter("userId", userId)
				.setParameter("repositoryId", repositoryId)
				.getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}
	
	public UserRepository findByLogin(String login, Long repositoryId) {
		
		try{
			return em.createQuery("select ur from UserRepository as ur where ur.user.login = :login and "
					+ "ur.repository.id = :repositoryId",UserRepository.class)
					.setParameter("login", login)
					.setParameter("repositoryId", repositoryId)
					.getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}
	
	public List<UserRepository> listByRepositoryId(Long repositoryId) {
		return em.createQuery("select ur from UserRepository as ur where ur.repository.id = :repositoryId", UserRepository.class)
				.setParameter("repositoryId", repositoryId)
				.getResultList();
	}
	
	public boolean deleteAllByRepository(Long repositoryId){
		Query query = em.createQuery("Delete from UserRepository as ur where ur.repository.id = :repositoryId");
		query.setParameter("repositoryId", repositoryId);

		boolean deleted = false;
		deleted = (query.executeUpdate() > 0);

		return deleted;
	}
	
}
