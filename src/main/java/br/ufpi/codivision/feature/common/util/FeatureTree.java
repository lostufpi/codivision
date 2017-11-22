package br.ufpi.codivision.feature.common.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import br.ufpi.codivision.core.model.DirTree;

public class FeatureTree implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String text;
	
	@Enumerated(EnumType.STRING)
	private FeatureNodeType type;
	
	private Set<FeatureTree> children;
	
	public FeatureTree(){
		this.children = new HashSet<FeatureTree>();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the nodes
	 */
	public Set<FeatureTree> getChildren() {
		return children;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setChildren(Set<FeatureTree> nodes) {
		this.children = nodes;
	}
	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public boolean equals(Object other){
		DirTree dirTree = (DirTree) other;
		return (this.text.equals(dirTree.getText()) ? true : false);
	}

	/**
	 * @return the type
	 */
	public FeatureNodeType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(FeatureNodeType type) {
		this.type = type;
	}
}
