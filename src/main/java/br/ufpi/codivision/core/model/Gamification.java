package br.ufpi.codivision.core.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.ufpi.codivision.common.model.PersistenceEntity;

@Entity
public class Gamification implements PersistenceEntity{
	
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Long cicle;
	
	private Long awardsAtt;
	
	private Long taskAtt;
	
	private Long msgTimer;
	
	private Date dateInicial;
	
	private boolean isActive;
	

	public Date getDateInicial() {
		return dateInicial;
	}

	public void setDateInicial(Date dateInicial) {
		this.dateInicial = dateInicial;
	}

	public Long getCicle() {
		return cicle;
	}

	public void setCicle(Long cicle) {
		this.cicle = cicle;
	}

	public Long getTaskAtt() {
		return taskAtt;
	}

	public void setTaskAtt(Long taskAtt) {
		this.taskAtt = taskAtt;
	}

	public Long getMsgTimer() {
		return msgTimer;
	}

	public void setMsgTimer(Long msgTimer) {
		this.msgTimer = msgTimer;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id=id;
		
	}
	public Gamification() {
		
	}
	
	public Gamification(Long cicle, Long taskAtt, Long msgTimer, Date date) {
		super();
		this.cicle = cicle;
		this.taskAtt = taskAtt;
		this.msgTimer=msgTimer;
		this.dateInicial=date;
		this.setActive(true);
	}

	public Long getAwardsAtt() {
		return awardsAtt;
	}

	public void setAwardsAtt(Long awardsAtt) {
		this.awardsAtt = awardsAtt;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
}