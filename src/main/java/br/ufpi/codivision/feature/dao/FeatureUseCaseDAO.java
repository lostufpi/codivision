package br.ufpi.codivision.feature.dao;

import javax.persistence.Query;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.feature.common.model.FeatureUseCase;

public class FeatureUseCaseDAO extends GenericDAO<FeatureUseCase> {
	public FeatureUseCaseDAO() {
		super(FeatureUseCase.class);
	}
	
	public void removeFeatureUseCaseByUseCaseId(Long useCaseId) {
		try {
			Query query = this.em.createQuery("DELETE FeatureUseCase WHERE useCaseId = :useCaseId");
			query.setParameter("useCaseId", useCaseId);
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeFeatureUseCaseByFeatureId(Long featureId) {
		try {
			Query query = this.em.createQuery("DELETE FeatureUseCase WHERE featureId = :featureId");
			query.setParameter("featureId", featureId);
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
