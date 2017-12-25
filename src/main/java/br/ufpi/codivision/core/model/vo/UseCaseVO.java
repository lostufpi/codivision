package br.ufpi.codivision.core.model.vo;

import java.io.Serializable;
import br.ufpi.codivision.feature.common.model.UseCase;

public class UseCaseVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private int totalFeatures;
	
	public UseCaseVO(UseCase useCase){
		this.id = useCase.getId();
		this.name = useCase.getName();
		this.totalFeatures = useCase.getFeatureUseCases().size();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTotalFeatures() {
		return totalFeatures;
	}

	public void setTotalFeatures(int totalFeatures) {
		this.totalFeatures = totalFeatures;
	}
}
