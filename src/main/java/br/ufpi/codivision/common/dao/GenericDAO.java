/**
 * 
 */
package br.ufpi.codivision.common.dao;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;

import br.com.caelum.vraptor.ioc.Container;
import br.ufpi.codivision.common.model.PersistenceEntity;

/**
 * @author Werney Ayala
 *
 */
public abstract class GenericDAO<T extends PersistenceEntity> {

	/**
	 * entityClass
	 */
	private Class<T> entityClass;

	/**
	 * container
	 */
	protected Container container;
	
	@Inject /* Injected by vraptor-jpa using the persistence-unit = default */
	protected EntityManager em; 

	/**
	 * Default constructor 
	 * @param entityClass
	 */
	public GenericDAO(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * Get Container
	 * @return container
	 */
	public Container getContainer() {
		return container;
	}

	/**
	 * Set container
	 * @param container
	 */
	public void setContainer(Container container) {
		this.container = container;
	}
	
	/**
	 * Set Entity Manager
	 * @param em
	 */
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	/**
	 * Adds or updates an entity and returns after operation
	 * 
	 * @param entity
	 * @return entity
	 * @throws Exception
	 */
	public T save(T entity) {
		
		if (entity.getId() != null && entity.getId() == 0) {
			entity.setId(null);
		}

		entity = em.merge(entity);
		return entity;
	}

	/**
	 * Deletes an entity by id
	 * 
	 * @param entityId
	 * @return true or false
	 * @throws Exception
	 */
	public boolean delete(Long entityId) {
		Query query = em.createQuery("Delete from " + entityClass.getSimpleName() + " en where en.id = :id");
		query.setParameter("id", entityId);

		boolean deleted = false;
		deleted = (query.executeUpdate() > 0);

		return deleted;
	}

	/**
	 * Remove
	 * 
	 * @param object
	 * 
	 */
	public final void delete(T object) {
		em.remove(object);
	}

	/**
	 * Find all
	 * 
	 * @return collection of entity
	 */
	@SuppressWarnings("unchecked")
	public Collection<T> findAll() {
		Query query = em.createQuery("from " + entityClass.getSimpleName());
		Collection<T> entities = query.getResultList();
		return entities;
	}

	/**
	 * Find by id
	 * 
	 * @param id
	 * @return entity
	 * */
	public T findById(Long id) {
		T result = null;
		try {
			result = em.find(entityClass, id);
		} catch (RuntimeException runtimeEx) {
			runtimeEx.printStackTrace();
		}
		return result;
	}

	/**
	 * Count All
	 * 
	 * @return the number of instances
	 * */
	public Long countAll() {
		return countByCriteria();
	}

	/**
	 * Count by example
	 * 
	 * @param exampleInstance
	 * @return the number of instances according to the example
	 * */
	public int countByExample(final T exampleInstance) {
		Session session = (Session) em.getDelegate();
		Criteria crit = session.createCriteria(getEntityClass());
		crit.setProjection(Projections.rowCount());
		crit.add(Example.create(exampleInstance));

		return (Integer) crit.list().get(0);
	}

	/**
	 * Find by example
	 * 
	 * @param exampleInstance
	 * @return the instances according to the example
	 * */
	@SuppressWarnings("unchecked")
	public List<T> findByExample(final T exampleInstance) {
		Session session = (Session) em.getDelegate();
		Criteria crit = session.createCriteria(getEntityClass());
		final List<T> result = crit.list();
		return result;
	}

	/**
	 * Count by criteria
	 * 
	 * @param criterion
	 * @return the number of instances according to the criterion
	 * */
	public Long countByCriteria(Criterion... criterion) {
		Session session = (Session) em.getDelegate();
		Criteria crit = session.createCriteria(getEntityClass());
		crit.setProjection(Projections.rowCount());

		for (final Criterion c : criterion) {
			crit.add(c);
		}

		return (Long) crit.list().get(0);
	}

	/**
	 * Find by criteria
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param criterion
	 * @return the instances according to the criteria
	 * */
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(final int firstResult, final int maxResults,
			final Criterion... criterion) {
		Session session = (Session) em.getDelegate();
		Criteria crit = session.createCriteria(getEntityClass());

		for (final Criterion c : criterion) {
			crit.add(c);
		}

		if (firstResult > 0) {
			crit.setFirstResult(firstResult);
		}

		if (maxResults > 0) {
			crit.setMaxResults(maxResults);
		}

		final List<T> result = crit.list();
		return result;
	}

	/**
	 * Find by criteria
	 * 
	 * @param criterion
	 * @return the instances according to the criteria
	 * */
	public List<T> findByCriteria(final Criterion... criterion) {
		return findByCriteria(-1, -1, criterion);
	}

	/**
	 * Execute the flush
	 * 
	 */
	public final void flush() {
		em.flush();
	}

	/**
	 * Get Entity Class
	 * 
	 * @return the entity class
	 * */
	public Class<T> getEntityClass() {
		return entityClass;
	}

	/**
	 * Create an object of type Criteria
	 * 
	 * @return the criteria
	 */
	public final Criteria createCriteria() {
		Session session = (Session) em.getDelegate();
		return session.createCriteria(getEntityClass());
	}

	/**
	 * Create an Example
	 * 
	 * @param object
	 * @return the object of type Example
	 */
	public final Example createExample(T object) {
		Example example = Example.create(object);
		example.enableLike(MatchMode.ANYWHERE);
		example.excludeZeroes();
		example.ignoreCase();
		return example;
	}

}
