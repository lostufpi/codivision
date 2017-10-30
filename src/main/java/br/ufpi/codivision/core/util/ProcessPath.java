package br.ufpi.codivision.core.util;

/**
 * 
 * @author Irvayne Matheus
 *
 */
public class ProcessPath {
	
	/**
	 * Receive path in the format src/main/.../.../name
	 * @param path
	 * @return
	 */
	public static String convertPathToName(String path) {
		String[] split = path.split("/");
		int lastIndice = split.length - 1;
		String name = split[lastIndice];
		return name;
	}
	
	public static String modifyNameMethod(String name) {
		String[] split = name.split("[(]");
		int lastIndice = split.length - 1;
		
		//nao possui parametros no methodos
		//pode retornar o valor name ex: calculaIdade()
		if(split[lastIndice].equals(")")) {
			return name;
		}else {
			//remove o ultimo ) que ficou e deixa apenas a lista de parametros separados por virgula
			String parameters = split[lastIndice].replaceAll("[)]", "");
			
			String[] split2 = parameters.split(",");
			
			//so possui 1 parametro
			if(split2.length == 1) {
				String[] split3 = split2[0].split("[.]");
				
				//FIXME
				return split[0] +"("+split3[split3.length - 1]+")".replaceAll(">", "");
			}else {
				String parametros = "";
				
				for (int i = 0; i < split2.length; i++) {
					String string = split2[i];
					String[] split3 = string.split("[.]");
					
					parametros = parametros + split3[split3.length - 1];
					
					if(i < split2.length - 1)
						parametros = parametros + ", ";
				}
				//FIXME
				return split[0] + "(" + parametros + ")".replaceAll(">", "");
				
			}
					
		}
		
	}
	
}
