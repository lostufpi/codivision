package br.ufpi.codivision.feature.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.feature.common.model.FeatureElement;

public class FeatureElementDAO extends GenericDAO<FeatureElement> {
	
	public FeatureElementDAO() {
		super(FeatureElement.class);
	} 
	
	public List<FeatureElement> searchFeatureElementByFeatureId(Long featureId) {
		try {
			TypedQuery<FeatureElement> query = this.em.createQuery("SELECT f FROM FeatureElement f WHERE f.feature.id = :featureId", FeatureElement.class);
			query.setParameter("featureId", featureId);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
