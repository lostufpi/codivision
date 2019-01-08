package br.ufpi.codivision.core.model;


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
	
	private int score = 0;
	
	private String lastContrib;
	
	private int numberOfTestMethods = 0;
	
	private int numberOfLinesCode = 0;
	
	private int numberOfLinesTest = 0;
	
	private int dmedal = 0;
	
	private int gmedal = 0;
	
	private int smedal = 0;
	
	private int bmedal = 0;
	
	private int days = 0;

	
	public GamePoints() {
		this.setDays(0);
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

	public int getNumberOfTestMethods() {
		return numberOfTestMethods;
	}

	public void addNumberOfTestMethods(int numberOfTestMethods) {
		this.numberOfTestMethods = numberOfTestMethods + this.numberOfTestMethods ;
	}

	public int getNumberOfLinesCode() {
		return numberOfLinesCode;
	}

	public void addNumberOfLinesCode(int numberOfLinesCode) {
		this.numberOfLinesCode = numberOfLinesCode + this.numberOfLinesCode;
	}

	public int getNumberOfLinesTest() {
		return numberOfLinesTest;
	}

	public void addNumberOfLinesTest(int numberOfLinesTest) {
		this.numberOfLinesTest = numberOfLinesTest + this.numberOfLinesTest;
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

	public int getDays() {
		return days;
	}
	
	public void countDays() {
		this.days += 1;
	}
	
	public void setDays(int days) {
		this.days = days;
	}


	

	
	
}