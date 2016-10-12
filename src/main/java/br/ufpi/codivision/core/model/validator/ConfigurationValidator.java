/**
 * 
 */
package br.ufpi.codivision.core.model.validator;

import java.util.Date;

import br.com.caelum.vraptor.validator.SimpleMessage;
import br.ufpi.codivision.common.validator.BaseValidator;
import br.ufpi.codivision.core.model.Configuration;
import br.ufpi.codivision.core.model.enums.TimeWindow;

/**
 * @author Werney Ayala
 *
 */
public class ConfigurationValidator extends BaseValidator{
	
	public void validate(Configuration configuration) {
		configuration.refreshTime();
		validator.check(configuration.getMonthlyDegradation() <= 100, new SimpleMessage("error", "repository.configuration.degradation.monthly.error"));
		
		if (configuration.getTimeWindow().equals(TimeWindow.EVER) && (configuration.getMonthlyDegradation() > 0)) {
			validator.add(new SimpleMessage("error", "repository.configuration.timewindow.ever.error"));
		} else if ((getDays(new Date(), configuration.getEndDate()) * configuration.getMonthlyDegradationPercentage()) > 1) {
			validator.add(new SimpleMessage("error", "repository.configuration.timewindow.error"));
		}
		
	}
	
	private long getDays(Date init, Date end) {
		return ((end.getTime() - init.getTime())/86400000);
	}

}
