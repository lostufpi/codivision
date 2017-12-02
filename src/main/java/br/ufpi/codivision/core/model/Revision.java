/**
 * 
 */
package br.ufpi.codivision.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.debit.model.File;

/**
 * @author Werney Ayala
 *
 */
@Entity
public class Revision implements PersistenceEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String externalId;
	
	@ManyToOne (targetEntity = Author.class)
	private Author author;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	private int totalFiles;
	
	private boolean extracted;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="revisionId")
	private List<OperationFile> files;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="revisionId")
	private List<File> codeSmellsFileAlteration;
	
	public Revision() { 
		this.codeSmellsFileAlteration = new ArrayList<>();
	}
	
	/**
	 * @return the author
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the files
	 */
	public List<OperationFile> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<OperationFile> files) {
		this.files = files;
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
	 * @return the totalFiles
	 */
	public int getTotalFiles() {
		return totalFiles;
	}

	/**
	 * @param totalFiles the totalFiles to set
	 */
	public void setTotalFiles(int totalFiles) {
		this.totalFiles = totalFiles;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * @return the extracted
	 */
	public boolean isExtracted() {
		return extracted;
	}

	/**
	 * @param extracted the extracted to set
	 */
	public void setExtracted(boolean extracted) {
		this.extracted = extracted;
	}

	public List<File> getCodeSmellsFileAlteration() {
		return codeSmellsFileAlteration;
	}

	public void setCodeSmellsFileAlteration(List<File> codeSmellsFileAlteration) {
		this.codeSmellsFileAlteration = codeSmellsFileAlteration;
	}

}
