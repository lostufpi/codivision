package br.ufpi.codivision.feature.common.model;

import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.core.util.Constants;


/**
 * @author Vanderson Moura
 * 
 * CLASSE ABSTRATA QUE REPRESENTADA UM ELEMENTO (CLASSE JAVA OU ARQUIVO .c (LINGUAGEM C), POR EXEMPLO) QUE CONSTITUI UMA FEATURE
 * EM QUALQUER QUE SEJA A LINGUAGEM DE PROGRAMAÇÃO, OS SEUS ELEMENTOS DEVERÃO EXTENDER DESTA CLASSE   
 * UM ELEMENTO TERÁ SEMPRE UM NOME (arquivo.java, arquivo.c, arquivo.cpp, ETC.) E UM NOME COMPLETO (PATH) (dir/dir/arquivo.java, POR EXEMPLO)
 * OS DEMAIS ATRIBUTOS DAS SUAS SUB-CLASSES DEVERÃO SER DO TIPO @Transient, POIS NÃO SERÃO PERSISTIDOS NO BANCO DE DADOS 
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="element_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Element implements PersistenceEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;
	
	private String name;
	private String fullname;
	
	@OneToMany(mappedBy = "element")
	private List<FeatureElement> featureElements;
	
	/**
	 * 
	 */
	public Element() {
	}
	
	/**
	 * @return the name 
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return fullname;
	}
	
	/**
	 * @param fullname the fullname to set
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	/**
	 * @return the formated name
	 */
	public String formatName(){
		return this.getName().substring(0, this.getName().indexOf(Constants.DOT));
	}
	
	/**
	 * @return the formated fullname
	 */
	public String formatFullname(){
		return this.getFullname().substring(0, this.getFullname().indexOf(Constants.DOT));
	}

	/**
	 * @return the featureElements
	 */
	public List<FeatureElement> getFeatureElements() {
		return featureElements;
	}

	/**
	 * @param featureElements the featureElements to set
	 */
	public void setFeatureElements(List<FeatureElement> featureElements) {
		this.featureElements = featureElements;
	}
}
