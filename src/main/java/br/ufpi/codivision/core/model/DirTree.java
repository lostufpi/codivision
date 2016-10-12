/**
 * 
 */
package br.ufpi.codivision.core.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.core.model.enums.NodeType;

/**
 * @author Werney Ayala
 *
 */
@Entity
public class DirTree implements PersistenceEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String text;
	
	@Enumerated(EnumType.STRING)
	private NodeType type;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name= "parent")
	private Set<DirTree> children;
	
	public DirTree(){
		this.children = new HashSet<DirTree>();
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the nodes
	 */
	public Set<DirTree> getChildren() {
		return children;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setChildren(Set<DirTree> nodes) {
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
	public NodeType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(NodeType type) {
		this.type = type;
	}
	
}
