package br.ufpi.codivision.core.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.vo.AuthorPercentage;
import br.ufpi.codivision.debit.model.CodeSmell;
import br.ufpi.codivision.debit.model.CodeSmellMethod;
import br.ufpi.codivision.debit.model.File;
import br.ufpi.codivision.debit.model.Method;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.Variable;


public class Fuzzy {
	
	public static void calcula(Repository repository, File file, List<AuthorPercentage> percentage) {
		Map<String, Integer> td = new HashMap<>();
		
		Map<String, Map<String, Integer>> historicTDRemove = historicTDRemove(repository);
		
		System.out.println(file.getPath());

		if(file.getQntBadSmellComment() > 0)
			td.put("TDComment", 1);
		
		for (CodeSmell codeSmell : file.getCodeSmells()) {
			Integer integer = td.get(codeSmell.getCodeSmellType()+"");
			if(integer == null) {
				td.put(codeSmell.getCodeSmellType()+"", 1);
			}
		}
		
		for (Method method : file.getMethods()) {
			for (CodeSmellMethod codeSmellMethod : method.getCodeSmells()) {
				
				Integer integer = td.get(codeSmellMethod.getCodeSmellType()+"");
				if(integer == null) {
					td.put(codeSmellMethod.getCodeSmellType()+"", 1);
				}
			}
		}
		System.out.print("DTs do arquivo - ");
		for (String string3 : td.keySet()) {
			System.out.print(string3+" - ");
		}
		System.out.println();
		double mCorrelato = 0;
		double mTotal = 0;
		
		double total = 0;
		for (AuthorPercentage percentage2 : percentage) {
			total = total + percentage2.getY();
			
			Map<String, Integer> map2 = historicTDRemove.get(percentage2.getName());
			
			if(map2 != null) {
				Integer sumType = 0;
				for (String string : td.keySet()) {
					Integer integer = map2.get(string);
					if(integer != null)
						sumType = sumType + integer;
				}
				Integer sumTotal = 0;
				for (String string2 : map2.keySet()) {
					Integer integer = map2.get(string2);
					if(integer != null)
						sumTotal = sumTotal + integer;
				}
				
				if(sumTotal > mTotal)
					mTotal = sumTotal;
				
				if(sumType > mCorrelato)
					mCorrelato = sumType;
		}
		
		}
		
		for (AuthorPercentage authorPercentage : percentage) {
			Map<String, Integer> map = historicTDRemove.get(authorPercentage.getName());
			
			if(map != null) {
				double sumType = 0;
				for (String string : td.keySet()) {
					Integer integer = map.get(string);
					if(integer != null)
						sumType = sumType + integer;
				}
				double sumTotal = 0;
				for (String string2 : map.keySet()) {
					Integer integer = map.get(string2);
					if(integer != null)
						sumTotal = sumTotal + integer;
				}
				System.out.println("nome = "+authorPercentage.getName());
				System.out.println("familiaridade = "+(authorPercentage.getY()/total)*100+" %");
				System.out.println("correlato = "+sumType/mCorrelato);
				System.out.println("total ="+ sumTotal/mTotal);
				
				System.out.print("Dts pagas = ");
				for (String string3 : map.keySet()) {
					System.out.print(string3 +"  "+ map.get(string3)+" - ");
				}
				System.out.println();
				
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				InputStream input = classLoader.getResourceAsStream("recommendation.fcl");
				
				FIS fis = FIS.load(input, true);
				
				if(fis == null){
					System.err.println("Não foi possivel carregar o arquivo!!");
					return;
				}
				
				/**
				 * Seta as variáveis
				 */
				fis.setVariable("FAMILIARIDADE", (authorPercentage.getY()/total)*100);
				fis.setVariable("CORRELATO", sumType/mCorrelato);
				fis.setVariable("TOTAL", sumTotal/mTotal);
				
				fis.evaluate();
				
				double valor = fis.getVariable("GRP").getValue();
				
				
				System.out.println("GRP = "+valor);
				System.out.println();
				
			}
			
			
			
		}
		
	}
	
	
	
