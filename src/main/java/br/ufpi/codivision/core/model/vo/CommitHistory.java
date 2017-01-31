package br.ufpi.codivision.core.model.vo;

import java.io.Serializable;

public class CommitHistory implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private long[] data;
	
	public CommitHistory(){
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long[] getData() {
		return data;
	}
	public void setData(long[] data) {
		this.data = data;
	}
	
	
}
