package br.ufpi.codivision.feature.dao;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.feature.common.model.UseCase;

public class UseCaseDAO extends GenericDAO<UseCase> {
	public UseCaseDAO() {
		super(UseCase.class);
	}
}
