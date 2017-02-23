package br.ufpi.codivision.feature.java.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.core.util.Constants;

/**
 * @author Vanderson Moura
 *
 */
@Entity
public class Class implements PersistenceEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String fullname;
	
	@Transient private String content;
	@Transient private String packageName; 
	@Transient private List<Method> methods;
	@Transient private List<Attribute> attributes;
	@Transient private Class superClass;
	
	@OneToMany(mappedBy = "class_")
	private List<FeatureClass> featureClasses;
	
	/**
	 * 
	 */
	public Class() {
		this.methods = new ArrayList<Method>();
		this.superClass = null;
		this.attributes = new ArrayList<Attribute>();
	}
	
	/**
	 * @param fullname
	 * @param content
	 * @param packageName
	 */
	public Class(String fullname, String content, String packageName) {
		super();
		this.fullname = fullname;
		this.name = fullname.substring(fullname.lastIndexOf(Constants.FILE_SEPARATOR) + 1);
		this.content = content;
		this.packageName = packageName;
		this.methods = new ArrayList<Method>();
		this.superClass = null;
		this.attributes = new ArrayList<Attribute>();
	}
	
	@Override
	public Long getId() {
		return this.id;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;		
	}
	
	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}
	
	/**
	 * @return formated packageName
	 */
	public String formatPackageName(){
		return this.packageName.replace(Constants.DOT, Constants.FILE_SEPARATOR);
	}

	/**
	 * @param packageName the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
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
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * @return the methods
	 */
	public List<Method> getMethods() {
		return methods;
	}

	/**
	 * @param methods the methods to set
	 */
	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}

	/**
	 * @return the attributes 
	 */
	public List<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributs the attributs to set
	 */
	public void setAttributes(List<Attribute> attributs) {
		this.attributes = attributs;
	}

	/**
	 * @return the superClass
	 */
	public Class getSuperClass() {
		return superClass;
	}

	/**
	 * @param superClass the superClass to set
	 */
	public void setSuperClass(Class superClass) {
		this.superClass = superClass;
	}

	/**
	 * @return the formated name
	 */
	public String formatName(){
		return this.name.substring(0, name.indexOf(Constants.DOT));
	}
	
	/**
	 * @return the formated fullname
	 */
	public String formatFullname(){
		return this.fullname.substring(0, fullname.indexOf(Constants.DOT));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return formatName();
	}
}
