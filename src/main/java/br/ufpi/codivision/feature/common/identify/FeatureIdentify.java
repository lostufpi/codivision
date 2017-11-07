package br.ufpi.codivision.feature.common.identify;

import java.util.List;

import br.ufpi.codivision.feature.common.model.Feature;

/**
 * @author Vanderson Moura
 * 
 * TODA ENTIDADE QUE SE ENCARREGA DE RETORNAR AS FEATURES DE UM SISTEMA DEVE IMPLEMENTAR ESSA INTERFACE.
 *
 */
public interface FeatureIdentify {
	public List<Feature> featureIdentify();
}
