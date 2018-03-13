package br.ufpi.codivision.core.dao;

import java.util.List;

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
		List<Author> authors; 
		try{
			authors = em.createQuery("SELECT a FROM Author a WHERE a.email = :email ", Author.class)
					.setParameter("email", email)
					.getResultList();
			return authors.isEmpty() ? null : authors.get(0);
			
		} catch (NoResultException e) {
			return null;
		}
	}
	public Author findByName(String name) {
		List<Author> authors; 
		try{
			authors = em.createQuery("SELECT a FROM Author a WHERE a.name = :name ", Author.class)
					.setParameter("name", name)
					.getResultList();
			return authors.isEmpty() ? null : authors.get(0);
			
		} catch (NoResultException e) {
			return null;
		}	
	}
}
