package br.ufpi.codivision.feature.dao;

import java.util.List;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.feature.common.model.UseCase;

public class UseCaseDAO extends GenericDAO<UseCase> {
	
	public UseCaseDAO() {
		super(UseCase.class);
	}
	
	public List<UseCase> useCasesOrderByName(){
		return em.createQuery("SELECT uc FROM UseCase uc ORDER BY uc.name ASC", UseCase.class).getResultList();
	}
}
