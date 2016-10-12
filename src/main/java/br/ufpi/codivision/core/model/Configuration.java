/**
 * 
 */
package br.ufpi.codivision.core.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.core.model.enums.TimeWindow;

/**
 * @author Werney Ayala
 *
 */
@Entity
public class Configuration implements PersistenceEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Double addWeight;
	private Double modWeight;
	private Double delWeight;
	private Double conditionWeight;
	
	private Double monthlyDegradationPercentage;
	
	private Integer changeDegradation;
	private Integer monthlyDegradation;
	
	private Integer alertThreshold;
	private Integer existenceThreshold;
	private Integer truckFactorThreshold;
	
	@Temporal(TemporalType.DATE)
	private Date initDate;
	
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	@Enumerated(EnumType.STRING)
	private TimeWindow timeWindow;
	
	public void refreshTime(){
		if (timeWindow.equals(TimeWindow.LAST_MONTH)) {
			
			endDate = new Date();
			
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, -1);
			initDate = c.getTime();
					
		} else if (timeWindow.equals(TimeWindow.LAST_WEEK)) {
			
			endDate = new Date();
			
			Calendar c = Calendar.getInstance();
			c.add(Calendar.WEEK_OF_MONTH, -1);
			initDate = c.getTime();
			
		} else if(timeWindow.equals(TimeWindow.EVER)) {
			
			initDate = new Date(0l);
			
			endDate = new Date();
			
		} else if(timeWindow.equals(TimeWindow.LAST_SIX_MONTHS)) {
				
			endDate = new Date();
			
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, -6);
			initDate = c.getTime();
		}
	}

	/**
	 * @return the addWeight
	 */
	public Double getAddWeight() {
		return addWeight;
	}

	/**
	 * @param addWeight the addWeight to set
	 */
	public void setAddWeight(Double addWeight) {
		this.addWeight = addWeight;
	}

	/**
	 * @return the modWeight
	 */
	public Double getModWeight() {
		return modWeight;
	}

	/**
	 * @param modWeight the modWeight to set
	 */
	public void setModWeight(Double modWeight) {
		this.modWeight = modWeight;
	}

	/**
	 * @return the delWeight
	 */
	public Double getDelWeight() {
		return delWeight;
	}

	/**
	 * @param delWeight the delWeight to set
	 */
	public void setDelWeight(Double delWeight) {
		this.delWeight = delWeight;
	}

	/**
	 * @return the initDate
	 */
	public Date getInitDate() {
		return initDate;
	}

	/**
	 * @param initDate the initDate to set
	 */
	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the timeWindow
	 */
	public TimeWindow getTimeWindow() {
		return timeWindow;
	}

	/**
	 * @param timeWindow the timeWindow to set
	 */
	public void setTimeWindow(TimeWindow timeWindow) {
		this.timeWindow = timeWindow;
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
	 * @return the alertThreshold
	 */
	public Integer getAlertThreshold() {
		return alertThreshold;
	}

	/**
	 * @param alertThreshold the alertThreshold to set
	 */
	public void setAlertThreshold(Integer alertThreshold) {
		this.alertThreshold = alertThreshold;
	}

	/**
	 * @return the existenceThreshold
	 */
	public Integer getExistenceThreshold() {
		return existenceThreshold;
	}

	/**
	 * @param existenceThreshold the existenceThreshold to set
	 */
	public void setExistenceThreshold(Integer existenceThreshold) {
		this.existenceThreshold = existenceThreshold;
	}

	/**
	 * @return the truckFactorThreshold
	 */
	public Integer getTruckFactorThreshold() {
		return truckFactorThreshold;
	}

	/**
	 * @param truckFactorThreshold the truckFactorThreshold to set
	 */
	public void setTruckFactorThreshold(Integer truckFactorThreshold) {
		this.truckFactorThreshold = truckFactorThreshold;
	}

	/**
	 * @return the monthlyDegradationPercentage
	 */
	public Double getMonthlyDegradationPercentage() {
		return monthlyDegradationPercentage;
	}

	/**
	 * @param monthlyDegradationPercentage the monthlyDegradationPercentage to set
	 */
	public void setMonthlyDegradationPercentage(Double monthlyDegradationPercentage) {
		this.monthlyDegradationPercentage = monthlyDegradationPercentage;
	}

	/**
	 * @return the changeDegradation
	 */
	public Integer getChangeDegradation() {
		return changeDegradation;
	}

	/**
	 * @param changeDegradation the changeDegradation to set
	 */
	public void setChangeDegradation(Integer changeDegradation) {
		this.changeDegradation = changeDegradation;
	}

	/**
	 * @return the monthlyDegradation
	 */
	public Integer getMonthlyDegradation() {
		return monthlyDegradation;
	}

	/**
	 * @param monthlyDegradation the monthlyDegradation to set
	 */
	public void setMonthlyDegradation(Integer monthlyDegradation) {
		this.monthlyDegradation = monthlyDegradation;
		
		this.monthlyDegradationPercentage = (monthlyDegradation/30.0)/100.0;
	}

	/**
	 * @return the conditionWeight
	 */
	public Double getConditionWeight() {
		return conditionWeight;
	}

	/**
	 * @param conditionWeight the conditionWeight to set
	 */
	public void setConditionWeight(Double conditionWeight) {
		this.conditionWeight = conditionWeight;
	}

}
