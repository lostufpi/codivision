package br.ufpi.codivision.core.model;


import java.util.ArrayList;
import java.util.List;

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
	
	private Long taskAtt;
	
	private Long msgTimer;
	
	private String dateInicial;
	
	private Long repId;

	public String getDateInicial() {
		return dateInicial;
	}

	public void setDateInicial(String dateInicial) {
		this.dateInicial = dateInicial;
	}

	public Long getRepId() {
		return repId;
	}

	public void setRepId(Long repId) {
		this.repId = repId;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub
		
	}
	public Gamification() {
		
	}
	
	public Gamification(Long cicle, Long taskAtt, Long msgTimer, String date, Long repId) {
		super();
		this.cicle = cicle;
		this.taskAtt = taskAtt;
		this.msgTimer=msgTimer;
		this.dateInicial=date;
		this.repId=repId;
	}
	
}