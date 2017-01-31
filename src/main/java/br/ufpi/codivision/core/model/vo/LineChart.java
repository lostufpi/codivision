package br.ufpi.codivision.core.model.vo;

import java.io.Serializable;
import java.util.List;

public class LineChart implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String[] dataCategories;
	List<CommitHistory> dataSeries;
	
	public LineChart(){
		
	}
	
	public String[] getDataCategories() {
		return dataCategories;
	}
	public void setDataCategories(String[] dataCategories) {
		this.dataCategories = dataCategories;
	}
	public List<CommitHistory> getDataSeries() {
		return dataSeries;
	}
	public void setDataSeries(List<CommitHistory> dataSeries) {
		this.dataSeries = dataSeries;
	}
	
	

}
