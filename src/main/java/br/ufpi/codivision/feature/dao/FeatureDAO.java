package br.ufpi.codivision.feature.dao;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.feature.common.model.Feature;

public class FeatureDAO extends GenericDAO<Feature>{
	
	public FeatureDAO() {
		super(Feature.class);
	}
}