	public static Map<String, Map<String, Integer>> historicTDRemove(Repository repository) {
		
		List<Revision> revisions = repository.getRevisions();
		
		List<File> filesTD = new ArrayList<>(repository.getCodeSmallsFile());
		
		Map<String, Map<String, Integer>> hashMap = new HashMap<>();
		
		
		for(int i = 0; i < revisions.size(); i++) {
			Revision revision = revisions.get(i);
			
			if(i < revisions.size() - 1) {
				
				for(int j = 0; j < revisions.get(i+1).getCodeSmellsFileAlteration().size(); j++) {
					File file = revisions.get(i+1).getCodeSmellsFileAlteration().get(j);
					for(int k = 0; k < filesTD.size(); k++) {
						//file 2 é o atual
						File file2 = filesTD.get(k);
						if(file.getPath()!= null && file2.getPath()!=null && file.getPath().equals(file2.getPath())) {
							
							//verifica comments
							if(file2.getQntBadSmellComment() < file.getQntBadSmellComment()) {
								
								Map<String, Integer> map = hashMap.get(revision.getAuthor());
								
								Integer TDresolvido = (int) (file.getQntBadSmellComment() - file2.getQntBadSmellComment());
								
								if(map==null) {
									Map<String, Integer> maps = new HashMap<>();
									maps.put("TDComment", TDresolvido);
									hashMap.put(revision.getAuthor(), maps);
								}else {
									Integer integer = map.get("TDComment");
									if(integer==null) {
										hashMap.get(revision.getAuthor()).put("TDComment", TDresolvido);
									}else {
										hashMap.get(revision.getAuthor()).put("TDComment", integer + TDresolvido);
									}
								}
								
							}
							//code smell do arquivo 
							fora: for(CodeSmell codeSmell : file2.getCodeSmells()) {
								for (CodeSmell codeSmell2 : file.getCodeSmells()) {
									if(codeSmell.getCodeSmellType().equals(codeSmell2.getCodeSmellType())) {
										continue fora;
									}
								}
								//caso nao pare no continue de fora é pq nao existe mais o codesmall
								Map<String, Integer> map = hashMap.get(revision.getAuthor());
								if(map==null) {
									Map<String, Integer> maps = new HashMap<>();
									maps.put(codeSmell.getCodeSmellType()+"", 1);
									hashMap.put(revision.getAuthor(), maps);
								}else {
									Integer integer = map.get(codeSmell.getCodeSmellType()+"");
									if(integer==null) {
										hashMap.get(revision.getAuthor()).put(codeSmell.getCodeSmellType()+"", 1);
									}else {
										hashMap.get(revision.getAuthor()).put(codeSmell.getCodeSmellType()+"", integer + 1);
									}
								}
								
							}
							
							
							//methods
							Map<String, Integer> atual = new HashMap<>();
							Map<String, Integer> antigo = new HashMap<>();
							
							for (Method method : file2.getMethods()) {
								for (CodeSmellMethod codeSmellMethod : method.getCodeSmells()) {
									Integer integer = atual.get(codeSmellMethod.getCodeSmellType()+"");
									if(integer==null) {
										atual.put(codeSmellMethod.getCodeSmellType()+"", 1);
									}else {
										atual.put(codeSmellMethod.getCodeSmellType()+"", integer + 1);
									}
								}
							}
							
							
							for (Method method : file.getMethods()) {
								for (CodeSmellMethod codeSmellMethod : method.getCodeSmells()) {
									Integer integer = antigo.get(codeSmellMethod.getCodeSmellType()+"");
									if(integer==null) {
										antigo.put(codeSmellMethod.getCodeSmellType()+"", 1);
									}else {
										antigo.put(codeSmellMethod.getCodeSmellType()+"", integer + 1);
									}
								}
							}
							
							for (String key : antigo.keySet()) {
								Integer integer = atual.get(key);
								if(integer == null) {
									Map<String, Integer> map = hashMap.get(revision.getAuthor());
									if(map == null) {
										Map<String, Integer> maps = new HashMap<>();
										maps.put(key+"", antigo.get(key));
										hashMap.put(revision.getAuthor(), maps);
									}else {
										Integer integer2 = hashMap.get(revision.getAuthor()).get(key+"");
										if(integer2==null) {
											hashMap.get(revision.getAuthor()).put(key+"", antigo.get(key));
										}else {
											hashMap.get(revision.getAuthor()).put(key+"", antigo.get(key) + integer2);
										}
									}
								}else {
									Map<String, Integer> map = hashMap.get(revision.getAuthor());
									if(map == null) {
										Map<String, Integer> maps = new HashMap<>();
										maps.put(key+"", atual.get(key) - antigo.get(key));
										hashMap.put(revision.getAuthor(), maps);
									}else {
										Integer integer2 = hashMap.get(revision.getAuthor()).get(key+"");
										if(integer2==null) {
											hashMap.get(revision.getAuthor()).put(key+"", atual.get(key) - antigo.get(key));
										}else {
											hashMap.get(revision.getAuthor()).put(key+"", atual.get(key) - antigo.get(key) + integer2);
										}
									}
								}
							}
							
							//substitui
							filesTD.add(k, file);
							break;
						}
					}
					
				}
			}
		}
		return hashMap;
	}

}
