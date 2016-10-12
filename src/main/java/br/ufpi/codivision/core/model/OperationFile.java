/**
 * 
 */
package br.ufpi.codivision.core.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.core.model.enums.OperationType;

/**
 * @author Werney Ayala
 *
 */
@Entity
public class OperationFile implements PersistenceEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private OperationType operationType;
	
	private String path;
	
	private String copyPath;
	
	private Long copyRevision;
	
	private int lineAdd;
	
	private int lineMod;
	
	private int lineDel;
	
	private int lineCondition;
	
	private int linesNumber;
	
	private boolean extracted; 
	
	/**
	 * 
	 */
	public OperationFile() {	}
	
	public OperationFile(char oType, String path, String copyPath, Long copyRevision){
		this.path = path;
		this.copyPath = copyPath;
		this.copyRevision = copyRevision;
		
		if(oType == 'A')
			this.operationType = OperationType.ADD;
		else if(oType == 'M')
			this.operationType = OperationType.MOD;
		else if(oType == 'D'){
			this.operationType = OperationType.DEL;
			this.extracted = true;
		}
	}
	
	public OperationFile (String oType, String path) {
		this.path = path;
		
		if (oType.equals("added")) {
			this.operationType = OperationType.ADD;
		} else if (oType.equals("modified")) {
			this.operationType = OperationType.MOD;
		} else if (oType.equals("renamed")) {
			
		} else if (oType.equals("removed")) {
			this.operationType = OperationType.DEL;
			this.extracted = true;
		}
		
	}
	
	/**
	 * @return the oType
	 */
	public OperationType getOperationType() {
		return operationType;
	}
	
	/**
	 * @param oType the oType to set
	 */
	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the copyPath
	 */
	public String getCopyPath() {
		return copyPath;
	}

	/**
	 * @param copyPath the copyPath to set
	 */
	public void setCopyPath(String copyPath) {
		this.copyPath = copyPath;
	}

	/**
	 * @return the copyRevision
	 */
	public Long getCopyRevision() {
		return copyRevision;
	}

	/**
	 * @param copyRevision the copyRevision to set
	 */
	public void setCopyRevision(Long copyRevision) {
		this.copyRevision = copyRevision;
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
	 * @return the lineAdd
	 */
	public int getLineAdd() {
		return lineAdd;
	}

	/**
	 * @param lineAdd the lineAdd to set
	 */
	public void setLineAdd(int lineAdd) {
		this.lineAdd = lineAdd;
	}

	/**
	 * @return the lineMod
	 */
	public int getLineMod() {
		return lineMod;
	}

	/**
	 * @param lineMod the lineMod to set
	 */
	public void setLineMod(int lineMod) {
		this.lineMod = lineMod;
	}

	/**
	 * @return the lineDel
	 */
	public int getLineDel() {
		return lineDel;
	}

	/**
	 * @param lineDel the lineDel to set
	 */
	public void setLineDel(int lineDel) {
		this.lineDel = lineDel;
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

	/**
	 * @return the linesNumber
	 */
	public int getLinesNumber() {
		return linesNumber;
	}

	/**
	 * @param linesNumber the linesNumber to set
	 */
	public void setLinesNumber(int linesNumber) {
		this.linesNumber = linesNumber;
	}

	/**
	 * @return the lineCondition
	 */
	public int getLineCondition() {
		return lineCondition;
	}

	/**
	 * @param lineCondition the lineCondition to set
	 */
	public void setLineCondition(int lineCondition) {
		this.lineCondition = lineCondition;
	}

}
