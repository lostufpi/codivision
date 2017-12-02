package br.ufpi.codivision.core.dao;

import javax.persistence.NoResultException;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.core.model.Author;

public class AuthorDAO extends GenericDAO<Author>{
	
	/**
	 * @param entityClass
	 */
	public AuthorDAO() {
		super(Author.class);
	}
	
	public Author findByEmail(String email){
		try{
			return em.createQuery("SELECT a FROM Author a WHERE a.email = :email ", Author.class)
					.setParameter("email", email)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
