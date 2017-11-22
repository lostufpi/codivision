package codivision.fuzzy;

import java.io.InputStream;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Rule;

public class TesteFile {

	public static void main(String[] args) {
		
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
		fis.setVariable("FAMILIARIDADE", 0.05);
		fis.setVariable("CORRELATO", 0.1);
		fis.setVariable("TOTAL", 0.06);
		
		fis.evaluate();
		
		double valor = fis.getVariable("GRP").getLatestDefuzzifiedValue();
		
		
		System.out.println("GRP = "+valor);
		
		for(Rule r : fis.getFunctionBlock("recommendation").getFuzzyRuleBlock("ConjuntoRegras").getRules())
			System.out.println(r);
	}
}
