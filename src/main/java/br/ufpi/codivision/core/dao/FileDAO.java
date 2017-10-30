package br.ufpi.codivision.core.dao;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.debit.model.File;

public class FileDAO extends GenericDAO<File>{
	
	public FileDAO() {
		super(File.class);
	}

}
