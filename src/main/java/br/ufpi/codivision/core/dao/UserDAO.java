/**
 * 
 */
package br.ufpi.codivision.core.dao;

import java.util.List;

import javax.persistence.NoResultException;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.User;

/**
 * @author Werney Ayala
 *
 */
public class UserDAO extends GenericDAO<User>{

	/**
	 * @param entityClass
	 */
	public UserDAO() {
		super(User.class);
	}
	
	public User find(User user){
		try{
			return em.createQuery("select user from User as user where user.login = :login "
					+ "and user.password = :password", User.class)
					.setParameter("login", user.getLogin())
					.setParameter("password", user.getPassword())
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	//returns an object with this login
	public User findByLogin(String login){
		try{
			return em.createQuery("select user from User as user where user.login = :login ", User.class)
					.setParameter("login", login)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		
	}
	
	public List<User> listByRepository(Long repositoryId) {
		return em.createQuery("select ur.user from UserRepository ur where ur.repository.id = :repositoryId", User.class).setParameter("repositoryId", repositoryId).getResultList();
	}
	
	public User findByRecoveryCode(String code){
		try{
			return em.createQuery("select user from User as user where user.recoveryCode = :recoveryCode ", User.class)
					.setParameter("recoveryCode", code)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public User findByEmail(String email){
		try{
			return em.createQuery("select user from User as user where user.email = :email ", User.class)
					.setParameter("email", email)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
}
