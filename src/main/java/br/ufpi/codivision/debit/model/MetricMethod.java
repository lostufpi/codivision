package br.ufpi.codivision.debit.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.debit.metric.MetricID;

@Entity
public class MetricMethod implements PersistenceEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private MetricID metricType;
	
	private String qnt;
	
	public MetricID getMetricType() {
		return metricType;
	}
	public void setMetricType(MetricID metricType) {
		this.metricType = metricType;
	}
	@Override
	public String toString() {
		return "<br>" + metricType + "=" + qnt;
	}
	public String getQnt() {
		return qnt;
	}
	public void setQnt(String qnt) {
		this.qnt = qnt;
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

}

