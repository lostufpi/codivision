package br.ufpi.codivision.common;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
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

public class Main {

	public static void main(String[] args) {
		boolean v1, v2, v3, v4;
		int h, x, y, z;
		h = 1;
		x = 3;
		y = 2;
		z = 0;
		v4 = false;
		v1 = !(x - 1 == y) || (z - x / y <= z + x - h) && !(!v4);
		v2 = (z + y - h > x - y + h) || v4 && (x * 2 - y * 5 / h <= x * y / h + x) || !v4;
		v3 = (x * 3 + y / h + y != y * x / h + y) && !v4 || (z * y * 3 + h != z + y / h + 1);
		
		System.out.println(v1 + "," + v2 + "," + v3);
	
		
		
	}
	
	private static void gerarPorcentagens() {
		UseCaseDAO ucd = new UseCaseDAO();
		RepositoryDAO repositoryDAO = new RepositoryDAO();
		Map<String, List<Double>> map = new HashMap<String, List<Double>>();
		inicializarMap(map);
		List<String> names = new ArrayList<>();
		List<UseCase> useCases = ucd.useCasesOrderByName();
		List<AuthorPercentage> percentage = new ArrayList<>();
		
		for (UseCase useCase : useCases) {
			percentage = repositoryDAO.getUseCasePercentage(1L, useCase.getId());
			
			for (AuthorPercentage authorPercentage : percentage) {
				names.add(authorPercentage.getName());
				map.get(authorPercentage.getName()).add(authorPercentage.getY());
			}
			
			for (Entry<String, List<Double>> entry : map.entrySet()) {
				if(!names.contains(entry.getKey())) {
					map.get(entry.getKey()).add(-1D);
				}
			}
			names = new ArrayList<>();
		}
	}
	
	
	private static void inicializarMap(Map<String, List<Double>> map) {
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
	
	private static void gerarDocExcel(Map<String, List<Double>> map, List<UseCase> useCases) throws IOException, RowsExceededException, WriteException {
		WritableWorkbook wwb = Workbook.createWorkbook(new File(System.getProperty("java.io.tmpdir").concat(System.getProperty("file.separator")).concat("result.xlsx")));
		WritableSheet sheet = wwb.createSheet("Resultados", 0);
		Label l = new Label(0, 0, "Caso de Uso");
		sheet.addCell(l);
		int c = 1;
		
		for (Entry<String, List<Double>> entry : map.entrySet()) {
			l = new Label(c++, 0, entry.getKey());
		}
		
		int r = 1; 
		for (UseCase useCase : useCases) {
			l = new Label(0, r++, useCase.getName());
		}
		
		c = r = 1;
		Number n;
		for (Entry<String, List<Double>> entry : map.entrySet()) {
			for (Double value : entry.getValue()) {
				n = new Number(c, r++, value);
			}
			r = 1;
			c++;
		}
		wwb.write();
	}
	
	private static String formatName(String name) {
		System.out.println(name);
		String n1 = name.substring(0, name.indexOf(name.contains("MBean") ? "MBean" : "Mbean")).concat("#");
		int a;
		List<String> names = new ArrayList<>();
		
		a = 0;
		for (int i = 1; i < n1.toCharArray().length; i++) {
			if(String.valueOf(n1.charAt(i)).equals(String.valueOf(n1.charAt(i)).toUpperCase())) {
				names.add(name.substring(a, i));
				a = i;
			}
		}
		
		String nameFinal = "Gerenciar ";
		for (int i = 0; i < names.size(); i++) {
			nameFinal = nameFinal.concat(names.get(i));
			if(i < names.size()-1) {
				nameFinal = nameFinal.concat(" ");
			}
		}
		return nameFinal;
	}
}
