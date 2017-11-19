package br.ufpi.codivision.feature.dao;

import br.ufpi.codivision.common.dao.GenericDAO;
import br.ufpi.codivision.feature.common.model.Element;

public class ElementDAO extends GenericDAO<Element>{
	
	public ElementDAO() {
		super(Element.class);
	} 
}
