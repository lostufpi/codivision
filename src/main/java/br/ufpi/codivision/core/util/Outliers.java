/**
 * 
 */
package br.ufpi.codivision.core.util;

import java.util.List;

/**
 * @author Werney Ayala
 *
 */
public class Outliers {
	
	public static double indentify(List<Integer> values){
		
		if((values.isEmpty()) || (values == null))
			return Double.MAX_VALUE;
		
		int medianPoint, medianPointA, medianPointB;
		double valueA, valueB;
		
		if((values.size() % 2) == 0){
			medianPoint = values.size() / 2;
			medianPointA = medianPoint / 2;
			medianPointB = medianPoint + medianPointA;
			valueA = (values.get(medianPointA) + values.get(medianPointA - 1)) / 2;
			valueB = (values.get(medianPointB) + values.get(medianPointB - 1)) / 2;
		} else {
			medianPoint = (values.size() + 1) / 2;
			medianPointA = medianPoint / 2;
			medianPointB = medianPoint + medianPointA;
			valueA = values.get(medianPointA);
			valueB = values.get(medianPointB - 1);
		}
		
		double irq = valueB - valueA;
		
		return (valueB + (irq * 1.5));
		
	}
	
}
