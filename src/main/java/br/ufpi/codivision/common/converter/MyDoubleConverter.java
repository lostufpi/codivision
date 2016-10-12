/**
 * 
 */
package br.ufpi.codivision.common.converter;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.converter.DoubleConverter;

/**
 * @author Werney Ayala
 *
 */
@Alternative
@Convert(Double.class)
@Priority(Interceptor.Priority.APPLICATION)
@SuppressWarnings("deprecation")
public class MyDoubleConverter extends DoubleConverter{
	
	@Override
	public Double convert(String value, Class<? extends Double> type) {
		return Double.parseDouble(value);
	}
}
