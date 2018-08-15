package br.ufpi.codivision.core.model;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.ufpi.codivision.common.model.PersistenceEntity;

@Entity
public class GamePoints implements PersistenceEntity{
	
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String date;
	
	private int score;
	
	private String lastContrib;
	
	private int numberOfLines;
	
	private int dmedal;
	
	private int gmedal;
	
	private int smedal;
	
	private int bmedal;
	
	public GamePoints(String date, int score, int numberOfLines, int dmedal, int gmedal, int smedal, int bmedal) {
		this.date=date;
		this.score=score;
		this.numberOfLines=numberOfLines;
		this.dmedal=dmedal;
		this.gmedal=gmedal;
		this.smedal=smedal;
		this.bmedal=bmedal;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getLastContrib() {
		return lastContrib;
	}

	public void setLastContrib(String lastContrib) {
		this.lastContrib = lastContrib;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public void setNumberOfLines(int numberOfLines) {
		this.numberOfLines = numberOfLines;
	}

	public int getDmedal() {
		return dmedal;
	}

	public void setDmedal(int dmedal) {
		this.dmedal = dmedal;
	}

	public int getGmedal() {
		return gmedal;
	}

	public void setGmedal(int gmedal) {
		this.gmedal = gmedal;
	}

	public int getSmedal() {
		return smedal;
	}

	public void setSmedal(int smedal) {
		this.smedal = smedal;
	}

	public int getBmedal() {
		return bmedal;
	}

	public void setBmedal(int bmedal) {
		this.bmedal = bmedal;
	}


	

	
	
}