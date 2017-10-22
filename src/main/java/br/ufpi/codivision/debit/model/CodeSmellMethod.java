package br.ufpi.codivision.debit.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.debit.codesmell.CodeSmellID;

@Entity
public class CodeSmellMethod implements PersistenceEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private CodeSmellID codeSmellType;

	public CodeSmellID getCodeSmellType() {
		return codeSmellType;
	}

	public void setCodeSmellType(CodeSmellID codeSmellType) {
		this.codeSmellType = codeSmellType;
	}

	@Override
	public String toString() {
		return  "<br>" + codeSmellType;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub
		
	}
	
	

}
