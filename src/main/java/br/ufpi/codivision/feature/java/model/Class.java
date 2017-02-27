package br.ufpi.codivision.feature.java.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import br.ufpi.codivision.core.util.Constants;
import br.ufpi.codivision.feature.common.model.Element;

/**
 * @author Vanderson Moura
 *
 */
@Entity
@DiscriminatorValue("JAVA")
public class Class extends Element{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Transient private String content;
	@Transient private String packageName; 
	@Transient private List<Method> methods;
	@Transient private List<Attribute> attributes;
	@Transient private Class superClass;
	
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
		this.setFullname(fullname);
		this.setName(fullname.substring(fullname.lastIndexOf(Constants.FILE_SEPARATOR) + 1));
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return formatName();
	}
}
