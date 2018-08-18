package br.ufpi.codivision.core.model;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import br.ufpi.codivision.common.model.PersistenceEntity;

@Entity
public class Author implements PersistenceEntity, Comparable<Author>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name; 
	
	private String email;
	
	private Long autFather;
	
	private int score;

	private int numberOfTestMethods;
	
	private int numberOfLinesCode;
	
	private int numberOfLinesTest;
	
	private int dmedal;
	
	private int gmedal;
	
	private int smedal;
	
	private int bmedal;
	
	public void resetGam() {
		this.score=0;
		this.numberOfTestMethods=0;
		this.numberOfLinesCode=0;
		this.numberOfLinesTest=0;
		this.dmedal=0;
		this.gmedal=0;
		this.smedal=0;
		this.bmedal=0;
	}
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="authorId")
	private List<GamePoints> gamePoints;
	
	public List<GamePoints> getGamePoints() {
		return this.gamePoints;
	}
	
	public GamePoints getLastGamePoint() {
		if (this.gamePoints != null) {
			return this.gamePoints.get(this.gamePoints.size()-1);
		}
		else return null;
	}

	/**
	 * @param revisions the revisions to set
	 */
	public void setGamePoints(List<GamePoints> gamePoints) {
		this.gamePoints = gamePoints;
	}
	public void addGamePoints(GamePoints gamePoint) {
		this.gamePoints.add(gamePoint);
	}
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = this.score + score;
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
		this.dmedal =this.dmedal + dmedal;
	}

	public int getGmedal() {
		return gmedal;
	}

	public void setGmedal(int gmedal) {
		this.gmedal =this.gmedal + gmedal;
	}

	public int getSmedal() {
		return smedal;
	}

	public void setSmedal(int smedal) {
		this.smedal =this.smedal + smedal;
	}

	public int getBmedal() {
		return bmedal;
	}

	public void setBmedal(int bmedal) {
		this.bmedal =this.bmedal + bmedal;
	}

	public Author() {
	}

	public Author(String name, String email) {
		super();
		this.gamePoints = new ArrayList<GamePoints>();
		this.name = name;
		this.email = email;
		this.autFather=null;
		this.score=0;
		this.numberOfTestMethods=0;
		this.numberOfLinesCode=0;
		this.numberOfLinesTest=0;
		this.dmedal=0;
		this.gmedal=0;
		this.smedal=0;
		this.bmedal=0;

	}

	public Long getAutFather() {
		return autFather;
	}

	public void setAutFather(Long autFather) {
		this.autFather = autFather;
	}


	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int compareTo(Author o) {
		if(this.score > o.getScore()) {
			return -1;
		}
		if (this.score < o.getScore()) {
			return 1;
		}
		if (this.dmedal > o.getDmedal()) {
			return -1;
		}
		if (this.dmedal < o.getDmedal()) {
			return 1;
		}
		if (this.gmedal > o.getDmedal()) {
			return -1;
		}
		if (this.gmedal < o.getDmedal()) {
			return 1;
		}
		if (this.smedal > o.getDmedal()) {
			return -1;
		}
		if (this.smedal < o.getDmedal()) {
			return 1;
		}
		if (this.bmedal > o.getDmedal()) {
			return -1;
		}
		if (this.bmedal < o.getDmedal()) {
			return 1;
		}
		return 0;
	}
}
