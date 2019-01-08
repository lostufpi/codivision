package br.ufpi.codivision.feature.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.model.vo.AuthorPercentage;
import br.ufpi.codivision.feature.common.model.UseCase;
import br.ufpi.codivision.feature.dao.UseCaseDAO;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class BuildResultUtil {
	
	public static void generateResultInFileExcel(UseCaseDAO ucd, RepositoryDAO rd, Long repositoryId) {
		Map<String, List<Double>> map = new HashMap<String, List<Double>>();
		inicializarMapSigaa(map);
		List<String> names = new ArrayList<>();
		ResultData resultData = new ResultData();
		resultData.setUseCases(ucd.useCasesOrderByName());
		List<AuthorPercentage> percentage = new ArrayList<>();
		
		for (UseCase useCase : resultData.getUseCases()) {
			percentage = rd.getUseCasePercentage(repositoryId, useCase.getId());
			Double total = calcularTotal(percentage);
			resultData.getTruckFactorList().add(computeTruckFactor(percentage, total, resultData));
			
			for (AuthorPercentage authorPercentage : percentage) {
				names.add(authorPercentage.getName());
				map.get(authorPercentage.getName()).add(authorPercentage.getY()/total);
			}
			
			for (Entry<String, List<Double>> entry : map.entrySet()) {
				if(!names.contains(entry.getKey())) {
					map.get(entry.getKey()).add(-1D);
				}
			}
			names = new ArrayList<>();
		}
		
		resultData.setMap(map);
		try {
			gerarDocExcel(resultData);
		} catch (WriteException | IOException e) {
			e.printStackTrace();
		}
	}
	private static Double calcularTotal(List<AuthorPercentage> ap) {
		Double total = 0D;
		for (AuthorPercentage authorPercentage : ap) {
			total += authorPercentage.getY();
		}
		return total;
	}
	
	private static Integer computeTruckFactor(List<AuthorPercentage> percentage, Double total, ResultData rd){
		GenericComparator.sortList(percentage, "y", SortType.DESC);
		Double truckFactorSum = 0D;
		Double tf = 0.7D;
		
		for (int i = 0; i < percentage.size(); i++) {
			truckFactorSum += percentage.get(i).getY()/total;
			if(truckFactorSum > tf) {
				String dominators = new String();
				for (int j = 0; j < i+1; j++) {
					dominators = dominators.concat(percentage.get(j).getName());
					if(j+1 < i+1) {
						dominators = dominators.concat(",");
					}
				}
				rd.getDominators().add(dominators);
				return i+1;
			}
		}
		rd.getDominators().add("");
		return 0;
	}
	
	private static void gerarDocExcel(ResultData resultData) throws IOException, RowsExceededException, WriteException {
		WritableWorkbook wwb = Workbook.createWorkbook(new File("C:\\Users\\Vanderson\\Documents\\result_ar.xls"));
		WritableSheet sheet = wwb.createSheet("Resultados", 0);
		Label l = new Label(0, 0, "Caso de Uso");
		sheet.addCell(l);
		int c = 1;
		
		for (Entry<String, List<Double>> entry : resultData.getMap().entrySet()) {
			l = new Label(c++, 0, entry.getKey());
			sheet.addCell(l);
		}
		
		int r = 1; 
		Number n;

		l = new Label(c, 0, "Truck Factor");
		sheet.addCell(l);
		
		for (Integer tf : resultData.getTruckFactorList()) {
			n = new Number(c, r++, Double.valueOf(String.valueOf(tf)));
			sheet.addCell(n);
		}
		r = 1;
		
		l = new Label(++c, 0, "Especialistas");
		sheet.addCell(l);
		
		for (String dominator : resultData.getDominators()) {
			l = new Label(c, r++, dominator);
			sheet.addCell(l);
		}
		r = 1;
		
		for (UseCase useCase : resultData.getUseCases()) {
			l = new Label(0, r++, useCase.getName());
			sheet.addCell(l);
		}
		
		c = r = 1;
		for (Entry<String, List<Double>> entry : resultData.getMap().entrySet()) {
			for (Double value : entry.getValue()) {
				n = new Number (c, r++, value);
				sheet.addCell(n);
			}
			r = 1;
			c++;
		}
		wwb.write();
		wwb.close();
	}
	
	private static void inicializarMapAr(Map<String, List<Double>> map) {
		map.put("ely.miranda", new ArrayList<Double>());
		map.put("Vinicius", new ArrayList<Double>());
		map.put("Saulo de Társio", new ArrayList<Double>());
		map.put("Valney Gama", new ArrayList<Double>());
		map.put("Paulo Sergio Queiroz", new ArrayList<Double>());
	}
	
	private static void inicializarMapSigaa(Map<String, List<Double>> map) {
		map.put("Cledjan", new ArrayList<Double>());
		map.put("Danniel", new ArrayList<Double>());
		map.put("Euclydes Melo", new ArrayList<Double>());
		map.put("Francisco", new ArrayList<Double>());
		map.put("Joelson Oliveira", new ArrayList<Double>());
		map.put("Kannya Leal", new ArrayList<Double>());
		map.put("Marcos Raniere", new ArrayList<Double>());
		map.put("Matheus", new ArrayList<Double>());
		map.put("mauriliojr21", new ArrayList<Double>());
		map.put("Pacheco", new ArrayList<Double>());
		map.put("Taison", new ArrayList<Double>());
	}
	
	private static void inicializarMapSerca(Map<String, List<Double>> map) {
		map.put("Vanderson", new ArrayList<Double>());
		map.put("Renato Felix", new ArrayList<Double>());
		map.put("zilma.felix", new ArrayList<Double>());
		map.put("Otavio Cury", new ArrayList<Double>());
		map.put("irvayne", new ArrayList<Double>());
		map.put("danilo", new ArrayList<Double>());
		map.put("Leonardo Costa", new ArrayList<Double>());
		map.put("wellington", new ArrayList<Double>());
		map.put("Gleison Andrade", new ArrayList<Double>());
	}
}
