package br.ufpi.codivision.feature.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpi.codivision.feature.common.model.UseCase;

public class ResultData {
	private Map<String, List<Double>> map;
	private List<UseCase> useCases; 
	private List<Integer> TruckFactorList;
	private List<String> dominators;
	
	public ResultData() {
		this.map = new HashMap<String, List<Double>>();
		this.useCases = new ArrayList<>();
		this.TruckFactorList = new ArrayList<>();
		this.dominators = new ArrayList<>();
	}

	public Map<String, List<Double>> getMap() {
		return map;
	}

	public void setMap(Map<String, List<Double>> map) {
		this.map = map;
	}

	public List<UseCase> getUseCases() {
		return useCases;
	}

	public void setUseCases(List<UseCase> useCases) {
		this.useCases = useCases;
	}

	public List<Integer> getTruckFactorList() {
		return TruckFactorList;
	}

	public void setTruckFactorList(List<Integer> truckFactorList) {
		TruckFactorList = truckFactorList;
	}

	public List<String> getDominators() {
		return dominators;
	}

	public void setDominators(List<String> dominators) {
		this.dominators = dominators;
	}
}
